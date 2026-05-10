package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.kosku.util.PopupManager;

public class NavbarAdminController {

    @FXML private HBox navMenuContainer;
    @FXML private VBox containerDashboard;
    @FXML private VBox containerPengguna;
    @FXML private VBox containerKos;
    @FXML private VBox containerLaporanPembayaran;
    @FXML private VBox containerLaporanBooking;
    @FXML private Label lblAdminName;

    @FXML
    public void initialize() {
        // Tampilkan nama admin dari session jika ada
        String username = SessionManager.getCurrentUsername();
        if (lblAdminName != null && username != null) {
            lblAdminName.setText("👤 " + username);
        }
    }

    /** Highlight menu aktif di navbar */
    public void setActivePage(String pageName) {
        resetStyles();
        if (pageName == null) return;
        switch (pageName.toLowerCase()) {
            case "dashboard":    highlight(containerDashboard); break;
            case "pengguna":     highlight(containerPengguna); break;
            case "kos":          highlight(containerKos); break;
            case "pembayaran":   highlight(containerLaporanPembayaran); break;
            case "booking":      highlight(containerLaporanBooking); break;
        }
    }

    private void resetStyles() {
        if (navMenuContainer == null) return;
        for (Node n : navMenuContainer.getChildren()) {
            n.getStyleClass().remove("nav-item-active");
        }
    }

    private void highlight(VBox container) {
        if (container != null) container.getStyleClass().add("nav-item-active");
    }

    @FXML
    private void goToDashboard() {
        Main.navigateTo("view/Admin/DashboardAdmin.fxml", "KosKu - Dashboard Admin");
    }

    @FXML
    private void goToPengguna() {
        Main.navigateTo("view/Admin/ManagementPengguna.fxml", "KosKu - Manajemen Pengguna");
    }

    @FXML
    private void goToKos() {
        Main.navigateTo("view/Admin/ManagementKos.fxml", "KosKu - Manajemen Kos");
    }

    @FXML
    private void goToLaporanPembayaran() {
        Main.navigateTo("view/Admin/LaporanPembayaran.fxml", "KosKu - Laporan Pembayaran");
    }

    @FXML
    private void goToLaporanBooking() {
        Main.navigateTo("view/Admin/LaporanBooking.fxml", "KosKu - Laporan Booking");
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = PopupManager.showConfirmation(
            "Konfirmasi Keluar",
            "Sesi Anda akan diakhiri. Yakin ingin logout?"
        );
        if (confirmed) {
            SessionManager.logout();
            Main.navigateTo("/view/auth/login.fxml", "KosKu - Login");
        }
    }
}
