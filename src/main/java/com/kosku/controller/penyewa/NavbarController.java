package com.kosku.controller.penyewa;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import com.kosku.Main;
import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import com.kosku.util.SessionManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class NavbarController implements Initializable {

    @FXML 
    private Label lblNamaPengguna;
    
    @FXML 
    private Button btnBeranda;
    
    @FXML 
    private Button btnRiwayat;
    
    @FXML 
    private Button btnChat;
    
    @FXML 
    private Button btnNotif;

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20 8 20; -fx-background-radius: 20;";
    private final String STYLE_ACTIVE = "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20 8 20; -fx-background-radius: 20;";

    private UserDAO userDAO;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        
        // Load nama pengguna
        loadUserProfile();
    }

    /**
     * Load profil pengguna saat ini dari database
     */
    private void loadUserProfile() {
        try {
            // Dapatkan ID pengguna dari SessionManager
            Integer userId = SessionManager.getCurrentUserId();
            
            if (userId == null) {
                // Jika belum login, gunakan placeholder
                lblNamaPengguna.setText("Halo, User!");
                return;
            }
            
            currentUser = userDAO.getById(User.class, userId);
            
            if (currentUser != null) {
                String displayName = currentUser.getUsername() != null ? 
                    currentUser.getUsername() : "User";
                lblNamaPengguna.setText("Halo, " + displayName + "!");
            } else {
                lblNamaPengguna.setText("Halo, User!");
            }
        } catch (Exception e) {
            System.err.println("Error loading user profile: " + e.getMessage());
            lblNamaPengguna.setText("Halo, User!");
        }
    }

    /**
     * Set highlight menu sesuai halaman aktif
     */
    public void setHighlight(String menu) {
        // Reset semua ke normal terlebih dahulu
        btnBeranda.setStyle(STYLE_NORMAL);
        btnRiwayat.setStyle(STYLE_NORMAL);
        btnChat.setStyle(STYLE_NORMAL);
        btnNotif.setStyle(STYLE_NORMAL);

        // Nyalakan sesuai halaman aktif
        switch (menu.toLowerCase()) {
            case "beranda": 
                btnBeranda.setStyle(STYLE_ACTIVE); 
                break;
            case "riwayat": 
                btnRiwayat.setStyle(STYLE_ACTIVE); 
                break;
            case "chat": 
                btnChat.setStyle(STYLE_ACTIVE); 
                break;
            case "notifikasi": 
                btnNotif.setStyle(STYLE_ACTIVE); 
                break;
        }
    }

    // ==========================================
    // NAVIGATION & SCENE SWITCHING
    // ==========================================

    /**
     * Switch scene dengan FXMLLoader
     */
    private void switchScene(Event event, String fxmlPath) {
        Main.navigateTo(fxmlPath);
    }

    @FXML
    void goToMainMenu(Event event) {
        switchScene(event, "/view/penyewa/MainMenuPenyewa.fxml");
    }

    @FXML
    void goToRiwayat(Event event) {
        switchScene(event, "/view/penyewa/RiwayatPenyewa.fxml");
    }

    @FXML
    void goToChat(Event event) {
        switchScene(event, "/view/penyewa/ChatPenyewa.fxml");
    }

    @FXML
    void goToNotif(Event event) {
        switchScene(event, "/view/penyewa/NotifPenyewa.fxml");
    }

    @FXML
    void goToProfil(Event event) {
        switchScene(event, "/view/penyewa/ProfilPenyewa.fxml");
    }

    @FXML
    void handleLogout(Event event) {
        // Konfirmasi logout
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Logout");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin keluar?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Clear session/login data
                SessionManager.logout();
                
                // Navigate ke login page
                Main.navigateTo("/view/auth/login.fxml", "KosKu - Login");
                
                System.out.println("Logout berhasil!");
            } catch (Exception e) {
                System.err.println("Error logout: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "Gagal logout: " + e.getMessage());
            }
        }
    }

    /**
     * Refresh data navbar (biasa dipanggil setelah update profil)
     */
    public void refreshUserProfile() {
        loadUserProfile();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}