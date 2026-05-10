package com.kosku.controller.pemilik;

import com.kosku.dao.PembayaranDAO;
import com.kosku.model.Pembayaran;
import com.kosku.util.SessionManager;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import com.kosku.util.PopupManager;
import com.kosku.service.NotifikasiService;
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
    @FXML private TableColumn<Pembayaran, Void> colAksi;

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
            try {
                var booking = cellData.getValue().getBooking();
                return new SimpleStringProperty(booking != null && booking.getPenyewa() != null ? 
                    booking.getPenyewa().getUsername() : "-");
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
        
        colKos.setCellValueFactory(cellData -> {
            try {
                var booking = cellData.getValue().getBooking();
                if (booking != null && booking.getKamar() != null && booking.getKamar().getKos() != null) {
                    return new SimpleStringProperty(booking.getKamar().getKos().getNamaKos());
                }
                return new SimpleStringProperty("-");
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
        
        colJumlah.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getJumlahBayar()));
            } catch (Exception e) {
                return new SimpleStringProperty("Rp0");
            }
        });
            
        colTanggal.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getCreatedAt() != null ? 
                    cellData.getValue().getCreatedAt().format(dateFormatter) : "-");
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
                
        colStatus.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getStatusVerifikasi().toString());
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });

        setupActionColumn();
    }

    private void loadAnalyticsData() {
        if (SessionManager.getCurrentUser() == null) return;
        int idPemilik = SessionManager.getCurrentUser().getIdUser();

        // 1. Load Stats
        BigDecimal totalRevenue = pembayaranDAO.getTotalPembayaranBulananByPemilik(idPemilik);
        totalRevenueLabel.setText(currencyFormat.format(totalRevenue));
        
        Map<String, Long> statusCounts = pembayaranDAO.getPaymentStatusCountsByPemilik(idPemilik);
        verifiedCountLabel.setText(String.valueOf(statusCounts.getOrDefault("VERIFIED", 0L)));
        waitingCountLabel.setText(String.valueOf(statusCounts.getOrDefault("WAITING_PEMILIK", 0L)));
        
        // 2. Load Table
        ObservableList<Pembayaran> payments = FXCollections.observableArrayList(pembayaranDAO.getPembayaranByPemilik(idPemilik));
        paymentTable.setItems(payments);
        
        // 3. Load Charts
        loadRevenueChart(idPemilik);
        loadStatusPieChart(statusCounts);
    }

    private void loadRevenueChart(int idPemilik) {
        revenueChart.getData().clear();
        Map<String, BigDecimal> monthlyData = pembayaranDAO.getMonthlyRevenueByPemilik(6, idPemilik);
        
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

    private void setupActionColumn() {
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Verifikasi");
            private final Button btnReject = new Button("Tolak");
            private final HBox pane = new HBox(10, btnApprove, btnReject);

            {
                btnApprove.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
                btnReject.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");

                btnApprove.setOnAction(event -> {
                    Pembayaran pembayaran = getTableView().getItems().get(getIndex());
                    handleApprove(pembayaran);
                });

                btnReject.setOnAction(event -> {
                    Pembayaran pembayaran = getTableView().getItems().get(getIndex());
                    handleReject(pembayaran);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pembayaran p = getTableView().getItems().get(getIndex());
                    if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.WAITING_PEMILIK) {
                        setGraphic(pane);
                    } else if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.WAITING_ADMIN) {
                        setGraphic(new Label("Menunggu Admin"));
                    } else if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.REJECTED) {
                        setGraphic(new Label("Ditolak"));
                    } else {
                        setGraphic(new Label("Selesai"));
                    }
                }
            }
        });
    }

    private void handleApprove(Pembayaran pembayaran) {
        pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.WAITING_ADMIN);
        pembayaranDAO.saveOrUpdate(pembayaran);

        // Kirim notifikasi ke penyewa bahwa pembayaran sudah diterima pemilik
        if (pembayaran.getBooking() != null) {
            NotifikasiService.kirimNotifPembayaranVerified(pembayaran.getBooking());
        }

        PopupManager.showInfo("Sukses", "Pembayaran ID " + pembayaran.getIdPembayaran() + " diteruskan ke Admin.");
        loadAnalyticsData();
    }

    private void handleReject(Pembayaran pembayaran) {
        boolean confirmed = PopupManager.showConfirmation("Konfirmasi", "Yakin ingin menolak pembayaran ini?");
        if (confirmed) {
            pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.REJECTED);
            pembayaranDAO.saveOrUpdate(pembayaran);
            loadAnalyticsData();
        }
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
