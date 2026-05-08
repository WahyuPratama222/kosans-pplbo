package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.math.BigDecimal;
import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.model.Kos;
import com.kosku.util.SessionManager;

public class RiwayatController implements Initializable {

    @FXML private NavbarController navbarController;
    @FXML private TextField tfCariRiwayat;
    @FXML private VBox vboxRiwayatBooking;

    private BookingDAO bookingDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Riwayat Penyewa berhasil dimuat!");
        bookingDAO = new BookingDAO();
        
        if (navbarController != null) {
            navbarController.setHighlight("riwayat");
        }
        
        loadRiwayatBooking();
    }

    private void loadRiwayatBooking() {
        if (vboxRiwayatBooking == null) return;
        vboxRiwayatBooking.getChildren().clear();
        Integer userId = SessionManager.getCurrentUserId();
        
        if (userId == null) {
            Label lblInfo = new Label("Anda harus login untuk melihat riwayat.");
            vboxRiwayatBooking.getChildren().add(lblInfo);
            return;
        }

        try {
            List<Booking> bookings = bookingDAO.getBookingByPenyewa(userId);
            
            if (bookings == null || bookings.isEmpty()) {
                Label lblEmpty = new Label("Belum ada riwayat booking.");
                lblEmpty.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
                vboxRiwayatBooking.getChildren().add(lblEmpty);
                return;
            }

            for (Booking booking : bookings) {
                HBox card = createBookingCard(booking);
                vboxRiwayatBooking.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Error loading booking: " + e.getMessage());
        }
    }

    private HBox createBookingCard(Booking booking) {
        Kos kos = booking.getKamar().getKos();
        
        HBox card = new HBox();
        card.setSpacing(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 12, 0, 0, 3);");
        
        // Image Area
        AnchorPane imagePane = new AnchorPane();
        imagePane.setPrefSize(300, 200);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(235);
        imageView.setPreserveRatio(false);
        
        String imagePath = kos.getFotoKos() != null ? "/" + kos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if(imageUrl != null) imageView.setImage(new Image(imageUrl.toExternalForm()));
        } catch (Exception ignored) {}
        
        AnchorPane.setTopAnchor(imageView, 0.0);
        AnchorPane.setBottomAnchor(imageView, 0.0);
        AnchorPane.setLeftAnchor(imageView, 0.0);
        AnchorPane.setRightAnchor(imageView, 0.0);

        Label lblStatus = new Label(booking.getStatusBooking().name());
        String statusColor = booking.getStatusBooking() == Booking.StatusBooking.DITERIMA ? "#16A34A" : 
                            (booking.getStatusBooking() == Booking.StatusBooking.PENDING ? "#F59E0B" : "#DC2626");
        lblStatus.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20;");
        AnchorPane.setTopAnchor(lblStatus, 14.0);
        AnchorPane.setLeftAnchor(lblStatus, 14.0);
        
        imagePane.getChildren().addAll(imageView, lblStatus);

        // Details Area
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(12);
        detailsBox.setStyle("-fx-padding: 24 32 24 32;");
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label lblNama = new Label(kos.getNamaKos());
        lblNama.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1A2744;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label lblTipe = new Label("Tipe: " + kos.getTipeKos().name());
        lblTipe.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");
        titleBox.getChildren().addAll(lblNama, spacer, lblTipe);

        Label lblAlamat = new Label("📍 " + kos.getAlamat());
        lblAlamat.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

        Label lblKamar = new Label("Kamar: " + booking.getKamar().getNomorKamar());
        lblKamar.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        Label lblTanggal = new Label("Periode: " + booking.getTanggalMulai() + " s/d " + booking.getTanggalSelesai());
        lblTanggal.setStyle("-fx-font-size: 12px; -fx-text-fill: #BBB;");

        HBox priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER_LEFT);
        priceBox.setSpacing(12);
        VBox priceText = new VBox(2);
        Label lblTotal = new Label("Total Harga");
        lblTotal.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        Label lblHargaVal = new Label("Rp " + String.format("%,.0f", booking.getTotalHarga()).replace(",", "."));
        lblHargaVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D6BE4;");
        priceText.getChildren().addAll(lblTotal, lblHargaVal);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Button btnDetail = new Button("Lihat Detail");
        btnDetail.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
        btnDetail.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detail Booking");
            alert.setHeaderText("Informasi Booking");
            alert.setContentText("ID Booking: " + booking.getIdBooking() + "\nStatus: " + booking.getStatusBooking());
            alert.showAndWait();
        });

        priceBox.getChildren().addAll(priceText, spacer2, btnDetail);
        detailsBox.getChildren().addAll(titleBox, lblAlamat, lblKamar, lblTanggal, priceBox);
        card.getChildren().addAll(imagePane, detailsBox);

        return card;
    }
}
