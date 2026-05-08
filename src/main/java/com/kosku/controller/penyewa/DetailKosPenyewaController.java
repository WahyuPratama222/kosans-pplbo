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

    private BookingDAO bookingDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingDAO = new BookingDAO();
        if (selectedKos != null) {
            tampilkanDetailKos(selectedKos);
        } else {
            lblNamaKos.setText("Data kos tidak ditemukan.");
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
        lblRating.setText("⭐ 4.9");

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
}
