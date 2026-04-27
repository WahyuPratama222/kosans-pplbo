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

    @FXML
    public void initialize() {
        if (jenisKelaminBox != null) {
            jenisKelaminBox.getItems().addAll("Laki-Laki", "Perempuan");
        }
    }

    @FXML
    public void handleRegister() {
        // Nanti tambahkan logika registrasi dengan database di sini
        System.out.println("Register button clicked! Navigating to Login...");
        Main.navigateTo("view/login.fxml", "KosKu - Login");
    }

    @FXML
    public void handleGoToLogin(MouseEvent event) {
        System.out.println("Navigating back to Login page...");
        Main.navigateTo("view/login.fxml", "KosKu - Login");
    }
}
