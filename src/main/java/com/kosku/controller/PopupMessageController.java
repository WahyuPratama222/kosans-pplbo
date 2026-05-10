package com.kosku.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PopupMessageController {

    @FXML private Label lblIcon;
    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Button btnOk;
    @FXML private Button btnCancel;
    @FXML private HBox buttonRow;

    private Stage popupStage;
    private boolean confirmed = false;

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public void configure(String icon, String title, String message, boolean showCancel, String okText, String cancelText) {
        lblIcon.setText(icon != null ? icon : "");
        lblTitle.setText(title != null ? title : "");
        lblMessage.setText(message != null ? message : "");
        btnOk.setText(okText != null ? okText : "OK");
        btnCancel.setText(cancelText != null ? cancelText : "Batal");
        btnCancel.setVisible(showCancel);
        btnCancel.setManaged(showCancel);
    }

    @FXML
    void handleOk(ActionEvent event) {
        confirmed = true;
        closePopup();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        confirmed = false;
        closePopup();
    }

    private void closePopup() {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
