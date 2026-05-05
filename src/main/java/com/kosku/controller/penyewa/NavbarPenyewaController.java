package com.kosku.controller.penyewa;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class NavbarPenyewaController {

    @FXML
    private HBox navMenuContainer;

    @FXML
    private VBox containerBeranda;

    @FXML
    private VBox containerCariKos;

    @FXML
    private VBox containerRiwayat;

    @FXML
    private VBox containerChat;

    /**
     * Set the active page to highlight in the navbar
     */
    public void setActivePage(String pageName) {
        resetStyles();
        if (pageName == null) return;

        switch (pageName.toLowerCase()) {
            case "beranda":
            case "dashboard":
                highlight(containerBeranda);
                break;
            case "carikos":
            case "search":
                highlight(containerCariKos);
                break;
            case "riwayat":
            case "favorit":
                highlight(containerRiwayat);
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
    private void goToBeranda() {
        Main.navigateTo("view/penyewa/MainMenuPenyewa.fxml", "KosKu - Beranda");
    }

    @FXML
    private void goToCariKos() {
        Main.navigateTo("view/SearchFilterPenyewa.fxml", "KosKu - Cari Kos");
    }

    @FXML
    private void goToRiwayat() {
        Main.navigateTo("view/penyewa/RiwayatPenyewa.fxml", "KosKu - Riwayat & Favorit");
    }

    @FXML
    private void goToChat() {
        Main.navigateTo("view/penyewa/ChatPenyewa.fxml", "KosKu - Chat");
    }
    
    @FXML
    private void handleLogout() {
        Main.navigateTo("view/login.fxml", "KosKu - Login");
    }
}