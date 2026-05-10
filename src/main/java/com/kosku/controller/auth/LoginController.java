package com.kosku.controller.auth;

import com.kosku.Main;
import com.kosku.model.User;
import com.kosku.service.auth.AuthService;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private javafx.scene.control.Button togglePasswordButton;

    // Inisialisasi Service
    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin(ActionEvent event) {
        String identifier = emailField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        try {
            // Delegasikan logika autentikasi ke Service
            User user = authService.login(identifier, password);
            
            // Jika berhasil, lakukan routing berdasarkan role
            System.out.println("[Login] Berhasil login sebagai: " + user.getRole());
            navigateToDashboard(user);

        } catch (Exception e) {
            // Tangkap pesan error dari Service (misal: "Password salah" atau "Akun tidak ditemukan")
            System.err.println("[Login Error] " + e.getMessage());
            showAlert("Login Gagal", e.getMessage());
        }
    }

    /**
     * Memisahkan logika navigasi agar method handleLogin tetap bersih
     */
    private void navigateToDashboard(User user) {
        String viewPath;
        String title;

        switch (user.getRole()) {
            case ADMIN:
                viewPath = "/view/Admin/DashboardAdmin.fxml";
                title = "KosKu - Dashboard Admin";
                break;
            case PEMILIK:
                viewPath = "/view/Pemilik/DashboardPemilik.fxml";
                title = "KosKu - Dashboard Pemilik";
                break;
            default: // PENYEWA
                viewPath = "/view/penyewa/MainMenuPenyewa.fxml";
                title = "KosKu - Dashboard Penyewa";
                break;
        }

        Main.navigateTo(viewPath, title);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoToRegister(MouseEvent event) {
        Main.navigateTo("/view/auth/register.fxml", "KosKu - Register");
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        if (passwordField.isVisible()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }
}