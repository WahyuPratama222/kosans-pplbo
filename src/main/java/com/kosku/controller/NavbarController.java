package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.event.Event;

public class NavbarController {

    @FXML
    public void goToMainMenu(MouseEvent event) {
        Main.navigateTo("view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }

    @FXML
    public void goToRiwayat(MouseEvent event) {
        Main.navigateTo("view/penyewa/RiwayatPenyewa.fxml", "KosKu - Riwayat");
    }

    @FXML
    public void goToChat(MouseEvent event) {
        Main.navigateTo("view/penyewa/ChatPenyewa.fxml", "KosKu - Chat");
    }

    @FXML
    public void goToNotif(MouseEvent event) {
        Main.navigateTo("view/penyewa/NotifPenyewa.fxml", "KosKu - Notifikasi");
    }

    @FXML
    public void handleLogout(Event event) {
        System.out.println("Logout diklik! Kembali ke Login...");
        Main.navigateTo("view/login.fxml", "KosKu - Login");
    }

    @FXML
    public void goToProfil(Event event) {
        Main.navigateTo("view/penyewa/ProfilPenyewa.fxml", "KosKu - Profil");
    }
}
