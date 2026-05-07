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

public class VerifikasiPenggunaController {

    @FXML private Button btnKembali;
    @FXML private TableView<User> tablePengguna;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colNoHp;
    @FXML private TableColumn<User, Void> colAction;

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdUser())));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colNoHp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomorHp() != null ? cellData.getValue().getNomorHp() : "-"));

        setupActionColumn();
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<User> unverifiedUsers = userDAO.getUnverifiedUsers();
                Platform.runLater(() -> {
                    userList.setAll(unverifiedUsers);
                    tablePengguna.setItems(userList);
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
                    User user = getTableView().getItems().get(getIndex());
                    handleApprove(user);
                });

                btnReject.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleReject(user);
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

    private void handleApprove(User user) {
        user.setIsVerified(true);
        userDAO.saveOrUpdate(user);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pengguna " + user.getUsername() + " telah diverifikasi.");
        alert.showAndWait();
        
        loadData(); // Refresh table
    }

    private void handleReject(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menolak dan menghapus pengguna " + user.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                userDAO.delete(User.class, user.getIdUser());
                loadData(); // Refresh table
            }
        });
    }
}
