package com.kosku.controller.pemilik;

import javafx.fxml.FXML;

public class LaporanPembayaranController {

    @FXML
    private NavbarPemilikController navbarController;

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("laporan");
        }
    }
}
