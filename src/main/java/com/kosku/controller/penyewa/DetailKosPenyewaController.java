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

        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Booking Kos");
        dialog.setHeaderText("Silakan lengkapi detail booking Anda");

        ButtonType btnKonfirmasi = new ButtonType("Konfirmasi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnKonfirmasi, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Kamar> cbKamar = new ComboBox<>();
        cbKamar.getItems().addAll(kamarTersedia);
        cbKamar.setConverter(new StringConverter<Kamar>() {
            @Override public String toString(Kamar object) { return object != null ? "Kamar " + object.getNomorKamar() : ""; }
            @Override public Kamar fromString(String string) { return null; }
        });
        cbKamar.getSelectionModel().selectFirst();

        DatePicker dpMulai = new DatePicker(LocalDate.now());

        String durasiLabelStr = selectedKos.getDurasiSewa() != null ? selectedKos.getDurasiSewa().name().toLowerCase() : "bulanan";
        if(durasiLabelStr.equals("bulanan")) durasiLabelStr = "Bulan";
        else if(durasiLabelStr.equals("harian")) durasiLabelStr = "Hari";
        else if(durasiLabelStr.equals("mingguan")) durasiLabelStr = "Minggu";
        else durasiLabelStr = "Tahun";
        
        Spinner<Integer> spLamaSewa = new Spinner<>(1, 60, 1);
        
        Label lblTotalHarga = new Label("Rp 0");
        lblTotalHarga.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D6BE4; -fx-font-size: 16px;");

        // Helper untuk update harga dan tanggal selesai
        Runnable updateHargaDanTanggal = () -> {
            int lama = spLamaSewa.getValue();
            BigDecimal total = selectedKos.getHarga().multiply(BigDecimal.valueOf(lama));
            lblTotalHarga.setText("Rp " + String.format("%,.0f", total).replace(",", "."));
        };

        spLamaSewa.valueProperty().addListener((obs, oldV, newV) -> updateHargaDanTanggal.run());
        updateHargaDanTanggal.run(); // inisialisasi awal

        grid.add(new Label("Pilih Kamar:"), 0, 0);
        grid.add(cbKamar, 1, 0);
        grid.add(new Label("Tanggal Mulai:"), 0, 1);
        grid.add(dpMulai, 1, 1);
        grid.add(new Label("Lama Sewa (" + durasiLabelStr + "):"), 0, 2);
        grid.add(spLamaSewa, 1, 2);
        grid.add(new Label("Total Harga:"), 0, 3);
        grid.add(lblTotalHarga, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnKonfirmasi) {
                int lama = spLamaSewa.getValue();
                LocalDate tanggalMulai = dpMulai.getValue();
                LocalDate tanggalSelesai = tanggalMulai;
                
                String durasi = selectedKos.getDurasiSewa() != null ? selectedKos.getDurasiSewa().name() : "BULANAN";
                if(durasi.equals("HARIAN")) tanggalSelesai = tanggalMulai.plusDays(lama);
                else if(durasi.equals("MINGGUAN")) tanggalSelesai = tanggalMulai.plusWeeks(lama);
                else if(durasi.equals("BULANAN")) tanggalSelesai = tanggalMulai.plusMonths(lama);
                else tanggalSelesai = tanggalMulai.plusYears(lama);
                
                BigDecimal totalHarga = selectedKos.getHarga().multiply(BigDecimal.valueOf(lama));

                return Booking.builder()
                        .penyewa(User.builder().idUser(userId).build())
                        .kamar(cbKamar.getValue())
                        .tanggalMulai(tanggalMulai)
                        .tanggalSelesai(tanggalSelesai)
                        .totalHarga(totalHarga)
                        .statusBooking(Booking.StatusBooking.PENDING)
                        .tanggalBooking(LocalDateTime.now())
                        .build();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(booking -> {
            try {
                bookingDAO.saveOrUpdate(booking);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Sukses");
                alert.setHeaderText(null);
                alert.setContentText("Booking Anda berhasil dikirim dengan status PENDING. Menunggu konfirmasi pemilik kos.");
                alert.showAndWait();
                
                Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
            } catch (Exception e) {
                System.err.println("Gagal menyimpan booking: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Terjadi kesalahan saat memproses booking: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
}
