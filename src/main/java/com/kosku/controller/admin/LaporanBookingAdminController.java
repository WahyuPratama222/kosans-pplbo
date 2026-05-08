package com.kosku.controller.admin;

import com.kosku.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LaporanBookingAdminController {

    @FXML private Button btnKembali;

    @FXML
    public void initialize() {
        if(btnKembali != null) {
            btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));
        }
    }
}
