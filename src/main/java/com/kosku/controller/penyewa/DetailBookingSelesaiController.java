package com.kosku.controller.penyewa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.kosku.Main;
import com.kosku.dao.PembayaranDAO;
import com.kosku.model.Booking;
import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.model.Pembayaran;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DetailBookingSelesaiController implements Initializable {

    public static Booking selectedBooking;

    // Status Transaksi
    @FXML private Label lblIdBooking;
    @FXML private Label lblStatusBooking;
    @FXML private Label lblTanggalBooking;

    // Rincian Pembayaran
    @FXML private Label lblTotalTagihan;
    @FXML private Label lblWaktuPembayaran;
    @FXML private Button btnLihatBukti;

    // Detail Kamar & Properti
    @FXML private Label lblNamaKos;
    @FXML private Label lblNomorKamar;
    @FXML private Label lblAlamatKos;

    // Waktu Sewa
    @FXML private Label lblCheckIn;
    @FXML private Label lblCheckOut;

    private PembayaranDAO pembayaranDAO;
    private Pembayaran pembayaranInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pembayaranDAO = new PembayaranDAO();

        if (selectedBooking != null) {
            pembayaranInfo = pembayaranDAO.getPembayaranByBooking(selectedBooking);
            tampilkanData();
        } else {
            lblIdBooking.setText("Data Tidak Ditemukan");
        }
    }

    private void tampilkanData() {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

        // 1. Status Transaksi
        lblIdBooking.setText("BK-" + selectedBooking.getIdBooking());
        lblStatusBooking.setText("Selesai");
        lblTanggalBooking.setText(selectedBooking.getTanggalBooking() != null ? selectedBooking.getTanggalBooking().format(formatterDateTime) : "-");

        // 2. Rincian Pembayaran
        lblTotalTagihan.setText("Rp " + String.format("%,.0f", selectedBooking.getTotalHarga()).replace(",", "."));
        if (pembayaranInfo != null) {
            lblWaktuPembayaran.setText(pembayaranInfo.getCreatedAt() != null ? pembayaranInfo.getCreatedAt().format(formatterDateTime) : "-");
            btnLihatBukti.setDisable(pembayaranInfo.getBuktiBayar() == null || pembayaranInfo.getBuktiBayar().isEmpty());
        } else {
            lblWaktuPembayaran.setText("Data pembayaran tidak tersedia (Simulasi)");
            btnLihatBukti.setDisable(true);
        }

        // 3. Detail Kamar & Properti
        Kamar kamar = selectedBooking.getKamar();
        Kos kos = kamar.getKos();
        lblNamaKos.setText(kos.getNamaKos());
        lblNomorKamar.setText(kamar.getNomorKamar());
        lblAlamatKos.setText(kos.getAlamat());

        // 4. Waktu Sewa
        lblCheckIn.setText(selectedBooking.getTanggalMulai() != null ? selectedBooking.getTanggalMulai().format(formatterDate) : "-");
        lblCheckOut.setText(selectedBooking.getTanggalSelesai() != null ? selectedBooking.getTanggalSelesai().format(formatterDate) : "-");
    }

    @FXML
    void handleLihatBukti(ActionEvent event) {
        // TODO: Tampilkan gambar bukti bayar dalam modal
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bukti Pembayaran");
        alert.setHeaderText("Lihat Bukti Bayar");
        alert.setContentText("Fitur melihat bukti bayar sedang dalam tahap pengembangan.");
        alert.showAndWait();
    }

    @FXML
    void handleKembali(ActionEvent event) {
        Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
    }

    // Tombol Aksi
    @FXML
    void handleRating(ActionEvent event) {
        try {
            com.kosku.dao.ReviewDAO reviewDAO = new com.kosku.dao.ReviewDAO();
            com.kosku.model.Review existing = reviewDAO.getReviewByBooking(selectedBooking.getIdBooking());
            if (existing != null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Anda sudah memberikan rating untuk booking ini.");
                alert.showAndWait();
            } else {
                BeriRatingController.selectedBooking = selectedBooking;
                Main.navigateTo("/view/penyewa/BeriRatingPenyewa.fxml", "Beri Rating & Ulasan");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void handlePesanLagi(ActionEvent event) {
        // Arahkan ke DetailKosPenyewa dengan kos yang sama
        com.kosku.dao.KosDAO kosDAO = new com.kosku.dao.KosDAO();
        Kos fullKos = kosDAO.getKosByIdWithKamar(selectedBooking.getKamar().getKos().getIdKos());
        DetailKosPenyewaController.selectedKos = fullKos;
        Main.navigateTo("/view/penyewa/DetailKosPenyewa.fxml", "Detail Kos");
    }

    @FXML
    void handleHubungiPemilik(ActionEvent event) {
        // TODO: Implementasi chat dengan pemilik
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hubungi Pemilik");
        alert.setHeaderText(null);
        alert.setContentText("Fitur chat dengan pemilik sedang dalam tahap pengembangan oleh tim lain.");
        alert.showAndWait();
    }

    @FXML
    void handleKomplain(ActionEvent event) {
        // TODO: Implementasi chat komplain ke admin
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ajukan Komplain");
        alert.setHeaderText(null);
        alert.setContentText("Fitur pengajuan komplain ke admin sedang dalam tahap pengembangan oleh tim lain.");
        alert.showAndWait();
    }
}
