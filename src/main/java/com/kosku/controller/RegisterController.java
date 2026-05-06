package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import com.kosku.model.User;
import com.kosku.dao.UserDAO;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField nomorHpField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Button btnRolePemilik;
    @FXML private Button btnRolePenyewa;
    @FXML private Button registerButton;

    @FXML private HBox roleInfoBanner;
    @FXML private Label roleInfoIcon;
    @FXML private Label roleInfoText;

    private User.Role selectedRole = User.Role.PEMILIK; // default

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Set default state: Pemilik active
        updateRoleUI();
    }

    @FXML
    public void handleSelectPemilik() {
        selectedRole = User.Role.PEMILIK;
        updateRoleUI();
    }

    @FXML
    public void handleSelectPenyewa() {
        selectedRole = User.Role.PENYEWA;
        updateRoleUI();
    }

    private void updateRoleUI() {
        if (selectedRole == User.Role.PEMILIK) {
            // Pemilik active
            btnRolePemilik.getStyleClass().removeAll("role-btn-inactive");
            if (!btnRolePemilik.getStyleClass().contains("role-btn-active")) {
                btnRolePemilik.getStyleClass().add("role-btn-active");
            }
            btnRolePenyewa.getStyleClass().removeAll("role-btn-active");
            if (!btnRolePenyewa.getStyleClass().contains("role-btn-inactive")) {
                btnRolePenyewa.getStyleClass().add("role-btn-inactive");
            }
            // Banner
            if (roleInfoIcon != null) roleInfoIcon.setText("🏡");
            if (roleInfoText != null) roleInfoText.setText("Anda akan terdaftar sebagai Pemilik Kos — dapat mengelola dan mendaftarkan kos.");
            if (roleInfoBanner != null) {
                roleInfoBanner.getStyleClass().removeAll("role-info-banner-penyewa");
                if (!roleInfoBanner.getStyleClass().contains("role-info-banner-pemilik")) {
                    roleInfoBanner.getStyleClass().add("role-info-banner-pemilik");
                }
                roleInfoBanner.getStyleClass().removeAll("role-info-banner");
            }
        } else {
            // Penyewa active
            btnRolePenyewa.getStyleClass().removeAll("role-btn-inactive");
            if (!btnRolePenyewa.getStyleClass().contains("role-btn-active")) {
                btnRolePenyewa.getStyleClass().add("role-btn-active");
            }
            btnRolePemilik.getStyleClass().removeAll("role-btn-active");
            if (!btnRolePemilik.getStyleClass().contains("role-btn-inactive")) {
                btnRolePemilik.getStyleClass().add("role-btn-inactive");
            }
            // Banner
            if (roleInfoIcon != null) roleInfoIcon.setText("🔍");
            if (roleInfoText != null) roleInfoText.setText("Anda akan terdaftar sebagai Penyewa Kos — dapat mencari dan memesan kos.");
            if (roleInfoBanner != null) {
                roleInfoBanner.getStyleClass().removeAll("role-info-banner-pemilik");
                if (!roleInfoBanner.getStyleClass().contains("role-info-banner-penyewa")) {
                    roleInfoBanner.getStyleClass().add("role-info-banner-penyewa");
                }
                roleInfoBanner.getStyleClass().removeAll("role-info-banner");
            }
        }
    }

    @FXML
    public void handleRegister() {
        String username    = usernameField.getText().trim();
        String email       = emailField.getText().trim();
        String nomorHp     = nomorHpField != null ? nomorHpField.getText().trim() : "";
        String password    = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Validasi wajib
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Kolom Kosong", "Username, Email, Password, dan Konfirmasi Password wajib diisi!");
            return;
        }

        if (username.length() < 3) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Username Terlalu Pendek", "Username minimal 3 karakter.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Email Tidak Valid", "Masukkan format email yang benar.");
            return;
        }

        if (password.length() < 6) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Password Terlalu Pendek", "Password minimal 6 karakter.");
            return;
        }

        if (!password.equals(confirmPass)) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Password Tidak Cocok", "Password dan Konfirmasi Password tidak sama!");
            return;
        }

        try {
            // Cek username sudah dipakai
            if (userDAO.findByUsername(username) != null) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                        "Username Sudah Dipakai", "Username '" + username + "' sudah terdaftar. Pilih username lain.");
                return;
            }

            // Cek email sudah terdaftar
            if (userDAO.findByEmail(email) != null) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                        "Email Sudah Terdaftar", "Email '" + email + "' sudah digunakan. Gunakan email lain.");
                return;
            }

            // Hash password
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

            // Buat user baru sesuai database
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);
            newUser.setRole(selectedRole);
            newUser.setIsVerified(true);
            if (!nomorHp.isEmpty()) {
                newUser.setNomorHp(nomorHp);
            }

            System.out.println("[RegisterDebug] Mendaftarkan user: " + username
                    + " | Email: " + email
                    + " | Role: " + selectedRole);

            userDAO.saveOrUpdate(newUser);

            System.out.println("[RegisterDebug] Registrasi berhasil.");

            String roleLabel = selectedRole == User.Role.PEMILIK ? "Pemilik Kos" : "Penyewa Kos";
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Registrasi Berhasil! 🎉",
                    "Akun " + roleLabel + " berhasil dibuat!\nSilakan login dengan username/email Anda.");

            Main.navigateTo("/view/login.fxml", "KosKu - Login");

        } catch (Exception e) {
            System.err.println("[RegisterDebug] Error: " + e.getMessage());
            e.printStackTrace();
            showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error Sistem", "Terjadi kesalahan saat mendaftar. Silakan coba lagi.");
        }
    }

    private void showAlert(javafx.scene.control.Alert.AlertType type, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoToLogin(MouseEvent event) {
        Main.navigateTo("/view/login.fxml", "KosKu - Login");
    }
}
