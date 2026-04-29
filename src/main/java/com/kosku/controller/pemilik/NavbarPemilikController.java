package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

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
        System.out.println("Navigating to Dashboard...");
    }

    @FXML
    private void goToKelolaKos() {
        System.out.println("Navigating to Kelola Kos...");
    }

    @FXML
    private void goToBooking() {
        System.out.println("Navigating to Booking...");
    }

    @FXML
    private void goToLaporan() {
        System.out.println("Navigating to Laporan...");
    }
    
    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
    }
}
