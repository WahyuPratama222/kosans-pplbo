package com.kosku.controller.pemilik;

import com.kosku.dao.PembayaranDAO;
import com.kosku.model.Pembayaran;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class LaporanPembayaranController {

    @FXML private NavbarPemilikController navbarController;
    @FXML private Label totalRevenueLabel;
    @FXML private Label verifiedCountLabel;
    @FXML private Label waitingCountLabel;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private PieChart statusPieChart;
    
    @FXML private TableView<Pembayaran> paymentTable;
    @FXML private TableColumn<Pembayaran, String> colNo;
    @FXML private TableColumn<Pembayaran, String> colPenyewa;
    @FXML private TableColumn<Pembayaran, String> colKos;
    @FXML private TableColumn<Pembayaran, String> colJumlah;
    @FXML private TableColumn<Pembayaran, String> colTanggal;
    @FXML private TableColumn<Pembayaran, String> colStatus;

    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("laporan");
        }
        
        setupTable();
        loadAnalyticsData();
    }

    private void setupTable() {
        colNo.setCellValueFactory(cellData -> {
            int index = paymentTable.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(index));
        });
        
        colPenyewa.setCellValueFactory(cellData -> {
            var booking = cellData.getValue().getBooking();
            return new SimpleStringProperty(booking != null && booking.getPenyewa() != null ? 
                booking.getPenyewa().getUsername() : "-");
        });
        
        colKos.setCellValueFactory(cellData -> {
            var booking = cellData.getValue().getBooking();
            if (booking != null && booking.getKamar() != null && booking.getKamar().getKos() != null) {
                return new SimpleStringProperty(booking.getKamar().getKos().getNamaKos());
            }
            return new SimpleStringProperty("-");
        });
        
        colJumlah.setCellValueFactory(cellData -> 
            new SimpleStringProperty(currencyFormat.format(cellData.getValue().getJumlahBayar())));
            
        colTanggal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? 
                cellData.getValue().getCreatedAt().format(dateFormatter) : "-"));
                
        colStatus.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatusVerifikasi().toString()));
    }

    private void loadAnalyticsData() {
        // 1. Load Stats
        BigDecimal totalRevenue = pembayaranDAO.getTotalPembayaranBulanan();
        totalRevenueLabel.setText(currencyFormat.format(totalRevenue));
        
        Map<String, Long> statusCounts = pembayaranDAO.getPaymentStatusCounts();
        verifiedCountLabel.setText(String.valueOf(statusCounts.getOrDefault("VERIFIED", 0L)));
        waitingCountLabel.setText(String.valueOf(statusCounts.getOrDefault("WAITING", 0L)));
        
        // 2. Load Table
        ObservableList<Pembayaran> payments = FXCollections.observableArrayList(pembayaranDAO.getAllPembayaran());
        paymentTable.setItems(payments);
        
        // 3. Load Charts
        loadRevenueChart();
        loadStatusPieChart(statusCounts);
    }

    private void loadRevenueChart() {
        revenueChart.getData().clear();
        Map<String, BigDecimal> monthlyData = pembayaranDAO.getMonthlyRevenue(6);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan");
        
        monthlyData.forEach((month, amount) -> {
            series.getData().add(new XYChart.Data<>(month, amount));
        });
        
        revenueChart.getData().add(series);
    }

    private void loadStatusPieChart(Map<String, Long> statusCounts) {
        statusPieChart.getData().clear();
        statusCounts.forEach((status, count) -> {
            statusPieChart.getData().add(new PieChart.Data(status + " (" + count + ")", count));
        });
    }

    @FXML
    private void handleExport() {
        System.out.println("Exporting financial data to CSV...");
        // Logic export bisa ditambahkan menggunakan OpenCSV atau manual string building
    }

    @FXML
    private void handlePrint() {
        System.out.println("Printing financial report...");
    }
}
