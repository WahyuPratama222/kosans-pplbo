package com.kosku.controller.pemilik;

import com.kosku.dao.BookingDAO;
import com.kosku.dao.KamarDAO;
import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardPemilikController {

    @FXML private NavbarPemilikController navbarController;
    @FXML private Label totalKosLabel;
    @FXML private Label totalKamarLabel;
    @FXML private Label okupansiLabel;
    @FXML private PieChart occupancyChart;
    @FXML private BarChart<String, Number> incomeChart;

    private KosDAO kosDAO = new KosDAO();
    private KamarDAO kamarDAO = new KamarDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("dashboard");
        }
        loadStats();
        setupCharts();
    }

    private void loadStats() {
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) return;

            List<Kos> kosList = kosDAO.getByPemilik(currentUser);
            int totalKos = kosList != null ? kosList.size() : 0;

            long totalKamar = 0, kamarTersedia = 0;
            if (kosList != null) {
                for (Kos kos : kosList) {
                    totalKamar     += kamarDAO.countTotalKamar(kos);
                    kamarTersedia  += kamarDAO.countKamarTersedia(kos);
                }
            }
            long kamarTerisi = totalKamar - kamarTersedia;
            double pctOkupansi = totalKamar > 0
                ? Math.round((kamarTerisi * 100.0) / totalKamar)
                : 0;

            if (totalKosLabel   != null) totalKosLabel.setText(String.valueOf(totalKos));
            if (totalKamarLabel != null) totalKamarLabel.setText(String.valueOf(totalKamar));
            if (okupansiLabel   != null) okupansiLabel.setText((int) pctOkupansi + "%");

            // Perbarui pie chart dengan data real
            if (occupancyChart != null) {
                occupancyChart.getData().clear();
                occupancyChart.getData().addAll(
                    new PieChart.Data("Terisi", kamarTerisi),
                    new PieChart.Data("Tersedia", kamarTersedia)
                );
                occupancyChart.setTitle("Persentase Okupansi");
            }

        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            e.printStackTrace();
            // Fallback ke nilai default
            if (totalKosLabel   != null) totalKosLabel.setText("0");
            if (totalKamarLabel != null) totalKamarLabel.setText("0");
            if (okupansiLabel   != null) okupansiLabel.setText("0%");
        }
    }

    private void setupCharts() {
        // BarChart pendapatan masih dummy karena belum ada tabel pendapatan bulanan
        if (incomeChart != null) {
            incomeChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Pendapatan (Juta Rp)");
            series.getData().add(new XYChart.Data<>("Jan", 12.5));
            series.getData().add(new XYChart.Data<>("Feb", 15.0));
            series.getData().add(new XYChart.Data<>("Mar", 14.2));
            series.getData().add(new XYChart.Data<>("Apr", 18.8));
            incomeChart.getData().add(series);
        }
    }

    // ==================== Navigation ====================

    @FXML
    private void goToDaftarKos() {
        com.kosku.Main.navigateTo("view/Pemilik/daftarKosPemilik.fxml", "KosKu - Kelola Kos");
    }

    @FXML
    private void goToManajemenKamar() {
        com.kosku.Main.navigateTo("view/Pemilik/ManagementKamar.fxml", "KosKu - Kelola Kamar");
    }

    @FXML
    private void goToChat() {
        com.kosku.Main.navigateTo("view/Pemilik/ChatPemilik.fxml", "KosKu - Chat Penyewa");
    }

    @FXML
    private void goToBooking() {
        com.kosku.Main.navigateTo("view/Pemilik/BookingPenyewa.fxml", "KosKu - Booking Penyewa");
    }
}
