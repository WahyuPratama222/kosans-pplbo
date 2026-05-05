package com.kosku.controller.penyewa;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainMenuPenyewaController {

    @FXML
    private TextField searchField;

    @FXML
    private VBox featuredContainer;

    @FXML
    private NavbarPenyewaController navbarController;

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("beranda");
        }
        // Logika untuk memuat data rekomendasi kos bisa ditambahkan di sini
        loadFeaturedKos();
    }

    private void loadFeaturedKos() {
        // Placeholder: Di masa depan akan mengambil data dari KosDAO
        System.out.println("Loading featured kos for tenant...");
    }

    @FXML
    private void handleSearch() {
        // Navigasi ke halaman SearchFilter dengan membawa kata kunci
        Main.navigateTo("view/SearchFilterPenyewa.fxml", "KosKu - Cari Kos");
    }

    @FXML
    private void goToCategoryReguler() {
        System.out.println("Filter: Reguler");
        Main.navigateTo("view/SearchFilterPenyewa.fxml");
    }

    @FXML
    private void goToCategoryExclusive() {
        System.out.println("Filter: Exclusive");
        Main.navigateTo("view/SearchFilterPenyewa.fxml");
    }

    @FXML
    private void goToRiwayat() {
        Main.navigateTo("view/penyewa/RiwayatPenyewa.fxml", "KosKu - Riwayat & Favorit");
    }
}