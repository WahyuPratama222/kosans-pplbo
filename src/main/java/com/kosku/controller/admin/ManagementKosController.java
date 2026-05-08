package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.application.Platform;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManagementKosController {

    @FXML private Button btnKembali;
    @FXML private TableView<Kos> tableKos;
    @FXML private TableColumn<Kos, String> colId;
    @FXML private TableColumn<Kos, String> colNamaKos;
    @FXML private TableColumn<Kos, String> colPemilik;
    @FXML private TableColumn<Kos, String> colTipeKos;
    @FXML private TableColumn<Kos, String> colHarga;
    @FXML private TableColumn<Kos, String> colStatus;
    @FXML private TableColumn<Kos, Void> colAction;

    private KosDAO kosDAO = new KosDAO();
    private ObservableList<Kos> kosList = FXCollections.observableArrayList();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        if(btnKembali != null) {
            btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));
        }

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKos())));
        colNamaKos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaKos()));
        colPemilik.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPemilik() != null ? cellData.getValue().getPemilik().getUsername() : "-"
        ));
        colTipeKos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipeKos().name()));
        colHarga.setCellValueFactory(cellData -> new SimpleStringProperty(
                currencyFormat.format(cellData.getValue().getHarga())
        ));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                Boolean.TRUE.equals(cellData.getValue().getIsVerified()) ? "Verified" : "Belum Verifikasi"
        ));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Fetch all Kos
                List<Kos> allKos = kosDAO.getAllWithKamar();
                Platform.runLater(() -> {
                    kosList.setAll(allKos);
                    tableKos.setItems(kosList);
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
            private final Button btnDelete = new Button("Hapus");
            
            private final HBox paneUnverified = new HBox(10, btnApprove, btnReject);
            private final HBox paneVerified = new HBox(10, btnDelete);

            {
                btnApprove.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                btnReject.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                btnDelete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");

                btnApprove.setOnAction(event -> {
                    Kos kos = getTableView().getItems().get(getIndex());
                    handleApprove(kos);
                });

                btnReject.setOnAction(event -> {
                    Kos kos = getTableView().getItems().get(getIndex());
                    handleDelete(kos, "Tolak");
                });

                btnDelete.setOnAction(event -> {
                    Kos kos = getTableView().getItems().get(getIndex());
                    handleDelete(kos, "Hapus");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Kos kos = getTableView().getItems().get(getIndex());
                    if (kos != null && !Boolean.TRUE.equals(kos.getIsVerified())) {
                        setGraphic(paneUnverified);
                    } else {
                        setGraphic(paneVerified);
                    }
                }
            }
        });
    }

    private void handleApprove(Kos kos) {
        kos.setIsVerified(true);
        kosDAO.saveOrUpdate(kos);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kos " + kos.getNamaKos() + " berhasil diverifikasi.");
        alert.showAndWait();
        
        loadData();
    }

    private void handleDelete(Kos kos, String actionName) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin melakukan aksi " + actionName + " pada kos " + kos.getNamaKos() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                kosDAO.delete(Kos.class, kos.getIdKos());
                loadData();
            }
        });
    }
}
