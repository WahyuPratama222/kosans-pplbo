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
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert("Error", "Email dan Password tidak boleh kosong!");
            return;
        }

        try {
            User user = userDAO.findByEmail(email.trim());
            if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
                com.kosku.util.SessionManager.login(user);
                
                // Routing berdasarkan Role
                if (user.getRole() == User.Role.PENYEWA) {
                    Main.navigateTo("/view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard Penyewa");
                } else if (user.getRole() == User.Role.PEMILIK) {
                    Main.navigateTo("/view/pemilik/DashboardPemilik.fxml", "KosKu - Dashboard Pemilik");
                } else {
                    Main.navigateTo("/view/admin/DashboardAdmin.fxml", "KosKu - Dashboard Admin");
                }
            } else {
                showAlert("Gagal Login", "Email atau Password salah!");
            }
        } catch (Exception e) {
            System.err.println("Gagal login: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error Database", "Terjadi kesalahan pada sistem.");
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
