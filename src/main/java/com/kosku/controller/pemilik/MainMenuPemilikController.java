package com.kosku.controller.pemilik;

import com.kosku.Main;
import javafx.fxml.FXML;

/**
 * Controller untuk mainMenuPemilik.fxml
 * Menangani navigasi quick actions dari halaman utama pemilik.
 */
public class MainMenuPemilikController {

    @FXML
    private NavbarPemilikController navbarController;

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("dashboard");
        }
    }

    @FXML
    private void handleTambahKos() {
        Main.navigateTo("view/Pemilik/daftarKosPemilik.fxml", "KosKu - Tambah Kos");
    }

    @FXML
    private void handleKelolaKamar() {
        Main.navigateTo("view/Pemilik/ManagementKamar.fxml", "KosKu - Kelola Kamar");
    }

    @FXML
    private void handleLihatLaporan() {
        Main.navigateTo("view/Pemilik/LaporanPembayaran.fxml", "KosKu - Laporan Pembayaran");
    }
}
