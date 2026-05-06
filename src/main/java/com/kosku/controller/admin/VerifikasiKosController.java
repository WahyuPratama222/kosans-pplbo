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

public class VerifikasiKosController {

    @FXML private Button btnKembali;
    @FXML private TableView<Kos> tableKos;
    @FXML private TableColumn<Kos, String> colId;
    @FXML private TableColumn<Kos, String> colNamaKos;
    @FXML private TableColumn<Kos, String> colPemilik;
    @FXML private TableColumn<Kos, String> colTipeKos;
    @FXML private TableColumn<Kos, String> colHarga;
    @FXML private TableColumn<Kos, Void> colAction;

    private KosDAO kosDAO = new KosDAO();
    private ObservableList<Kos> kosList = FXCollections.observableArrayList();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKos())));
        colNamaKos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaKos()));
        colPemilik.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPemilik() != null ? cellData.getValue().getPemilik().getUsername() : "-"
        ));
        colTipeKos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipeKos().name()));
        colHarga.setCellValueFactory(cellData -> new SimpleStringProperty(
                currencyFormat.format(cellData.getValue().getHarga())
        ));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Gunakan getAllWithKamar() atau query khusus jika perlu fetch relasi. 
                // Karena kita menggunakan session terpisah di thread, kita perlu pastikan eager fetch jika perlu.
                List<Kos> unverifiedKos = kosDAO.getUnverifiedKos();
                Platform.runLater(() -> {
                    kosList.setAll(unverifiedKos);
                    tableKos.setItems(kosList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Setujui");
            private final Button btnReject = new Button("Tolak");
            private final HBox pane = new HBox(10, btnApprove, btnReject);

            {
                btnApprove.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-cursor: hand;");
                btnReject.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand;");

                btnApprove.setOnAction(event -> {
                    Kos kos = getTableView().getItems().get(getIndex());
                    handleApprove(kos);
                });

                btnReject.setOnAction(event -> {
                    Kos kos = getTableView().getItems().get(getIndex());
                    handleReject(kos);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void handleApprove(Kos kos) {
        kos.setIsVerified(true);
        kosDAO.saveOrUpdate(kos);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kos " + kos.getNamaKos() + " telah diverifikasi.");
        alert.showAndWait();
        
        loadData(); // Refresh table
    }

    private void handleReject(Kos kos) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menolak dan menghapus pendaftaran kos " + kos.getNamaKos() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                kosDAO.delete(Kos.class, kos.getIdKos());
                loadData(); // Refresh table
            }
        });
    }
}
