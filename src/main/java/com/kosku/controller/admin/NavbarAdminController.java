package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavbarAdminController {

    @FXML private Button btnLogout;

    @FXML
    public void initialize() {
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> handleLogout());
        }
    }

    private void handleLogout() {
        SessionManager.clearSession();
        Main.navigateTo("view/auth/login.fxml", "KosKu - Login");
    }
}
