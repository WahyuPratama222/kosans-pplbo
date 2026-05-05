package com.kosku.controller.penyewa;

import java.util.ResourceBundle;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;

public class MainMenuController implements Initializable {

    @FXML
    private NavbarController navbarController;

    @FXML
    private TextField tfCari;

    @FXML
    private ComboBox<String> cbUrutan;

    @FXML
    private TextField tfHargaMin;

    @FXML
    private TextField tfHargaMax;

    @FXML
    private Button btnTipePutra;

    @FXML
    private Button btnTipePutri;

    @FXML
    private Button btnTipeCampur;

    @FXML
    private Label lblJumlahKos;

    @FXML
    private FlowPane flowPaneKos;

    private KosDAO kosDAO;
    private List<Kos> allKosList;
    private List<Kos> filteredKosList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Dashboard Main Menu Penyewa berhasil dimuat!");
        
        kosDAO = new KosDAO();
        
        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("beranda");
        }
        
        // Inisialisasi ComboBox sorting
        initializeComboBox();
        
        // Load semua data kos
        loadAllKos();
    }

    // ==========================================
    // INISIALISASI METHOD
    // ==========================================

    private void initializeComboBox() {
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
            "Terbaru",
            "Harga Terendah",
            "Harga Tertinggi",
            "Rating Tertinggi"
        );
        cbUrutan.setItems(sortOptions);
        cbUrutan.getSelectionModel().selectFirst();
        cbUrutan.setOnAction(e -> applyAllFilters());
    }

    private void loadAllKos() {
        try {
            // Load semua kos dari database
            allKosList = kosDAO.getAll(Kos.class);
            filteredKosList = allKosList;
            
            if (allKosList != null && !allKosList.isEmpty()) {
                displayKos(filteredKosList);
                lblJumlahKos.setText("Menampilkan " + filteredKosList.size() + " kos");
            } else {
                lblJumlahKos.setText("Tidak ada kos tersedia");
                showAlert(Alert.AlertType.INFORMATION, "Informasi", 
                    "Data kos belum ada atau sedang memuat");
            }
        } catch (Exception e) {
            System.err.println("Error loading kos data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal memuat data kos: " + e.getMessage());
        }
    }

    private void displayKos(List<Kos> kosList) {
        flowPaneKos.getChildren().clear();
        
        if (kosList == null || kosList.isEmpty()) {
            Label lblEmpty = new Label("Tidak ada kos yang sesuai");
            lblEmpty.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            flowPaneKos.getChildren().add(lblEmpty);
            return;
        }
        
        for (Kos kos : kosList) {
            VBox kosCard = createKosCard(kos);
            flowPaneKos.getChildren().add(kosCard);
        }
    }

    private VBox createKosCard(Kos kos) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; " +
                     "-fx-background-color: white; -fx-padding: 15; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefWidth(280);
        card.setPrefHeight(320);
        
        // Nama Kos
        Label lblNama = new Label(kos.getNamaKos() != null ? kos.getNamaKos() : "N/A");
        lblNama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A3A6B;");
        
        // Alamat
        Label lblAlamat = new Label(kos.getAlamat() != null ? kos.getAlamat() : "Alamat tidak tersedia");
        lblAlamat.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-wrap-text: true;");
        
        // Harga
        Label lblHarga = new Label("Mulai dari: Rp. " + 
            (kos.getHargaMin() != null ? kos.getHargaMin().toString() : "0"));
        lblHarga.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D6BE4;");
        
        // Fasilitas (dummy)
        Label lblFasilitas = new Label("✓ WiFi  ✓ AC  ✓ Parkir");
        lblFasilitas.setStyle("-fx-font-size: 11px; -fx-text-fill: #4CAF50;");
        
        // Button Lihat Detail
        Button btnDetail = new Button("Lihat Detail");
        btnDetail.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnDetail.setOnAction(e -> showKosDetail(kos));
        btnDetail.setPrefWidth(Double.MAX_VALUE);
        
        card.getChildren().addAll(lblNama, lblAlamat, lblHarga, lblFasilitas, new Region(), btnDetail);
        return card;
    }

    private void showKosDetail(Kos kos) {
        StringBuilder detail = new StringBuilder();
        detail.append("=== DETAIL KOS ===\n\n");
        detail.append("Nama: ").append(kos.getNamaKos()).append("\n");
        detail.append("Alamat: ").append(kos.getAlamat()).append("\n");
        detail.append("Harga Min: Rp. ").append(kos.getHargaMin()).append("\n");
        detail.append("Harga Max: Rp. ").append(kos.getHargaMax()).append("\n");
        detail.append("Status: ").append(kos.getIsVerified() ? "Terverifikasi" : "Belum Terverifikasi");
        
        showAlert(Alert.AlertType.INFORMATION, "Detail Kos", detail.toString());
    }

    // ==========================================
    // EVENT HANDLERS
    // ==========================================

    @FXML
    void handleCari(ActionEvent event) {
        String keyword = tfCari.getText().trim();
        
        if (keyword.isEmpty()) {
            loadAllKos();
            return;
        }
        
        try {
            // Cari kos berdasarkan keyword
            filteredKosList = allKosList.stream()
                .filter(kos -> kos.getNamaKos().toLowerCase().contains(keyword.toLowerCase()) ||
                              kos.getAlamat().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
            
            displayKos(filteredKosList);
            lblJumlahKos.setText("Ditemukan " + filteredKosList.size() + " kos");
        } catch (Exception e) {
            System.err.println("Error searching: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mencari kos");
        }
    }

    @FXML
    void filterPutra(ActionEvent event) {
        System.out.println("Filter cepat: Kos Putra");
        // TODO: Filter berdasarkan tipe kos (jika ada field di model)
        showAlert(Alert.AlertType.INFORMATION, "Info", "Filter Putra akan segera tersedia");
    }

    @FXML
    void filterPutri(ActionEvent event) {
        System.out.println("Filter cepat: Kos Putri");
        showAlert(Alert.AlertType.INFORMATION, "Info", "Filter Putri akan segera tersedia");
    }

    @FXML
    void filterCampur(ActionEvent event) {
        System.out.println("Filter cepat: Kos Campur");
        showAlert(Alert.AlertType.INFORMATION, "Info", "Filter Campur akan segera tersedia");
    }

    @FXML
    void filterAC(ActionEvent event) {
        System.out.println("Filter cepat: Fasilitas AC");
        showAlert(Alert.AlertType.INFORMATION, "Info", "Filter AC akan segera tersedia");
    }

    @FXML
    void filterWifi(ActionEvent event) {
        System.out.println("Filter cepat: Fasilitas WiFi");
        showAlert(Alert.AlertType.INFORMATION, "Info", "Filter WiFi akan segera tersedia");
    }

    @FXML
    void filterMurah(ActionEvent event) {
        System.out.println("Filter cepat: Harga Termurah");
        if (allKosList != null && !allKosList.isEmpty()) {
            filteredKosList = allKosList.stream()
                .sorted((a, b) -> a.getHargaMin().compareTo(b.getHargaMin()))
                .toList();
            displayKos(filteredKosList);
            lblJumlahKos.setText("Filter: Harga Termurah (" + filteredKosList.size() + " kos)");
        }
    }

    @FXML
    void applyFilter(ActionEvent event) {
        applyAllFilters();
    }

    private void applyAllFilters() {
        try {
            String minHargaStr = tfHargaMin.getText().trim();
            String maxHargaStr = tfHargaMax.getText().trim();
            String urutan = cbUrutan.getValue();
            
            // Filter berdasarkan harga
            filteredKosList = allKosList.stream()
                .filter(kos -> {
                    if (!minHargaStr.isEmpty()) {
                        try {
                            if (kos.getHargaMin().doubleValue() < Double.parseDouble(minHargaStr)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    }
                    if (!maxHargaStr.isEmpty()) {
                        try {
                            if (kos.getHargaMax().doubleValue() > Double.parseDouble(maxHargaStr)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    }
                    return true;
                })
                .toList();
            
            // Sort berdasarkan pilihan
            if (urutan != null) {
                switch (urutan) {
                    case "Harga Terendah":
                        filteredKosList = filteredKosList.stream()
                            .sorted((a, b) -> a.getHargaMin().compareTo(b.getHargaMin()))
                            .toList();
                        break;
                    case "Harga Tertinggi":
                        filteredKosList = filteredKosList.stream()
                            .sorted((a, b) -> b.getHargaMax().compareTo(a.getHargaMax()))
                            .toList();
                        break;
                }
            }
            
            displayKos(filteredKosList);
            lblJumlahKos.setText("Ditemukan " + filteredKosList.size() + " kos");
        } catch (Exception e) {
            System.err.println("Error applying filter: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menerapkan filter");
        }
    }

    @FXML
    void resetFilter(ActionEvent event) {
        tfCari.clear();
        tfHargaMin.clear();
        tfHargaMax.clear();
        cbUrutan.getSelectionModel().selectFirst();
        loadAllKos();
        System.out.println("Filter telah direset.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void lihatDetail1(ActionEvent event) {
        System.out.println("Membuka detail Kos 1...");
        // TODO: Pindah ke halaman detail kos untuk item 1
    }

    @FXML
    void lihatDetail2(ActionEvent event) {
        System.out.println("Membuka detail Kos 2...");
        // TODO: Pindah ke halaman detail kos untuk item 2
    }

    @FXML
    void lihatDetail3(ActionEvent event) {
        System.out.println("Membuka detail Kos 3...");
        // TODO: Pindah ke halaman detail kos untuk item 3
    }

    @FXML
    void muatLebih(ActionEvent event) {
        System.out.println("Memuat lebih banyak data kos...");
        // TODO: Ambil data tambahan dari database dan masukkan ke flowPaneKos
    }

}