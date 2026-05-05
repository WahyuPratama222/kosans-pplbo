package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.kosku.dao.NotifikasiDAO;
import com.kosku.model.Notifikasi;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Notifikasi Penyewa
 * Menampilkan berbagai notifikasi terkait booking, pembayaran, dan pesan dari pemilik
 */
public class NotifController implements Initializable {

    @FXML
    private ListView<String> lvNotifikasi;
    
    @FXML
    private TextArea taDetailNotif;
    
    @FXML
    private Label lblJumlahNotif;

    @FXML
    private NavbarController navbarController;

    private ObservableList<String> daftarNotifikasi;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    
    private NotifikasiDAO notifikasiDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Notifikasi Penyewa berhasil dimuat!");
        
        notifikasiDAO = new NotifikasiDAO();
        
        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("notifikasi");
        }
        
        // Setup UI
        taDetailNotif.setWrapText(true);
        taDetailNotif.setEditable(false);
        
        // Load notifikasi
        loadNotifikasi();
        
        // Listener untuk memilih notifikasi
        lvNotifikasi.setOnMouseClicked(e -> showDetailNotifikasi());
    }

    private void loadNotifikasi() {
        try {
            Integer userId = SessionManager.getCurrentUserId();
            
            if (userId == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Anda harus login terlebih dahulu");
                return;
            }
            
            daftarNotifikasi = FXCollections.observableArrayList();
            
            // Ambil data notifikasi dari database berdasarkan ID pengguna
            List<Notifikasi> notifikasiList = notifikasiDAO.getNotificationsByUser(userId);
            
            if (notifikasiList != null && !notifikasiList.isEmpty()) {
                for (Notifikasi notif : notifikasiList) {
                    String tipe = notif.getTipe() != null ? "[" + notif.getTipe() + "]" : "[INFO]";
                    String judul = notif.getJudul() != null ? notif.getJudul() : "Notifikasi";
                    String status = notif.getSudahDibaca() ? "✓ " : "● ";
                    daftarNotifikasi.add(status + tipe + " " + judul);
                }
            }
            
            lvNotifikasi.setItems(daftarNotifikasi);
            
            // Update jumlah notifikasi
            lblJumlahNotif.setText("Total: " + daftarNotifikasi.size() + " notifikasi");
            
        } catch (Exception e) {
            System.err.println("Error loading notifikasi: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal memuat notifikasi: " + e.getMessage());
        }
    }

    private void showDetailNotifikasi() {
        int selectedIndex = lvNotifikasi.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                Integer userId = SessionManager.getCurrentUserId();
                
                if (userId == null) {
                    return;
                }
                
                // Ambil notifikasi dari database berdasarkan index
                List<Notifikasi> notifikasiList = notifikasiDAO.getNotificationsByUser(userId);
                
                if (notifikasiList != null && selectedIndex < notifikasiList.size()) {
                    Notifikasi notif = notifikasiList.get(selectedIndex);
                    
                    StringBuilder detail = new StringBuilder();
                    detail.append("=== DETAIL NOTIFIKASI ===\n\n");
                    detail.append("Judul: ").append(notif.getJudul()).append("\n\n");
                    detail.append("Tipe: ").append(notif.getTipe()).append("\n\n");
                    detail.append("Waktu: ").append(notif.getWaktuNotifikasi().format(dateFormatter)).append("\n\n");
                    detail.append("Status: ").append(notif.getSudahDibaca() ? "Sudah dibaca" : "Belum dibaca").append("\n\n");
                    detail.append("Isi Lengkap:\n").append(notif.getIsi());
                    
                    taDetailNotif.setText(detail.toString());
                }
            } catch (Exception e) {
                System.err.println("Error showing detail: " + e.getMessage());
            }
        }
    }

    @FXML
    void tandaiSudahDibaca(ActionEvent event) {
        String selectedNotif = lvNotifikasi.getSelectionModel().getSelectedItem();
        if (selectedNotif != null) {
            try {
                // TODO: Update status notifikasi di database
                showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                    "Notifikasi ditandai sudah dibaca");
                loadNotifikasi();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "Gagal menandai notifikasi: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", 
                "Pilih notifikasi terlebih dahulu");
        }
    }

    @FXML
    void hapusNotifikasi(ActionEvent event) {
        String selectedNotif = lvNotifikasi.getSelectionModel().getSelectedItem();
        if (selectedNotif != null) {
            try {
                // TODO: Hapus notifikasi dari database
                daftarNotifikasi.remove(selectedNotif);
                taDetailNotif.clear();
                lblJumlahNotif.setText("Total: " + daftarNotifikasi.size() + " notifikasi");
                showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                    "Notifikasi berhasil dihapus");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "Gagal menghapus notifikasi: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", 
                "Pilih notifikasi terlebih dahulu");
        }
    }

    @FXML
    void hapusSemua(ActionEvent event) {
        // TODO: Implementasi untuk menghapus semua notifikasi
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText(null);
        confirm.setContentText("Hapus semua notifikasi?");
        
        if (confirm.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
            try {
                daftarNotifikasi.clear();
                taDetailNotif.clear();
                lblJumlahNotif.setText("Total: 0 notifikasi");
                showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                    "Semua notifikasi berhasil dihapus");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "Gagal menghapus notifikasi: " + e.getMessage());
            }
        }
    }

    @FXML
    void refreshNotifikasi(ActionEvent event) {
        loadNotifikasi();
        taDetailNotif.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
