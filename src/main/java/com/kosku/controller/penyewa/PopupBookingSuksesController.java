package com.kosku.controller.penyewa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.kosku.Main;

public class PopupBookingSuksesController {
    
    @FXML private Button btnRiwayat;
    @FXML private Button btnMainMenu;
    
    private Stage popupStage;
    
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }
    
    @FXML
    void goToRiwayat(ActionEvent event) {
        if (popupStage != null) popupStage.close();
        Main.navigateTo("/view/penyewa/RiwayatPenyewa.fxml", "Riwayat Booking");
    }
    
    @FXML
    void goToMainMenu(ActionEvent event) {
        if (popupStage != null) popupStage.close();
        Main.navigateTo("/view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
    }
}
