package com.kosku.controller.penyewa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.kosku.Main;
import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.model.Kos;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DetailBookingController implements Initializable {

    public static Booking selectedBooking;

    @FXML private TextField tfTanggalMulai;
    @FXML private TextField tfTanggalSelesai;
    @FXML private ToggleGroup paymentGroup;
    @FXML private Label lblStatusPembayaran;
    
    @FXML private Label lblNamaKos;
    @FXML private Label lblTotalHarga;
    @FXML private Label lblDurasiSewa;
    @FXML private Label lblKamarDetail;
    @FXML private Label lblAlamatKos;
    @FXML private Button btnBayar;

    private BookingDAO bookingDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingDAO = new BookingDAO();

        if (selectedBooking != null) {
            tampilkanDataBooking();
        } else {
            lblNamaKos.setText("Data Booking Tidak Ditemukan");
        }
    }

    private void tampilkanDataBooking() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        tfTanggalMulai.setText(selectedBooking.getTanggalMulai() != null ? selectedBooking.getTanggalMulai().format(formatter) : "-");
        tfTanggalSelesai.setText(selectedBooking.getTanggalSelesai() != null ? selectedBooking.getTanggalSelesai().format(formatter) : "-");

        Kos kos = selectedBooking.getKamar().getKos();
        lblNamaKos.setText(kos.getNamaKos());
        lblTotalHarga.setText("Rp " + String.format("%,.0f", selectedBooking.getTotalHarga()).replace(",", "."));
        
        String durasi = kos.getDurasiSewa() != null ? kos.getDurasiSewa().name() : "";
        lblDurasiSewa.setText("/ " + durasi);

        lblKamarDetail.setText("Kamar: " + selectedBooking.getKamar().getNomorKamar());
        lblAlamatKos.setText(kos.getAlamat());

        Booking.StatusBooking status = selectedBooking.getStatusBooking();
        
        if (status == Booking.StatusBooking.DITERIMA) {
            lblStatusPembayaran.setText("Status: Menunggu Pembayaran. Silakan pilih metode pembayaran dan konfirmasi.");
            btnBayar.setDisable(false);
            btnBayar.setText("🔒 Konfirmasi Pembayaran");
        } else if (status == Booking.StatusBooking.PENDING) {
            lblStatusPembayaran.setText("Status: Menunggu Verifikasi Pemilik. Pembayaran Anda sedang diproses.");
            lblStatusPembayaran.setStyle("-fx-text-fill: #B45309;");
            btnBayar.setDisable(true);
            btnBayar.setText("⏳ Menunggu Verifikasi");
            disablePaymentMethods();
        } else if (status == Booking.StatusBooking.SELESAI) {
            lblStatusPembayaran.setText("Status: Booking Selesai. Pembayaran lunas.");
            lblStatusPembayaran.setStyle("-fx-text-fill: #16A34A;");
            btnBayar.setDisable(true);
            btnBayar.setText("✅ Lunas");
            disablePaymentMethods();
        } else {
            lblStatusPembayaran.setText("Status: " + status.name());
            btnBayar.setDisable(true);
            disablePaymentMethods();
        }
    }

    private void disablePaymentMethods() {
        if (paymentGroup.getToggles() != null) {
            for (Toggle toggle : paymentGroup.getToggles()) {
                if (toggle instanceof ToggleButton) {
                    ((ToggleButton) toggle).setDisable(true);
                }
            }
        }
    }

    @FXML
    void handleBayar(ActionEvent event) {
        if (paymentGroup.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Pilih Metode Pembayaran");
            alert.setHeaderText(null);
            alert.setContentText("Silakan pilih metode pembayaran terlebih dahulu!");
            alert.showAndWait();
            return;
        }

        // Simulasi pembayaran
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Pembayaran");
        confirm.setHeaderText("Konfirmasi Pembayaran Anda");
        confirm.setContentText("Apakah Anda yakin ingin melanjutkan pembayaran sebesar " + lblTotalHarga.getText() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                prosesPembayaran();
            }
        });
    }

    private void prosesPembayaran() {
        try {
            // Karena ini simulasi sederhana, kita ubah status menjadi DITERIMA (Menunggu verifikasi) atau SELESAI.
            // Sesuai prompt: "menunggu verifikasi pemilik atau pembayaran"
            // Setelah bayar -> Menunggu verifikasi (DITERIMA) atau langsung SELESAI? 
            // Kita set SELESAI agar masuk riwayat Selesai.
            selectedBooking.setStatusBooking(Booking.StatusBooking.SELESAI);
            bookingDAO.saveOrUpdate(selectedBooking);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pembayaran Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Pembayaran Anda berhasil dikonfirmasi. Booking Selesai.");
            alert.showAndWait();

            // Kembali ke riwayat
            Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
            
        } catch (Exception e) {
            System.err.println("Error proses pembayaran: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat memproses pembayaran.");
            alert.showAndWait();
        }
    }

    @FXML
    void handleKembali(ActionEvent event) {
        Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
    }
}
