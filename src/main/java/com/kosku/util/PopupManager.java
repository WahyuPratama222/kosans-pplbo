package com.kosku.util;

import com.kosku.Main;
import com.kosku.controller.PopupMessageController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupManager {

    private static final String POPUP_FXML = "/view/penyewa/PopupMessagePenyewa.fxml";

    public static void showInfo(String title, String message) {
        showPopup("✅", title, message, false, "OK", "Batal");
    }

    public static void showWarning(String title, String message) {
        showPopup("⚠️", title, message, false, "OK", "Batal");
    }

    public static void showError(String title, String message) {
        showPopup("⛔", title, message, false, "OK", "Batal");
    }

    public static boolean showConfirmation(String title, String message) {
        return showPopup("❓", title, message, true, "Ya", "Batal");
    }

    private static boolean showPopup(String icon, String title, String message, boolean showCancel, String okText, String cancelText) {
        try {
            FXMLLoader loader = new FXMLLoader(PopupManager.class.getResource(POPUP_FXML));
            Parent root = loader.load();
            PopupMessageController controller = loader.getController();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            if (Main.getPrimaryStage() != null) {
                popupStage.initOwner(Main.getPrimaryStage());
            }
            controller.setPopupStage(popupStage);
            controller.configure(icon, title, message, showCancel, okText, cancelText);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            return controller.isConfirmed();
        } catch (Exception ex) {
            System.err.println("[PopupManager] Gagal membuka popup: " + ex.getMessage());
            return false;
        }
    }
}
