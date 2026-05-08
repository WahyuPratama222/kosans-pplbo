package com.kosku.controller.penyewa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import com.kosku.Main;
import com.kosku.dao.ReviewDAO;
import com.kosku.model.Booking;
import com.kosku.model.Review;

import java.net.URL;
import java.util.ResourceBundle;

public class BeriRatingController implements Initializable {

    public static Booking selectedBooking;

    @FXML private Label lblNamaKos;
    @FXML private Label lblStatusRating;
    @FXML private TextArea taKomentar;
    
    @FXML private Button btnStar1;
    @FXML private Button btnStar2;
    @FXML private Button btnStar3;
    @FXML private Button btnStar4;
    @FXML private Button btnStar5;

    private int currentRating = 5;
    private ReviewDAO reviewDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reviewDAO = new ReviewDAO();

        if (selectedBooking != null && selectedBooking.getKamar() != null && selectedBooking.getKamar().getKos() != null) {
            lblNamaKos.setText(selectedBooking.getKamar().getKos().getNamaKos());
        } else {
            lblNamaKos.setText("Data Kos Tidak Ditemukan");
        }

        // Set default rating to 5
        updateStars(5);
    }

    private void updateStars(int rating) {
        currentRating = rating;
        
        Button[] stars = {btnStar1, btnStar2, btnStar3, btnStar4, btnStar5};
        
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].setStyle("-fx-background-color: transparent; -fx-text-fill: #F59E0B; -fx-font-size: 50px; -fx-cursor: hand; -fx-padding: 0;");
            } else {
                stars[i].setStyle("-fx-background-color: transparent; -fx-text-fill: #D1D5DB; -fx-font-size: 50px; -fx-cursor: hand; -fx-padding: 0;");
            }
        }

        // Update status text
        switch (rating) {
            case 1: lblStatusRating.setText("Sangat Buruk"); break;
            case 2: lblStatusRating.setText("Buruk"); break;
            case 3: lblStatusRating.setText("Cukup"); break;
            case 4: lblStatusRating.setText("Bagus"); break;
            case 5: lblStatusRating.setText("Luar Biasa!"); break;
        }
    }

    @FXML void handleStar1(ActionEvent event) { updateStars(1); }
    @FXML void handleStar2(ActionEvent event) { updateStars(2); }
    @FXML void handleStar3(ActionEvent event) { updateStars(3); }
    @FXML void handleStar4(ActionEvent event) { updateStars(4); }
    @FXML void handleStar5(ActionEvent event) { updateStars(5); }

    @FXML
    void handleKirim(ActionEvent event) {
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Tidak ada data booking yang dipilih.");
            return;
        }

        try {
            // Validasi apakah booking sudah memiliki review
            Review existingReview = reviewDAO.getReviewByBooking(selectedBooking.getIdBooking());
            if (existingReview != null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Anda sudah memberikan rating untuk booking ini.");
                return;
            }

            // Buat Review baru
            Review review = new Review();
            review.setBooking(selectedBooking);
            review.setPenyewa(selectedBooking.getPenyewa());
            review.setRating(currentRating);
            review.setKomentar(taKomentar.getText() != null ? taKomentar.getText().trim() : "");

            // Simpan ke database
            reviewDAO.saveOrUpdate(review);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(null);
            alert.setContentText("Terima kasih! Ulasan Anda berhasil disimpan.");
            alert.showAndWait();

            // Kembali ke riwayat
            Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan ulasan: " + e.getMessage());
        }
    }

    @FXML
    void handleBatal(ActionEvent event) {
        Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
