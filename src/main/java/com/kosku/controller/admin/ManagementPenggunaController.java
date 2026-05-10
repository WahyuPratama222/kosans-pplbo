package com.kosku.controller.admin;

import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import com.kosku.util.PopupManager;

import java.util.List;

public class ManagementPenggunaController {

    @FXML private TableView<User> tablePengguna;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colNoHp;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void> colAction;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatAdmin;
    @FXML private Label lblStatPemilik;
    @FXML private Label lblStatPenyewa;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdUser())));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole().name()));
        colNoHp.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNomorHp() != null ? d.getValue().getNomorHp() : "-"));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                Boolean.TRUE.equals(d.getValue().getIsVerified()) ? "Verified" : "Belum Verifikasi"));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<User> all = userDAO.getAll(User.class);
                long admins   = all.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
                long pemiliks = all.stream().filter(u -> u.getRole() == User.Role.PEMILIK).count();
                long penyewas = all.stream().filter(u -> u.getRole() == User.Role.PENYEWA).count();

                Platform.runLater(() -> {
                    userList.setAll(all);
                    tablePengguna.setItems(userList);
                    if (lblStatTotal  != null) lblStatTotal.setText(String.valueOf(all.size()));
                    if (lblStatAdmin  != null) lblStatAdmin.setText(String.valueOf(admins));
                    if (lblStatPemilik != null) lblStatPemilik.setText(String.valueOf(pemiliks));
                    if (lblStatPenyewa != null) lblStatPenyewa.setText(String.valueOf(penyewas));
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
                btnReject.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex()), "Tolak pendaftaran"));
                btnDelete.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex()), "Hapus akun"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User user = getTableView().getItems().get(getIndex());
                if (user == null) return;
                if (!Boolean.TRUE.equals(user.getIsVerified())) setGraphic(paneUnverified);
                else if (user.getRole() == User.Role.ADMIN) setGraphic(new Label("Admin"));
                else setGraphic(paneVerified);
            }
        });
    }

    private void handleApprove(User user) {
        user.setIsVerified(true);
        userDAO.saveOrUpdate(user);
        PopupManager.showInfo("Sukses", "Pengguna " + user.getUsername() + " diverifikasi.");
        loadData();
    }

    private void handleDelete(User user, String action) {
        boolean confirmed = PopupManager.showConfirmation("Konfirmasi",
                "Yakin " + action + " pengguna " + user.getUsername() + "?");
        if (confirmed) { userDAO.delete(User.class, user.getIdUser()); loadData(); }
    }
}
