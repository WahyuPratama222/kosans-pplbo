package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.model.Booking.StatusBooking;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Riwayat Booking Penyewa
 * Menampilkan daftar semua booking yang telah dilakukan oleh penyewa
 */
public class RiwayatController implements Initializable {

    @FXML
    private TabPane tabPane;
    
    @FXML
    private TableView<Booking> tblRiwayat;
    
    @FXML
    private TableColumn<Booking, Integer> colIdBooking;
    @FXML
    private TableColumn<Booking, String> colNamaKos;
    @FXML
    private TableColumn<Booking, String> colNamaKamar;
    @FXML
    private TableColumn<Booking, String> colTanggalMulai;
    @FXML
    private TableColumn<Booking, String> colTanggalSelesai;
    @FXML
    private TableColumn<Booking, String> colStatusBooking;
    @FXML
    private TableColumn<Booking, String> colTotalHarga;
    @FXML
    private TableColumn<Booking, String> colAksi;

    @FXML
    private NavbarController navbarController;

    private BookingDAO bookingDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Riwayat Penyewa berhasil dimuat!");
        
        bookingDAO = new BookingDAO();
        
        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("riwayat");
        }
        
        // Inisialisasi tabel
        initializeTable();
        
        // Load data
        loadRiwayatBooking();
    }

    private void initializeTable() {
        // Konfigurasi kolom-kolom tabel
        colIdBooking.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdBooking()).asObject());
        
        colNamaKos.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getKamar().getKos().getNamaKos()));
        
        colNamaKamar.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getKamar().getNomorKamar()));
        
        colTanggalMulai.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTanggalMulai().toString()));
        
        colTanggalSelesai.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTanggalSelesai().toString()));
        
        colStatusBooking.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatusBooking().toString()));
        
        colTotalHarga.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                "Rp. " + cellData.getValue().getTotalHarga().toString()));
    }

    private void loadRiwayatBooking() {
        try {
            // Dapatkan ID penyewa dari SessionManager
            Integer idPenyewa = SessionManager.getCurrentUserId();
            
            if (idPenyewa == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Anda harus login terlebih dahulu");
                return;
            }
            
            // Ambil data booking dari database berdasarkan ID penyewa
            List<Booking> bookingList = bookingDAO.getBookingByPenyewa(idPenyewa);
            
            if (bookingList != null && !bookingList.isEmpty()) {
                tblRiwayat.getItems().addAll(bookingList);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Informasi", 
                    "Belum ada riwayat booking");
            }
        } catch (Exception e) {
            System.err.println("Error loading riwayat booking: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal memuat riwayat booking: " + e.getMessage());
        }
    }

    @FXML
    void lihatDetail(ActionEvent event) {
        Booking selectedBooking = tblRiwayat.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            StringBuilder detail = new StringBuilder();
            detail.append("ID Booking: ").append(selectedBooking.getIdBooking()).append("\n");
            detail.append("Nama Kos: ").append(selectedBooking.getKamar().getKos().getNamaKos()).append("\n");
            detail.append("Nama Kamar: ").append(selectedBooking.getKamar().getNomorKamar()).append("\n");
            detail.append("Tanggal Mulai: ").append(selectedBooking.getTanggalMulai()).append("\n");
            detail.append("Tanggal Selesai: ").append(selectedBooking.getTanggalSelesai()).append("\n");
            detail.append("Status: ").append(selectedBooking.getStatusBooking()).append("\n");
            detail.append("Total Harga: Rp. ").append(selectedBooking.getTotalHarga()).append("\n");
            
            showAlert(Alert.AlertType.INFORMATION, "Detail Booking", detail.toString());
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih booking terlebih dahulu");
        }
    }

    @FXML
    void batalkanBooking(ActionEvent event) {
        Booking selectedBooking = tblRiwayat.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            if (selectedBooking.getStatusBooking() == StatusBooking.PENDING) {
                try {
                    // Update status ke DIBATALKAN
                    selectedBooking.setStatusBooking(StatusBooking.DIBATALKAN);
                    bookingDAO.saveOrUpdate(selectedBooking);
                    
                    // Refresh tabel
                    tblRiwayat.getItems().clear();
                    loadRiwayatBooking();
                    
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                        "Booking berhasil dibatalkan");
                } catch (Exception e) {
                    System.err.println("Error membatalkan booking: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "Gagal membatalkan booking: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Hanya booking dengan status PENDING yang bisa dibatalkan");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", 
                "Pilih booking terlebih dahulu");
        }
    }

    @FXML
    void refreshRiwayat(ActionEvent event) {
        tblRiwayat.getItems().clear();
        loadRiwayatBooking();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
