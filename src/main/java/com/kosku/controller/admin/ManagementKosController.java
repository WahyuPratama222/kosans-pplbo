package com.kosku.controller.admin;

import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManagementKosController {

    @FXML private TableView<Kos> tableKos;
    @FXML private TableColumn<Kos, String> colId;
    @FXML private TableColumn<Kos, String> colNamaKos;
    @FXML private TableColumn<Kos, String> colPemilik;
    @FXML private TableColumn<Kos, String> colTipeKos;
    @FXML private TableColumn<Kos, String> colHarga;
    @FXML private TableColumn<Kos, String> colStatus;
    @FXML private TableColumn<Kos, Void> colAction;
    @FXML private Label lblStatTotalKos;
    @FXML private Label lblStatVerified;
    @FXML private Label lblStatBelumVerified;

    private final KosDAO kosDAO = new KosDAO();
    private final ObservableList<Kos> kosList = FXCollections.observableArrayList();
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdKos())));
        colNamaKos.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNamaKos()));
        colPemilik.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPemilik() != null ? d.getValue().getPemilik().getUsername() : "-"));
        colTipeKos.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipeKos().name()));
        colHarga.setCellValueFactory(d -> new SimpleStringProperty(currencyFmt.format(d.getValue().getHarga())));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                Boolean.TRUE.equals(d.getValue().getIsVerified()) ? "Verified" : "Belum Verifikasi"));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Kos> all = kosDAO.getAllWithKamar();
                long verified    = all.stream().filter(k -> Boolean.TRUE.equals(k.getIsVerified())).count();
                long belumVerif  = all.size() - verified;

                Platform.runLater(() -> {
                    kosList.setAll(all);
                    tableKos.setItems(kosList);
                    if (lblStatTotalKos != null) lblStatTotalKos.setText(String.valueOf(all.size()));
                    if (lblStatVerified != null) lblStatVerified.setText(String.valueOf(verified));
                    if (lblStatBelumVerified != null) lblStatBelumVerified.setText(String.valueOf(belumVerif));
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Verifikasi");
            private final Button btnReject  = new Button("Tolak");
            private final Button btnDelete  = new Button("Hapus");
            private final HBox paneUnverified = new HBox(8, btnApprove, btnReject);
            private final HBox paneVerified   = new HBox(8, btnDelete);

            {
                btnApprove.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 11px;");
                btnReject.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 11px;");
                btnDelete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 11px;");

                btnApprove.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                btnReject.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex()), "Tolak"));
                btnDelete.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex()), "Hapus"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Kos kos = getTableView().getItems().get(getIndex());
                if (kos != null && !Boolean.TRUE.equals(kos.getIsVerified())) setGraphic(paneUnverified);
                else setGraphic(paneVerified);
            }
        });
    }

    private void handleApprove(Kos kos) {
        kos.setIsVerified(true);
        kosDAO.saveOrUpdate(kos);
        new Alert(Alert.AlertType.INFORMATION, "Kos " + kos.getNamaKos() + " diverifikasi.").showAndWait();
        loadData();
    }

    private void handleDelete(Kos kos, String action) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin " + action + " kos " + kos.getNamaKos() + "?",
                ButtonType.YES, ButtonType.NO);
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) { kosDAO.delete(Kos.class, kos.getIdKos()); loadData(); }
        });
    }
}
