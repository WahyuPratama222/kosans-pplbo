package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import com.kosku.model.User;
import com.kosku.dao.UserDAO;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Dipanggil otomatis setelah FXML dimuat
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String identifier = emailField.getText();
        String password = passwordField.getText();

        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert("Error", "Email/Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            System.out.println("[LoginDebug] Mencoba login dengan ID: [" + identifier.trim() + "]");
            
            // Menggunakan method findByIdentifier (Username atau Email)
            User user = userDAO.findByIdentifier(identifier.trim());
            
            if (user == null) {
                System.out.println("[LoginDebug] User TIDAK DITEMUKAN di database.");
                showAlert("Gagal Login", "Email atau Username tidak terdaftar!");
                return;
            }

            System.out.println("[LoginDebug] User ditemukan: " + user.getUsername() + " (Role: " + user.getRole() + ")");

            if (org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
                System.out.println("[LoginDebug] Password COCOK! Melakukan login...");
                com.kosku.util.SessionManager.login(user);
                
                // Routing berdasarkan Role
                if (user.getRole() == User.Role.ADMIN) {
                    System.out.println("[LoginDebug] Mengarahkan Admin ke Dashboard...");
                    Main.navigateTo("/view/admin/DashboardAdmin.fxml", "KosKu - Dashboard Admin");
                } else if (user.getRole() == User.Role.PEMILIK) {
                    System.out.println("[LoginDebug] Mengarahkan Pemilik ke Dashboard...");
                    Main.navigateTo("/view/pemilik/DashboardPemilik.fxml", "KosKu - Dashboard Pemilik");
                } else {
                    System.out.println("[LoginDebug] Mengarahkan Penyewa ke Main Menu...");
                    Main.navigateTo("/view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard Penyewa");
                }
            } else {
                System.out.println("[LoginDebug] Password SALAH.");
                showAlert("Gagal Login", "Password salah!");
            }
        } catch (Exception e) {
            System.err.println("[LoginDebug] ERROR: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error Sistem", "Terjadi kesalahan pada koneksi database.");
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoToRegister(MouseEvent event) {
        Main.navigateTo("view/register.fxml", "KosKu - Register");
    }
}
