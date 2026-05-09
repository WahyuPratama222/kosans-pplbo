package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import java.util.Optional;
import com.kosku.util.SessionManager;

public class NavbarPemilikController {

    @FXML
    private VBox containerDashboard;

    @FXML
    private VBox containerKelolaKos;

    @FXML
    private VBox containerBooking;

    @FXML
    private VBox containerLaporan;

    @FXML
    private VBox containerChat;

    @FXML
    private HBox navMenuContainer;

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    /**
     * Set the active page to highlight in the navbar
     * @param pageName dashboard, kelolakos, booking, laporan
     */
    public void setActivePage(String pageName) {
        resetStyles();

        if (pageName == null) return;

        switch (pageName.toLowerCase()) {
            case "dashboard":
                highlight(containerDashboard);
                break;
            case "kelolakos":
            case "manajemenkos":
            case "daftarkos":
                highlight(containerKelolaKos);
                break;
            case "booking":
                highlight(containerBooking);
                break;
            case "laporan":
                highlight(containerLaporan);
                break;
            case "chat":
                highlight(containerChat);
                break;
        }
    }

    private void resetStyles() {
        if (navMenuContainer == null) return;
        for (Node node : navMenuContainer.getChildren()) {
            node.getStyleClass().remove("nav-item-active");
        }
    }

    private void highlight(VBox container) {
        if (container != null) {
            container.getStyleClass().add("nav-item-active");
        }
    }

    @FXML
    private void goToDashboard() {
        com.kosku.Main.navigateTo("view/Pemilik/DashboardPemilik.fxml", "KosKu - Dashboard Pemilik");
    }

    @FXML
    private void goToKelolaKos() {
        com.kosku.Main.navigateTo("view/Pemilik/daftarKosPemilik.fxml", "KosKu - Kelola Kos");
    }

    @FXML
    private void goToBooking() {
        com.kosku.Main.navigateTo("view/Pemilik/BookingPenyewa.fxml", "KosKu - Booking Penyewa");
    }

    @FXML
    private void goToLaporan() {
        com.kosku.Main.navigateTo("view/Pemilik/LaporanPembayaran.fxml", "KosKu - Laporan Pembayaran");
    }

    @FXML
    private void goToChat() {
        com.kosku.Main.navigateTo("view/Pemilik/ChatPemilik.fxml", "KosKu - Chat Penyewa");
    }

    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Keluar");
        confirmAlert.setHeaderText("Keluar dari Akun Pemilik");
        confirmAlert.setContentText("Sesi Anda akan diakhiri.\nApakah Anda yakin ingin keluar dari aplikasi?");
        
        ButtonType btnYes = new ButtonType("Ya, Keluar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(btnYes, btnNo);
        
        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-background-color: #F8FAFC;");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            try {
                SessionManager.logout();
                com.kosku.Main.navigateTo("/view/auth/login.fxml", "KosKu - Login");
                System.out.println("Logout Pemilik berhasil!");
            } catch (Exception e) {
                System.err.println("Error logout: " + e.getMessage());
            }
        }
    }
}
