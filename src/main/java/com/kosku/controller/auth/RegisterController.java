package com.kosku.controller.auth;

import com.kosku.Main;
import com.kosku.model.User;
import com.kosku.service.auth.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;

public class RegisterController {

    @FXML private TextField usernameField, emailField, nomorHpField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Button btnRolePemilik, btnRolePenyewa;
    @FXML private HBox roleInfoBanner;
    @FXML private Label roleInfoIcon, roleInfoText;

    private User.Role selectedRole = User.Role.PEMILIK;
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
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

    @FXML
    public void handleRegister() {
        // 1. Ambil data
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();
        String nomorHp = nomorHpField != null ? nomorHpField.getText().trim() : "";

        // 2. Validasi UI dasar (Tanpa cek DB)
        if (!validateInput(username, email, password, confirmPass)) return;

        try {
            // 3. Bungkus data ke objek
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // Kirim plain, service yang bakal hash
            newUser.setRole(selectedRole);
            newUser.setNomorHp(nomorHp);
            newUser.setIsVerified(true);

            // 4. Delegasikan ke Service
            authService.register(newUser);

            // 5. Berhasil
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil!", 
                "Akun " + (selectedRole == User.Role.PEMILIK ? "Pemilik" : "Penyewa") + " berhasil dibuat.");
            Main.navigateTo("/view/auth/login.fxml", "KosKu - Login");

        } catch (Exception e) {
            // Error dari service (misal: "Username sudah digunakan") tampil di sini
            showAlert(Alert.AlertType.ERROR, "Gagal Daftar", e.getMessage());
        }
    }

    private boolean validateInput(String username, String email, String pass, String confirm) {
        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Kosong", "Semua kolom wajib diisi!");
            return false;
        }
        if (pass.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Password Lemah", "Minimal 6 karakter.");
            return false;
        }
        if (!pass.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Password Tidak Cocok", "Konfirmasi password salah.");
            return false;
        }
        return true;
    }

    private void updateRoleUI() {
        boolean isPemilik = (selectedRole == User.Role.PEMILIK);
        
        // Update Button Style
        toggleStyle(btnRolePemilik, isPemilik);
        toggleStyle(btnRolePenyewa, !isPemilik);

        // Update Banner Text
        roleInfoIcon.setText(isPemilik ? "🏡" : "🔍");
        roleInfoText.setText(isPemilik ? "Anda akan terdaftar sebagai Pemilik Kos." : "Anda akan terdaftar sebagai Penyewa Kos.");
        
        // Update Banner Style
        roleInfoBanner.getStyleClass().removeAll("role-info-banner-pemilik", "role-info-banner-penyewa");
        roleInfoBanner.getStyleClass().add(isPemilik ? "role-info-banner-pemilik" : "role-info-banner-penyewa");
    }

    private void toggleStyle(Button btn, boolean active) {
        btn.getStyleClass().removeAll("role-btn-active", "role-btn-inactive");
        btn.getStyleClass().add(active ? "role-btn-active" : "role-btn-inactive");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoToLogin(MouseEvent event) {
        Main.navigateTo("/view/auth/login.fxml", "KosKu - Login");
    }
}