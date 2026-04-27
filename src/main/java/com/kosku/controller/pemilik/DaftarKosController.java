package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DaftarKosController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Label totalKosLabel;

    @FXML
    private Label totalKamarLabel;

    @FXML
    private Label kamarTerisiLabel;

    @FXML
    private VBox kosContainer;

    @FXML
    private NavbarPemilikController navbarController;

    @FXML
    public void initialize() {
        // Set active page in navbar
        if (navbarController != null) {
            navbarController.setActivePage("kelolakos");
        }

        // Initialize sort options
        if (sortComboBox != null) {
            sortComboBox.getItems().addAll("Nama (A-Z)", "Nama (Z-A)", "Terbaru", "Kamar Terbanyak");
        }
        
        // Load initial data
        loadKosData();
    }

    private void loadKosData() {
        // TODO: Implement loading data from KosDAO
        System.out.println("Loading kos data for owner...");
    }

    @FXML
    private void handleTambahKos() {
        // TODO: Navigate to Add Kos form
        System.out.println("Navigating to Add Kos form...");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        System.out.println("Searching for: " + query);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        sortComboBox.getSelectionModel().clearSelection();
        loadKosData();
    }
}
