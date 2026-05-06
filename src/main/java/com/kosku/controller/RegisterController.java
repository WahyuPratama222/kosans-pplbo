package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class RegisterController {

    @FXML
    private TextField namaLengkapField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> jenisKelaminBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    private com.kosku.dao.UserDAO userDAO = new com.kosku.dao.UserDAO();

    @FXML
    public void initialize() {
        if (jenisKelaminBox != null) {
            jenisKelaminBox.getItems().addAll("Laki-Laki", "Perempuan");
        }
    }

    @FXML
    public void handleRegister() {
        String namaLengkap = namaLengkapField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (namaLengkap == null || namaLengkap.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Semua kolom harus diisi!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Password dan Confirm Password tidak cocok!");
            return;
        }

        try {
            // Cek apakah email sudah terdaftar
            if (userDAO.findByEmail(email.trim()) != null) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Gagal Daftar", "Email sudah terdaftar!");
                return;
            }

            // Enkripsi password menggunakan BCrypt
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

            // Buat objek User baru
            com.kosku.model.User newUser = new com.kosku.model.User();
            newUser.setUsername(namaLengkap.trim()); // Nama Lengkap dipetakan ke username sesuai permintaan
            newUser.setEmail(email.trim());
            newUser.setPassword(hashedPassword);
            newUser.setRole(com.kosku.model.User.Role.PENYEWA); // Default sebagai penyewa
            newUser.setIsVerified(true);

            userDAO.saveOrUpdate(newUser);

            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Sukses", "Registrasi berhasil! Silakan login.");
            Main.navigateTo("view/login.fxml", "KosKu - Login");

        } catch (Exception e) {
            System.err.println("Gagal registrasi: " + e.getMessage());
            e.printStackTrace();
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error Database", "Terjadi kesalahan saat mendaftar.");
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
        Main.navigateTo("view/login.fxml", "KosKu - Login");
    }
}
