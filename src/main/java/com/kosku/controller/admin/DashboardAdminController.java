package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardAdminController {

    @FXML private Button btnDashboard;
    @FXML private Button btnManajemenPengguna;
    @FXML private Button btnManajemenKos;
    @FXML private Button btnLaporanPembayaran;
    @FXML private Button btnLaporanBooking;
    @FXML private Button btnVerifikasiPengguna;
    @FXML private Button btnVerifikasiKos;
    @FXML private Button btnLogout;

    @FXML
    public void initialize() {
        if (btnDashboard != null) btnDashboard.setOnAction(e -> openPage("view/Admin/MainMenuAdmin.fxml"));
        if (btnManajemenPengguna != null) btnManajemenPengguna.setOnAction(e -> openPage("view/Admin/ManagementPengguna.fxml"));
        if (btnManajemenKos != null) btnManajemenKos.setOnAction(e -> openPage("view/Admin/ManagementKos.fxml"));
        if (btnLaporanPembayaran != null) btnLaporanPembayaran.setOnAction(e -> openPage("view/Admin/LaporanPembayaran.fxml"));
        if (btnLaporanBooking != null) btnLaporanBooking.setOnAction(e -> openPage("view/Admin/LaporanBooking.fxml"));
        if (btnVerifikasiPengguna != null) btnVerifikasiPengguna.setOnAction(e -> openPage("view/Admin/VerifikasiPengguna.fxml"));
        if (btnVerifikasiKos != null) btnVerifikasiKos.setOnAction(e -> openPage("view/Admin/VerifikasiKos.fxml"));
        if (btnLogout != null) btnLogout.setOnAction(e -> handleLogout());
    }

    private void openPage(String fxmlPath) {
        Main.navigateTo(fxmlPath);
    }

    private void handleLogout() {
        SessionManager.clearSession();
        Main.navigateTo("view/auth/login.fxml", "KosKu - Login");
    }
}
