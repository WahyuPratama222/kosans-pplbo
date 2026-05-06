package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardPemilikController {

    @FXML private NavbarPemilikController navbarController;
    @FXML private Label totalKosLabel;
    @FXML private Label totalKamarLabel;
    @FXML private Label okupansiLabel;
    @FXML private PieChart occupancyChart;
    @FXML private BarChart<String, Number> incomeChart;

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("dashboard");
        }
        setupCharts();
        loadStats();
    }

    private void setupCharts() {
        // 1. PieChart Okupansi (Terisi vs Kosong)
        PieChart.Data terisi = new PieChart.Data("Terisi", 65);
        PieChart.Data kosong = new PieChart.Data("Kosong", 35);
        occupancyChart.getData().addAll(terisi, kosong);
        occupancyChart.setTitle("Persentase Okupansi");

        // 2. BarChart Pendapatan (Dummy Data 4 Bulan Terakhir)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan (Juta Rp)");
        series.getData().add(new XYChart.Data<>("Jan", 12.5));
        series.getData().add(new XYChart.Data<>("Feb", 15.0));
        series.getData().add(new XYChart.Data<>("Mar", 14.2));
        series.getData().add(new XYChart.Data<>("Apr", 18.8));
        
        incomeChart.getData().add(series);
    }

    private void loadStats() {
        // Placeholder untuk logic DAO nantinya
        totalKosLabel.setText("3");
        totalKamarLabel.setText("45");
        okupansiLabel.setText("82%");
    }
}
