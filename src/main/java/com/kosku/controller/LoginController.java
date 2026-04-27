package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // Dipanggil otomatis setelah FXML dimuat
    }

    @FXML
    public void handleLogin() {
        // Sementara kita langsung navigasi ke MainMenuPenyewa tanpa cek database
        // Nanti tambahkan logika login menggunakan UserDAO di sini
        System.out.println("Login button clicked! Navigating to Dashboard...");
        Main.navigateTo("view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }

    @FXML
    public void handleGoToRegister(MouseEvent event) {
        System.out.println("Navigating to Register page...");
        Main.navigateTo("view/register.fxml", "KosKu - Register");
    }
}
