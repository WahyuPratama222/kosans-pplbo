package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.util.SessionManager;

public class BookingPenyewaController implements Initializable {

    @FXML
    private NavbarPemilikController navbarController;

    @FXML private TableView<BookingDTO> tabelBooking;
    @FXML private TableColumn<BookingDTO, Integer> colId;
    @FXML private TableColumn<BookingDTO, String> colPenyewa;
    @FXML private TableColumn<BookingDTO, String> colProperti;
    @FXML private TableColumn<BookingDTO, String> colCheckin;
    @FXML private TableColumn<BookingDTO, String> colStatus;
    @FXML private TableColumn<BookingDTO, Void> colAksi;

    @FXML private Label lblTotalBooking;
    @FXML private Label lblMenunggu;
    @FXML private Label lblAktif;

    @FXML private HBox boxWarning;
    @FXML private Label lblWarningTitle;
    @FXML private Button btnLihatSekarang;

    private BookingDAO bookingDAO;
    private List<Booking> allBookings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (navbarController != null) {
            navbarController.setActivePage("booking");
        }
        bookingDAO = new BookingDAO();
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().idBooking).asObject());
        colPenyewa.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().penyewaName));
        colProperti.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().propertiName));
        colCheckin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().checkinDate));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status));
        
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnChat = new Button("Chat Penyewa");

            {
                btnChat.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnChat.setOnAction(event -> {
                    BookingDTO dto = getTableView().getItems().get(getIndex());
                    if (dto != null && dto.originalPenyewa != null) {
                        ChatPemilikController.targetPenyewaChat = dto.originalPenyewa;
                        com.kosku.Main.navigateTo("/view/Pemilik/ChatPemilik.fxml", "KosKu - Chat Penyewa");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnChat);
                }
            }
        });
    }

    private void loadData() {
        Integer pemilikId = SessionManager.getCurrentUserId();
        if (pemilikId == null) return;

        allBookings = bookingDAO.getBookingByPemilik(pemilikId);
        List<BookingDTO> dtos = allBookings.stream().map(BookingDTO::new).collect(Collectors.toList());
        tabelBooking.setItems(FXCollections.observableArrayList(dtos));

        int total = allBookings.size();
        long pending = allBookings.stream().filter(b -> b.getStatusBooking() == Booking.StatusBooking.PENDING).count();
        long aktif = allBookings.stream().filter(b -> b.getStatusBooking() == Booking.StatusBooking.DITERIMA).count();

        lblTotalBooking.setText(String.valueOf(total));
        lblMenunggu.setText(String.valueOf(pending));
        lblAktif.setText(String.valueOf(aktif));

        if (pending > 0) {
            boxWarning.setVisible(true);
            boxWarning.setManaged(true);
            lblWarningTitle.setText("Ada " + pending + " booking yang menunggu konfirmasi Anda");
            btnLihatSekarang.setOnAction(e -> {
                com.kosku.Main.navigateTo("view/Pemilik/KonfirmasiBooking.fxml");
            });
        } else {
            boxWarning.setVisible(false);
            boxWarning.setManaged(false);
        }
    }

    private void filterTableByStatus(Booking.StatusBooking status) {
        if (allBookings == null) return;
        List<BookingDTO> filtered = allBookings.stream()
                .filter(b -> b.getStatusBooking() == status)
                .map(BookingDTO::new)
                .collect(Collectors.toList());
        tabelBooking.setItems(FXCollections.observableArrayList(filtered));
    }

    public static class BookingDTO {
        public final int idBooking;
        public final String penyewaName;
        public final String propertiName;
        public final String checkinDate;
        public final String status;
        public final com.kosku.model.User originalPenyewa;

        public BookingDTO(Booking b) {
            this.idBooking = b.getIdBooking() != null ? b.getIdBooking() : 0;
            this.originalPenyewa = b.getPenyewa();
            
            String pName = "Unknown";
            try {
                if (b.getPenyewa() != null) pName = b.getPenyewa().getUsername();
            } catch (Exception e) {}
            this.penyewaName = pName;

            String prName = "Unknown";
            try {
                if (b.getKamar() != null && b.getKamar().getKos() != null) {
                    prName = b.getKamar().getKos().getNamaKos() + " - Kamar " + b.getKamar().getNomorKamar();
                }
            } catch (Exception e) {}
            this.propertiName = prName;

            this.checkinDate = b.getTanggalMulai() != null ? b.getTanggalMulai().toString() : "Unknown";
            this.status = b.getStatusBooking() != null ? b.getStatusBooking().name() : "Unknown";
        }
    }
}
