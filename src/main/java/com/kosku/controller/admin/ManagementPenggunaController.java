package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.application.Platform;

import java.util.List;

public class ManagementPenggunaController {

    @FXML private Button btnKembali;
    @FXML private TableView<User> tablePengguna;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colNoHp;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void> colAction;

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if(btnKembali != null) {
            btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));
        }

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdUser())));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
        colNoHp.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNomorHp() != null ? cellData.getValue().getNomorHp() : "-"
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
                List<User> allUsers = userDAO.getAll(User.class);
                Platform.runLater(() -> {
                    userList.setAll(allUsers);
                    tablePengguna.setItems(userList);
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
                    User user = getTableView().getItems().get(getIndex());
                    handleApprove(user);
                });

                btnReject.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDelete(user, "Tolak pendaftaran");
                });

                btnDelete.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDelete(user, "Hapus akun");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null && !Boolean.TRUE.equals(user.getIsVerified())) {
                        setGraphic(paneUnverified);
                    } else {
                        // Jangan biarkan admin menghapus dirinya sendiri
                        if (user.getRole() == User.Role.ADMIN) {
                            setGraphic(new Label("Admin"));
                        } else {
                            setGraphic(paneVerified);
                        }
                    }
                }
            }
        });
    }

    private void handleApprove(User user) {
        user.setIsVerified(true);
        userDAO.saveOrUpdate(user);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pengguna " + user.getUsername() + " berhasil diverifikasi.");
        alert.showAndWait();
        
        loadData();
    }

    private void handleDelete(User user, String actionType) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin " + actionType + " pengguna " + user.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                userDAO.delete(User.class, user.getIdUser());
                loadData();
            }
        });
    }
}
