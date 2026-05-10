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
    @FXML private Button btnPending;
    @FXML private Button btnSelesai;

    private String currentTab = "PENDING";
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

            int count = 0;
            boolean isPendingTab = "PENDING".equals(currentTab);
            
            for (Booking booking : bookings) {
                Booking.StatusBooking status = booking.getStatusBooking();
                
                boolean show = false;
                if (isPendingTab) {
                    show = (status == Booking.StatusBooking.PENDING || status == Booking.StatusBooking.DITERIMA);
                } else {
                    show = (status == Booking.StatusBooking.SELESAI || status == Booking.StatusBooking.DITOLAK || status == Booking.StatusBooking.DIBATALKAN);
                }

                if (show) {
                    HBox card = createBookingCard(booking);
                    vboxRiwayatBooking.getChildren().add(card);
                    count++;
                }
            }
            
            if (count == 0) {
                Label lblEmpty = new Label(isPendingTab ? "Tidak ada booking yang sedang berjalan/menunggu pembayaran." : "Belum ada riwayat booking yang selesai/dibatalkan.");
                lblEmpty.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
                vboxRiwayatBooking.getChildren().add(lblEmpty);
            }
        } catch (Exception e) {
            System.err.println("Error loading booking: " + e.getMessage());
        }
    }

    @FXML
    private void showPending() {
        currentTab = "PENDING";
        updateTabStyles();
        loadRiwayatBooking();
    }

    @FXML
    private void showSelesai() {
        currentTab = "SELESAI";
        updateTabStyles();
        loadRiwayatBooking();
    }

    private void updateTabStyles() {
        String activeStyle = "-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 24; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: white; -fx-text-fill: #555; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 24; -fx-border-color: #DDD; -fx-border-radius: 24; -fx-cursor: hand;";

        if ("PENDING".equals(currentTab)) {
            if (btnPending != null) btnPending.setStyle(activeStyle);
            if (btnSelesai != null) btnSelesai.setStyle(inactiveStyle);
        } else {
            if (btnPending != null) btnPending.setStyle(inactiveStyle);
            if (btnSelesai != null) btnSelesai.setStyle(activeStyle);
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

        detailsBox.getChildren().addAll(titleBox, lblAlamat, lblKamar, lblTanggal);

        if (booking.getStatusBooking() == Booking.StatusBooking.DITOLAK && booking.getAlasanTolak() != null) {
            Label lblAlasan = new Label("Alasan Penolakan: " + booking.getAlasanTolak());
            lblAlasan.setStyle("-fx-font-size: 13px; -fx-text-fill: #DC2626; -fx-font-style: italic; -fx-padding: 8; -fx-background-color: #FEF2F2; -fx-background-radius: 6;");
            lblAlasan.setWrapText(true);
            detailsBox.getChildren().add(lblAlasan);
        }

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
        
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        if (booking.getStatusBooking() == Booking.StatusBooking.PENDING || booking.getStatusBooking() == Booking.StatusBooking.DITERIMA) {
            Button btnTanya = new Button("Tanya Pemilik");
            btnTanya.setStyle("-fx-background-color: white; -fx-text-fill: #2D6BE4; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-color: #2D6BE4; -fx-border-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
            btnTanya.setOnAction(e -> {
                if (kos != null && kos.getPemilik() != null) {
                    ChatController.targetPemilikChat = kos.getPemilik();
                }
                com.kosku.Main.navigateTo("/view/penyewa/ChatPenyewa.fxml", "KosKu - Chat");
            });

            Button btnBayar = new Button("Lanjut ke Pembayaran");
            btnBayar.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
            btnBayar.setOnAction(e -> {
                DetailBookingController.selectedBooking = booking;
                com.kosku.Main.navigateTo("/view/penyewa/DetailBookingPenyewa.fxml", "Detail Booking");
            });

            actionBox.getChildren().addAll(btnTanya, btnBayar);
        } else if (booking.getStatusBooking() == Booking.StatusBooking.SELESAI) {
            Button btnDetail = new Button("Lihat Detail");
            btnDetail.setStyle("-fx-background-color: white; -fx-text-fill: #2D6BE4; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-border-color: #2D6BE4; -fx-border-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
            btnDetail.setOnAction(e -> {
                DetailBookingSelesaiController.selectedBooking = booking;
                com.kosku.Main.navigateTo("/view/penyewa/DetailBookingSelesaiPenyewa.fxml", "Detail Booking Selesai");
            });

            Button btnRating = new Button("Beri Rating");
            btnRating.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
            btnRating.setOnAction(e -> {
                try {
                    com.kosku.dao.ReviewDAO reviewDAO = new com.kosku.dao.ReviewDAO();
                    com.kosku.model.Review existing = reviewDAO.getReviewByBooking(booking.getIdBooking());
                    if (existing != null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Peringatan");
                        alert.setHeaderText(null);
                        alert.setContentText("Anda sudah memberikan rating untuk booking ini.");
                        alert.showAndWait();
                    } else {
                        BeriRatingController.selectedBooking = booking;
                        com.kosku.Main.navigateTo("/view/penyewa/BeriRatingPenyewa.fxml", "Beri Rating & Ulasan");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            actionBox.getChildren().addAll(btnDetail, btnRating);
        } else {
            Button btnDetail = new Button("Lihat Detail");
            btnDetail.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 24;");
            btnDetail.setOnAction(e -> {
                DetailBookingController.selectedBooking = booking;
                if (booking.getStatusBooking() == Booking.StatusBooking.DITOLAK) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info Penolakan");
                    alert.setHeaderText("Booking Ditolak");
                    alert.setContentText("Alasan: " + (booking.getAlasanTolak() != null ? booking.getAlasanTolak() : "Tidak ada alasan yang diberikan."));
                    alert.showAndWait();
                } else {
                    DetailBookingController.selectedBooking = booking;
                    com.kosku.Main.navigateTo("/view/penyewa/DetailBookingPenyewa.fxml", "Detail Booking");
                }
            });
            actionBox.getChildren().add(btnDetail);
        }

        priceBox.getChildren().addAll(priceText, spacer2, actionBox);
        detailsBox.getChildren().add(priceBox);
        card.getChildren().addAll(imagePane, detailsBox);

        return card;
    }
}
