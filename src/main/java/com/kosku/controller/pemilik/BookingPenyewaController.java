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

    @FXML private TableView<Booking> tabelBooking;
    @FXML private TableColumn<Booking, Integer> colId;
    @FXML private TableColumn<Booking, String> colPenyewa;
    @FXML private TableColumn<Booking, String> colProperti;
    @FXML private TableColumn<Booking, String> colCheckin;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, Void> colAksi;

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
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBooking() != null ? data.getValue().getIdBooking() : 0).asObject());
        colPenyewa.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getPenyewa().getUsername());
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colProperti.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getKamar().getKos().getNamaKos() + " - Kamar " + data.getValue().getKamar().getNomorKamar());
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colCheckin.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getTanggalMulai() != null ? data.getValue().getTanggalMulai().toString() : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colStatus.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getStatusBooking() != null ? data.getValue().getStatusBooking().name() : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnChat = new Button("Chat Penyewa");

            {
                btnChat.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnChat.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking != null && booking.getPenyewa() != null) {
                        ChatPemilikController.targetPenyewaChat = booking.getPenyewa();
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
        tabelBooking.setItems(FXCollections.observableArrayList(allBookings));

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
            btnLihatSekarang.setOnAction(e -> filterTableByStatus(Booking.StatusBooking.PENDING));
        } else {
            boxWarning.setVisible(false);
            boxWarning.setManaged(false);
        }
    }

    private void filterTableByStatus(Booking.StatusBooking status) {
        if (allBookings == null) return;
        List<Booking> filtered = allBookings.stream()
                .filter(b -> b.getStatusBooking() == status)
                .collect(Collectors.toList());
        tabelBooking.setItems(FXCollections.observableArrayList(filtered));
    }
}
