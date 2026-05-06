package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.dao.PembayaranDAO;
import com.kosku.model.Pembayaran;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.application.Platform;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LaporanPembayaranAdminController {

    @FXML private Button btnKembali;
    @FXML private TableView<Pembayaran> mainTable;
    @FXML private TableColumn<Pembayaran, String> colId;
    @FXML private TableColumn<Pembayaran, String> colBookingId;
    @FXML private TableColumn<Pembayaran, String> colPenyewa;
    @FXML private TableColumn<Pembayaran, String> colJumlah;
    @FXML private TableColumn<Pembayaran, String> colStatus;
    @FXML private TableColumn<Pembayaran, String> colTanggal;
    @FXML private TableColumn<Pembayaran, Void> colAction;

    private PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private ObservableList<Pembayaran> pembayaranList = FXCollections.observableArrayList();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @FXML
    public void initialize() {
        btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdPembayaran())));
        colBookingId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBooking().getIdBooking())));
        colPenyewa.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBooking().getPenyewa().getUsername()));
        colJumlah.setCellValueFactory(cellData -> new SimpleStringProperty(currencyFormat.format(cellData.getValue().getJumlahBayar())));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusVerifikasi().name()));
        colTanggal.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().format(dateFormatter) : "-"
        ));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Untuk admin, kita bisa melihat semua atau hanya yang WAITING.
                // Kita ambil semua pembayaran agar Laporan lengkap, tapi tombol aksi hanya untuk WAITING.
                List<Pembayaran> list = pembayaranDAO.getAllPembayaran();
                Platform.runLater(() -> {
                    pembayaranList.setAll(list);
                    mainTable.setItems(pembayaranList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
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
                    // Hanya tampilkan tombol jika status masih WAITING
                    if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.WAITING) {
                        setGraphic(pane);
                    } else {
                        setGraphic(new Label("Selesai"));
                    }
                }
            }
        });
    }

    private void handleApprove(Pembayaran pembayaran) {
        pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.VERIFIED);
        pembayaranDAO.saveOrUpdate(pembayaran);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pembayaran ID " + pembayaran.getIdPembayaran() + " diverifikasi.");
        alert.showAndWait();
        
        loadData();
    }

    private void handleReject(Pembayaran pembayaran) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menolak pembayaran ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.REJECTED);
                pembayaranDAO.saveOrUpdate(pembayaran);
                loadData();
            }
        });
    }
}
