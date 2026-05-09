package com.kosku.controller.pemilik;

import com.kosku.dao.KamarDAO;
import com.kosku.dao.KosDAO;
import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk ManagementKamar.fxml
 * Mengelola kamar-kamar milik pemilik: tambah, edit, hapus, filter.
 */
public class ManagementKamarController implements Initializable {

    @FXML private NavbarPemilikController navbarController;
    @FXML private ComboBox<Kos> cbPilihKos;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField tfCariKamar;
    @FXML private Label lblTotalKamar;
    @FXML private Label lblKamarTerisi;
    @FXML private GridPane gridKamar;
    @FXML private Button btnTambahKamar;
    @FXML private Button btnCari;
    @FXML private Button btnReset;

    private KosDAO kosDAO = new KosDAO();
    private KamarDAO kamarDAO = new KamarDAO();
    private List<Kos> kosList;
    private List<Kamar> currentKamarList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (navbarController != null) {
            navbarController.setActivePage("kelolakos");
        }

        setupCbStatus();
        loadKosList();

        if (btnTambahKamar != null) {
            btnTambahKamar.setOnAction(e -> handleTambahKamar());
        }
        if (btnCari != null) {
            btnCari.setOnAction(e -> applyFilter());
        }
        if (btnReset != null) {
            btnReset.setOnAction(e -> handleReset());
        }
    }

    private void setupCbStatus() {
        if (cbStatus != null) {
            cbStatus.setItems(FXCollections.observableArrayList("Semua", "Tersedia", "Terisi"));
            cbStatus.getSelectionModel().selectFirst();
            cbStatus.setOnAction(e -> applyFilter());
        }
    }

    private void loadKosList() {
        new Thread(() -> {
            try {
                User currentUser = SessionManager.getCurrentUser();
                if (currentUser == null) return;

                kosList = kosDAO.getByPemilik(currentUser);

                Platform.runLater(() -> {
                    if (cbPilihKos != null) {
                        cbPilihKos.setCellFactory(lv -> new ListCell<>() {
                            @Override
                            protected void updateItem(Kos item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty || item == null ? "" : item.getNamaKos());
                            }
                        });
                        cbPilihKos.setButtonCell(new ListCell<>() {
                            @Override
                            protected void updateItem(Kos item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty || item == null ? "-- Pilih Kos --" : item.getNamaKos());
                            }
                        });
                        cbPilihKos.setItems(FXCollections.observableArrayList(kosList));
                        cbPilihKos.setOnAction(e -> {
                            Kos selected = cbPilihKos.getValue();
                            if (selected != null) loadKamarForKos(selected);
                        });

                        // Pilih kos pertama otomatis
                        if (!kosList.isEmpty()) {
                            cbPilihKos.getSelectionModel().selectFirst();
                            loadKamarForKos(kosList.get(0));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadKamarForKos(Kos kos) {
        new Thread(() -> {
            try {
                currentKamarList = kamarDAO.getByKos(kos);
                long total = currentKamarList != null ? currentKamarList.size() : 0;
                long terisi = currentKamarList != null
                    ? currentKamarList.stream().filter(k -> !Boolean.TRUE.equals(k.getStatusTersedia())).count()
                    : 0;

                Platform.runLater(() -> {
                    if (lblTotalKamar != null) lblTotalKamar.setText(String.valueOf(total));
                    if (lblKamarTerisi != null) lblKamarTerisi.setText(String.valueOf(terisi));
                    displayKamar(currentKamarList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void applyFilter() {
        if (currentKamarList == null) return;

        String keyword = tfCariKamar != null ? tfCariKamar.getText().trim().toLowerCase() : "";
        String statusFilter = cbStatus != null ? cbStatus.getValue() : "Semua";

        List<Kamar> filtered = currentKamarList.stream().filter(k -> {
            // Keyword filter
            if (!keyword.isEmpty() && k.getNomorKamar() != null
                    && !k.getNomorKamar().toLowerCase().contains(keyword)) {
                return false;
            }
            // Status filter
            if ("Tersedia".equals(statusFilter) && !Boolean.TRUE.equals(k.getStatusTersedia())) return false;
            if ("Terisi".equals(statusFilter) && Boolean.TRUE.equals(k.getStatusTersedia())) return false;
            return true;
        }).toList();

        displayKamar(filtered);
    }

    private void handleReset() {
        if (tfCariKamar != null) tfCariKamar.clear();
        if (cbStatus != null) cbStatus.getSelectionModel().selectFirst();
        displayKamar(currentKamarList);
    }

    private void displayKamar(List<Kamar> kamarList) {
        if (gridKamar == null) return;
        gridKamar.getChildren().clear();

        if (kamarList == null || kamarList.isEmpty()) {
            Label empty = new Label("🛏️ Belum ada kamar. Klik \"Tambah Kamar\" untuk mulai.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B; -fx-padding: 30;");
            gridKamar.add(empty, 0, 0);
            return;
        }

        int col = 0, row = 0;
        int maxCols = 4;

        for (Kamar kamar : kamarList) {
            VBox card = createKamarCard(kamar);
            gridKamar.add(card, col, row);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createKamarCard(Kamar kamar) {
        boolean tersedia = Boolean.TRUE.equals(kamar.getStatusTersedia());

        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-border-radius: 12; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        // Status badge
        Label lblStatus = new Label(tersedia ? "✅ Tersedia" : "🔴 Terisi");
        lblStatus.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: "
                + (tersedia ? "#16A34A" : "#DC2626")
                + "; -fx-padding: 3 8; -fx-background-radius: 10;");

        // Nomor kamar
        Label lblNomor = new Label("🛏️ Kamar " + kamar.getNomorKamar());
        lblNomor.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1A3A6B;");

        // Separator line
        Separator sep = new Separator();

        // Catatan (jika ada)
        if (kamar.getCatatanTambahan() != null && !kamar.getCatatanTambahan().isEmpty()) {
            Label lblCatatan = new Label(kamar.getCatatanTambahan());
            lblCatatan.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
            lblCatatan.setWrapText(true);
            card.getChildren().addAll(lblStatus, lblNomor, sep, lblCatatan);
        } else {
            card.getChildren().addAll(lblStatus, lblNomor, sep);
        }

        // Action buttons
        HBox btnBox = new HBox(8);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Button btnToggle = new Button(tersedia ? "Tandai Terisi" : "Tandai Tersedia");
        btnToggle.setStyle("-fx-background-color: " + (tersedia ? "#FEF3C7" : "#D1FAE5") + "; "
                + "-fx-text-fill: " + (tersedia ? "#92400E" : "#065F46") + "; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10;");
        btnToggle.setOnAction(e -> handleToggleStatus(kamar));

        Button btnEdit = new Button("✏️ Edit");
        btnEdit.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2D6BE4; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-color: #BFDBFE; -fx-border-radius: 6;");
        btnEdit.setOnAction(e -> handleEditKamar(kamar));

        Button btnHapus = new Button("🗑️");
        btnHapus.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-font-size: 10px; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-color: #FECACA; -fx-border-radius: 6;");
        btnHapus.setOnAction(e -> handleHapusKamar(kamar));

        btnBox.getChildren().addAll(btnToggle, btnEdit, btnHapus);
        card.getChildren().add(btnBox);

        return card;
    }

    // ========== ACTIONS ==========

    private void handleTambahKamar() {
        Kos selectedKos = cbPilihKos != null ? cbPilihKos.getValue() : null;
        if (selectedKos == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih kos terlebih dahulu.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tambah Kamar Baru");
        dialog.setHeaderText("Tambah kamar untuk: " + selectedKos.getNamaKos());

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        TextField tfNomor = new TextField();
        tfNomor.setPromptText("Contoh: 101, A1, 02B");
        tfNomor.setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6;");

        TextArea taCatatan = new TextArea();
        taCatatan.setPromptText("Catatan tambahan (opsional)");
        taCatatan.setPrefHeight(80);
        taCatatan.setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6;");

        content.getChildren().addAll(
            new Label("Nomor Kamar:"), tfNomor,
            new Label("Catatan:"), taCatatan
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String nomor = tfNomor.getText().trim();
                if (nomor.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", "Nomor kamar tidak boleh kosong.");
                    return;
                }
                try {
                    Kamar kamarBaru = new Kamar();
                    kamarBaru.setKos(selectedKos);
                    kamarBaru.setNomorKamar(nomor);
                    kamarBaru.setStatusTersedia(true);
                    if (!taCatatan.getText().trim().isEmpty()) {
                        kamarBaru.setCatatanTambahan(taCatatan.getText().trim());
                    }
                    kamarDAO.saveOrUpdate(kamarBaru);
                    loadKamarForKos(selectedKos);
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kamar " + nomor + " berhasil ditambahkan!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambah kamar: " + e.getMessage());
                }
            }
        });
    }

    private void handleEditKamar(Kamar kamar) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Kamar");
        dialog.setHeaderText("Edit Kamar: " + kamar.getNomorKamar());

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        TextField tfNomor = new TextField(kamar.getNomorKamar());
        tfNomor.setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6;");

        TextArea taCatatan = new TextArea(kamar.getCatatanTambahan() != null ? kamar.getCatatanTambahan() : "");
        taCatatan.setPrefHeight(80);
        taCatatan.setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6;");

        content.getChildren().addAll(
            new Label("Nomor Kamar:"), tfNomor,
            new Label("Catatan:"), taCatatan
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String nomor = tfNomor.getText().trim();
                if (nomor.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", "Nomor kamar tidak boleh kosong.");
                    return;
                }
                try {
                    kamar.setNomorKamar(nomor);
                    kamar.setCatatanTambahan(taCatatan.getText().trim());
                    kamarDAO.saveOrUpdate(kamar);
                    Kos selected = cbPilihKos != null ? cbPilihKos.getValue() : null;
                    if (selected != null) loadKamarForKos(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kamar berhasil diperbarui!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal memperbarui kamar: " + e.getMessage());
                }
            }
        });
    }

    private void handleToggleStatus(Kamar kamar) {
        boolean current = Boolean.TRUE.equals(kamar.getStatusTersedia());
        String pesan = current ? "kamar ini menjadi TERISI?" : "kamar ini menjadi TERSEDIA?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin mengubah status " + pesan, ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                kamar.setStatusTersedia(!current);
                kamarDAO.saveOrUpdate(kamar);
                Kos selected = cbPilihKos != null ? cbPilihKos.getValue() : null;
                if (selected != null) loadKamarForKos(selected);
            }
        });
    }

    private void handleHapusKamar(Kamar kamar) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus Kamar " + kamar.getNomorKamar() + "?\nData booking terkait akan ikut terhapus.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                try {
                    kamarDAO.delete(Kamar.class, kamar.getIdKamar());
                    Kos selected = cbPilihKos != null ? cbPilihKos.getValue() : null;
                    if (selected != null) loadKamarForKos(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kamar berhasil dihapus.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus kamar: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
