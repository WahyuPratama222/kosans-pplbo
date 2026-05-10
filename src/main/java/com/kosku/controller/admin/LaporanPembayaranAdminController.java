package com.kosku.controller.admin;

import com.kosku.dao.PembayaranDAO;
import com.kosku.model.Pembayaran;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import com.kosku.util.PopupManager;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LaporanPembayaranAdminController {

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
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdPembayaran())));
        colBookingId.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getBooking().getIdBooking()));
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colPenyewa.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getBooking().getPenyewa().getUsername());
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
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
                List<Pembayaran> list = pembayaranDAO.getAllPembayaranWithDetails();
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
                    if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.WAITING_ADMIN) {
                        setGraphic(pane);
                    } else if (p != null && p.getStatusVerifikasi() == Pembayaran.StatusVerifikasi.WAITING_PEMILIK) {
                        setGraphic(new Label("Menunggu Pemilik"));
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
        pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.VERIFIED);
        pembayaranDAO.saveOrUpdate(pembayaran);

        PopupManager.showInfo("Sukses", "Pembayaran ID " + pembayaran.getIdPembayaran() + " diverifikasi.");

        loadData();
    }

    private void handleReject(Pembayaran pembayaran) {
        boolean confirmed = PopupManager.showConfirmation("Konfirmasi", "Yakin ingin menolak pembayaran ini?");
        if (confirmed) {
            pembayaran.setStatusVerifikasi(Pembayaran.StatusVerifikasi.REJECTED);
            pembayaranDAO.saveOrUpdate(pembayaran);
            loadData();
        }
    }
}
