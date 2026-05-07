package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;

public class ProfilPenyewaController {

    @FXML
    private TextField tfNama;
    
    @FXML
    private TextField tfEmail;
    
    @FXML
    private TextField tfNoHp;
    
    @FXML
    private TextField tfPekerjaan;
    
    @FXML
    private PasswordField pfPasswordLama;
    
    @FXML
    private PasswordField pfPasswordBaru;

    @FXML
    public void initialize() {
        // Data dummy untuk contoh UI
        tfNama.setText("User Penyewa");
        tfEmail.setText("user@example.com");
        tfNoHp.setText("081234567890");
        tfPekerjaan.setText("Mahasiswa");
    }

    @FXML
    public void simpanProfil() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText("Profil berhasil diperbarui!");
        alert.showAndWait();
    }

    @FXML
    public void ubahPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText("Password berhasil diubah!");
        alert.showAndWait();
    }
}
