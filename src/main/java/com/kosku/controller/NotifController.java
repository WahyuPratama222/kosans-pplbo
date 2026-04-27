package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NotifController {

    @FXML
    private Button closeButton;

    @FXML
    public void handleClose() {
        System.out.println("Menutup Notifikasi, kembali ke Dashboard...");
        Main.navigateTo("view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }
}
