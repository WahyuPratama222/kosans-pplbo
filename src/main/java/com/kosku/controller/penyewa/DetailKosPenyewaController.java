package com.kosku.controller.penyewa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.StringConverter;
import com.kosku.Main;
import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.model.User;
import com.kosku.util.SessionManager;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DetailKosPenyewaController implements Initializable {

    public static Kos selectedKos;

    @FXML private ImageView ivFotoKos;
    @FXML private Label lblNamaKos;
    @FXML private Label lblAlamat;
    @FXML private Label lblHarga;
    @FXML private Label lblTipeKos;
    @FXML private Label lblDurasi;
    @FXML private Label lblDeskripsi;
    @FXML private Label lblKamarTersedia;
    @FXML private Label lblRating;
    @FXML private javafx.scene.layout.VBox vboxUlasan;
    @FXML private Button btnMuatLebihUlasan;

    private BookingDAO bookingDAO;
    private com.kosku.dao.ReviewDAO reviewDAO;
    
    private int currentReviewOffset = 0;
    private final int REVIEWS_PER_PAGE = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingDAO = new BookingDAO();
        reviewDAO = new com.kosku.dao.ReviewDAO();
        
        if (selectedKos != null) {
            tampilkanDetailKos(selectedKos);
            loadUlasan();
        } else {
            lblNamaKos.setText("Data kos tidak ditemukan.");
            vboxUlasan.getChildren().add(new Label("Data kos tidak tersedia."));
            btnMuatLebihUlasan.setVisible(false);
        }
    }

    private void tampilkanDetailKos(Kos kos) {
        lblNamaKos.setText(kos.getNamaKos() != null ? kos.getNamaKos() : "N/A");
        lblAlamat.setText("📍 " + (kos.getAlamat() != null ? kos.getAlamat() : "Alamat tidak tersedia"));
        lblDeskripsi.setText(kos.getDeskripsi() != null ? kos.getDeskripsi() : "Tidak ada deskripsi.");

        BigDecimal hargaMin = kos.getHarga();
        if (hargaMin != null) {
            lblHarga.setText("Rp " + String.format("%,.0f", hargaMin).replace(",", "."));
        } else {
            lblHarga.setText("Hubungi Pemilik");
        }

        String tipeStr = "🚹 Putra";
        String tipeBg = "#2D6BE4";
        if (kos.getTipeKos() != null) {
            if (kos.getTipeKos().name().equals("PUTRI")) {
                tipeStr = "🚺 Putri";
                tipeBg = "#D6336C";
            } else if (kos.getTipeKos().name().equals("CAMPUR")) {
                tipeStr = "👥 Campur";
                tipeBg = "#16A34A";
            }
        }
        lblTipeKos.setText(tipeStr);
        lblTipeKos.setStyle("-fx-background-color: " + tipeBg + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 20;");

        String durasiStr = kos.getDurasiSewa() != null ? kos.getDurasiSewa().name() : "BULANAN";
        durasiStr = durasiStr.substring(0, 1).toUpperCase() + durasiStr.substring(1).toLowerCase();
        lblDurasi.setText(durasiStr);

        int jumlahKamarTersedia = 0;
        if (kos.getKamarList() != null) {
            for (Kamar k : kos.getKamarList()) {
                if (Boolean.TRUE.equals(k.getStatusTersedia())) jumlahKamarTersedia++;
            }
        }
        lblKamarTersedia.setText(jumlahKamarTersedia + " Kamar");
        
        // Fetch average rating from DB
        Double avgRating = reviewDAO.getAverageRating(kos.getIdKos());
        if (avgRating != null && avgRating > 0) {
            lblRating.setText(String.format("⭐ %.1f", avgRating));
        } else {
            lblRating.setText("⭐ -"); // Belum ada rating
        }

        String imagePath = kos.getFotoKos() != null ? "/" + kos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ivFotoKos.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                URL defaultUrl = getClass().getResource("/images/tesKos.png");
                if (defaultUrl != null) ivFotoKos.setImage(new Image(defaultUrl.toExternalForm()));
            }
        } catch (Exception e) {}
    }

    @FXML
    void handleKembali(ActionEvent event) {
        Main.navigateTo("/view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }

    @FXML
    void handleBooking(ActionEvent event) {
        Integer userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Anda harus login sebagai penyewa untuk melakukan booking.");
            alert.showAndWait();
            return;
        }

        List<Kamar> kamarTersedia = selectedKos.getKamarList().stream()
                .filter(k -> Boolean.TRUE.equals(k.getStatusTersedia()))
                .collect(Collectors.toList());

        if (kamarTersedia.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Kamar Penuh");
            alert.setHeaderText(null);
            alert.setContentText("Maaf, saat ini tidak ada kamar yang tersedia di kos ini.");
            alert.showAndWait();
            return;
        }

        // Navigate ke halaman booking dengan data kos yang dipilih
        Main.navigateTo("/view/penyewa/BookingPenyewa.fxml", "Booking Kos");
    }

    @FXML
    void handleChatPemilik(ActionEvent event) {
        if (selectedKos != null && selectedKos.getPemilik() != null) {
            ChatController.targetPemilikChat = selectedKos.getPemilik();
        }
        Main.navigateTo("/view/penyewa/ChatPenyewa.fxml", "KosKu - Chat");
    }

    private void loadUlasan() {
        if (selectedKos == null) return;
        
        List<com.kosku.model.Review> reviews = reviewDAO.getReviewsByKos(selectedKos.getIdKos(), REVIEWS_PER_PAGE, currentReviewOffset);
        
        if (reviews == null || reviews.isEmpty()) {
            if (currentReviewOffset == 0) {
                Label lblEmpty = new Label("Belum ada ulasan untuk kos ini.");
                lblEmpty.setStyle("-fx-font-size: 15px; -fx-text-fill: #888;");
                vboxUlasan.getChildren().add(lblEmpty);
            }
            btnMuatLebihUlasan.setVisible(false);
            return;
        }

        for (com.kosku.model.Review r : reviews) {
            vboxUlasan.getChildren().add(createReviewCard(r));
        }
        
        // Jika data yang diambil kurang dari limit, artinya sudah habis
        if (reviews.size() < REVIEWS_PER_PAGE) {
            btnMuatLebihUlasan.setVisible(false);
        } else {
            btnMuatLebihUlasan.setVisible(true);
        }
    }

    private javafx.scene.layout.VBox createReviewCard(com.kosku.model.Review review) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(10);
        card.setStyle("-fx-background-color: #F8F9FF; -fx-padding: 20; -fx-background-radius: 12; -fx-border-color: #E8EDF5; -fx-border-radius: 12;");
        
        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Asumsi penyewa tidak null
        String namaPenyewa = review.getPenyewa() != null ? review.getPenyewa().getUsername() : "Penyewa Anonim";
        Label lblName = new Label(namaPenyewa);
        lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A3A6B;");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        String tgl = review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate().toString() : "";
        Label lblDate = new Label(tgl);
        lblDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");
        
        header.getChildren().addAll(lblName, spacer, lblDate);
        
        Label lblBintang = new Label("⭐".repeat(review.getRating()));
        lblBintang.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 14px;");
        
        Label lblKomentar = new Label(review.getKomentar() != null ? review.getKomentar() : "");
        lblKomentar.setStyle("-fx-font-size: 14px; -fx-text-fill: #555; -fx-wrap-text: true;");
        
        card.getChildren().addAll(header, lblBintang, lblKomentar);
        return card;
    }

    @FXML
    void handleMuatLebihUlasan(ActionEvent event) {
        currentReviewOffset += REVIEWS_PER_PAGE;
        loadUlasan();
    }
}
