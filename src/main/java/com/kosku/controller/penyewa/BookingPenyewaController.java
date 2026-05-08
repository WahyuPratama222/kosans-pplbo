package com.kosku.controller.penyewa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BookingPenyewaController implements Initializable {

    // Preview Section
    @FXML private ImageView ivFotoKosPreview;
    @FXML private Label lblNamaKosPreview;
    @FXML private Label lblAlamatPreview;
    @FXML private Label lblTipeKosPreview;
    @FXML private Label lblRatingPreview;
    @FXML private Label lblHargaPreview;

    // Form Section
    @FXML private TextField tfNamaLengkap;
    @FXML private TextField tfNoTelepon;
    @FXML private TextField tfEmail;
    @FXML private ComboBox<String> cbDurasi;
    @FXML private DatePicker dpTanggalMulai;
    @FXML private TextField tfTanggalBerakhir;
    @FXML private TextArea taKatatan;
    @FXML private CheckBox cbSetuju;

    // Summary Section
    @FXML private Label lblHargaBulanan;
    @FXML private Label lblJumlahBulan;
    @FXML private Label lblTotalBiaya;
    @FXML private Button btnSubmitBooking;

    private BookingDAO bookingDAO;
    private Kos selectedKos;
    private BigDecimal hargaBulanan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingDAO = new BookingDAO();
        selectedKos = DetailKosPenyewaController.selectedKos;

        if (selectedKos == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Data kos tidak ditemukan");
            return;
        }

        initializeData();
        setupEventHandlers();
    }

    private void initializeData() {
        // Populate kos preview
        tampilkanPreviewKos();



        // Populate durasi options
        ObservableList<String> durasiOptions = FXCollections.observableArrayList(
                "1 Bulan", "3 Bulan", "6 Bulan", "12 Bulan"
        );
        cbDurasi.setItems(durasiOptions);
        cbDurasi.getSelectionModel().selectFirst();

        // Set default date to today
        dpTanggalMulai.setValue(LocalDate.now());

        // Populate user data (if available in SessionManager)
        Integer userId = SessionManager.getCurrentUserId();
        if (userId != null) {
            // TODO: Fetch and populate user data (nama, telepon, email) dari database
            // Untuk sekarang, biarkan kosong atau pre-fill jika data tersedia
        }

        // Initialize harga
        updatePriceAndDate();
    }

    private void tampilkanPreviewKos() {
        lblNamaKosPreview.setText(selectedKos.getNamaKos() != null ? selectedKos.getNamaKos() : "N/A");
        lblAlamatPreview.setText("📍 " + (selectedKos.getAlamat() != null ? selectedKos.getAlamat() : "Alamat tidak tersedia"));

        BigDecimal harga = selectedKos.getHarga();
        if (harga != null) {
            lblHargaPreview.setText("Rp " + String.format("%,.0f", harga).replace(",", "."));
            hargaBulanan = harga;
        } else {
            lblHargaPreview.setText("Hubungi Pemilik");
            hargaBulanan = BigDecimal.ZERO;
        }

        // Set tipe kos badge
        String tipeStr = "🚹 Putra";
        String tipeBg = "#2D6BE4";
        if (selectedKos.getTipeKos() != null) {
            if (selectedKos.getTipeKos().name().equals("PUTRI")) {
                tipeStr = "🚺 Putri";
                tipeBg = "#D6336C";
            } else if (selectedKos.getTipeKos().name().equals("CAMPUR")) {
                tipeStr = "👥 Campur";
                tipeBg = "#16A34A";
            }
        }
        lblTipeKosPreview.setText(tipeStr);
        lblTipeKosPreview.setStyle("-fx-background-color: " + tipeBg + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 12 5 12; -fx-background-radius: 15;");

        lblRatingPreview.setText("⭐ 4.9");

        // Load image
        String imagePath = selectedKos.getFotoKos() != null ? "/" + selectedKos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ivFotoKosPreview.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                URL defaultUrl = getClass().getResource("/images/tesKos.png");
                if (defaultUrl != null) ivFotoKosPreview.setImage(new Image(defaultUrl.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        cbDurasi.setOnAction(event -> updatePriceAndDate());
        dpTanggalMulai.setOnAction(event -> updatePriceAndDate());
    }

    private void updatePriceAndDate() {
        // Get selected values
        String selectedDurasi = cbDurasi.getValue();
        LocalDate startDate = dpTanggalMulai.getValue();

        if (selectedDurasi == null || startDate == null) {
            return;
        }

        // Parse durasi
        int months = extractMonths(selectedDurasi);

        // Update end date
        LocalDate endDate = startDate.plusMonths(months);
        tfTanggalBerakhir.setText(endDate.toString());

        // Update price summary
        if (hargaBulanan != null && hargaBulanan.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalHarga = hargaBulanan.multiply(BigDecimal.valueOf(months));

            // Update labels in summary section
            lblHargaBulanan.setText("Rp " + String.format("%,.0f", hargaBulanan).replace(",", "."));
            lblJumlahBulan.setText(months + " Bulan");
            lblTotalBiaya.setText("Rp " + String.format("%,.0f", totalHarga).replace(",", "."));
        }
    }

    private int extractMonths(String durasi) {
        if (durasi.contains("3")) return 3;
        if (durasi.contains("6")) return 6;
        if (durasi.contains("12")) return 12;
        return 1; // default 1 bulan
    }

    @FXML
    void handleKembali(ActionEvent event) {
        Main.navigateTo("/view/penyewa/DetailKosPenyewa.fxml", "Detail Kos");
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Main.navigateTo("/view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }

    @FXML
    void handleSubmitBooking(ActionEvent event) {
        // Validate form
        String validationError = validateForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", validationError);
            return;
        }

        // Get form data
        Integer userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Anda harus login untuk melakukan booking");
            return;
        }

        String selectedDurasi = cbDurasi.getValue();
        LocalDate startDate = dpTanggalMulai.getValue();
        int months = extractMonths(selectedDurasi);
        LocalDate endDate = startDate.plusMonths(months);

        BigDecimal totalHarga = hargaBulanan.multiply(BigDecimal.valueOf(months));

        // Create booking object (kamar akan ditentukan oleh pemilik kemudian)
        Booking booking = Booking.builder()
                .penyewa(User.builder().idUser(userId).build())
                .kamar(null)  // Kamar ditentukan oleh pemilik
                .tanggalMulai(startDate)
                .tanggalSelesai(endDate)
                .totalHarga(totalHarga)
                .statusBooking(Booking.StatusBooking.PENDING)
                .tanggalBooking(LocalDateTime.now())
                // TODO: Add catatan if TextArea is mapped to booking model
                .build();

        // Save booking
        try {
            bookingDAO.saveOrUpdate(booking);
            showAlert(Alert.AlertType.INFORMATION, "Booking Sukses", 
                    "Booking Anda berhasil dikirim dengan status PENDING.\nMenunggu konfirmasi pemilik kos.");
            Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
        } catch (Exception e) {
            System.err.println("Error saving booking: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Terjadi kesalahan saat memproses booking: " + e.getMessage());
        }
    }

    private String validateForm() {
        if (tfNamaLengkap.getText().trim().isEmpty()) {
            return "Nama lengkap tidak boleh kosong";
        }
        if (tfNoTelepon.getText().trim().isEmpty()) {
            return "Nomor telepon tidak boleh kosong";
        }
        if (tfEmail.getText().trim().isEmpty()) {
            return "Email tidak boleh kosong";
        }
        if (cbDurasi.getValue() == null) {
            return "Pilih durasi sewa";
        }
        if (dpTanggalMulai.getValue() == null) {
            return "Pilih tanggal mulai sewa";
        }
        if (dpTanggalMulai.getValue().isBefore(LocalDate.now())) {
            return "Tanggal mulai tidak boleh di masa lalu";
        }
        if (!cbSetuju.isSelected()) {
            return "Anda harus setuju dengan syarat dan ketentuan";
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
