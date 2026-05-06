package com.kosku.controller.penyewa;

import java.util.ResourceBundle;
import java.util.List;
import java.math.BigDecimal;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;
import com.kosku.model.Kamar;

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
    private Button btnDurasiHarian;

    @FXML
    private Button btnDurasiMingguan;

    @FXML
    private Button btnDurasiBulanan;

    @FXML
    private Button btnDurasiTahunan;

    @FXML
    private Label lblJumlahKos;

    @FXML
    private FlowPane flowPaneKos;

    private KosDAO kosDAO;
    private List<Kos> allKosList;
    private List<Kos> filteredKosList;
    private Kos.TipeKos activeTipeFilter = null;
    private Kos.DurasiSewa activeDurasiFilter = null;

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
            // Load semua kos dari database beserta kamarList (JOIN FETCH)
            allKosList = kosDAO.getAllWithKamar();
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
        VBox card = new VBox();
        card.setPrefWidth(460.0);
        card.setSpacing(0.0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.09), 12, 0, 0, 4);");

        // Top Image (AnchorPane)
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(220.0);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(220.0);
        imageView.setFitWidth(460.0);
        imageView.setPreserveRatio(false);

        // Load image
        String imagePath = kos.getFotoKos() != null ? "/" + kos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if(imageUrl != null) {
                imageView.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                URL defaultUrl = getClass().getResource("/images/tesKos.png");
                if (defaultUrl != null) {
                    imageView.setImage(new Image(defaultUrl.toExternalForm()));
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + e.getMessage());
        }

        AnchorPane.setLeftAnchor(imageView, 0.0);
        AnchorPane.setRightAnchor(imageView, 0.0);
        AnchorPane.setTopAnchor(imageView, 0.0);

        Label lblRating = new Label("⭐ 4.9");
        lblRating.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");
        AnchorPane.setRightAnchor(lblRating, 14.0);
        AnchorPane.setTopAnchor(lblRating, 14.0);

        topPane.getChildren().addAll(imageView, lblRating);

        // Content (Bottom VBox)
        VBox contentBox = new VBox();
        contentBox.setSpacing(10.0);
        contentBox.setStyle("-fx-padding: 18 20 20 20;");

        HBox badgesBox = new HBox(8.0);
        String tipeStr = "🚹 Putra";
        String tipeBg = "#2D6BE4";
        if (kos.getTipeKos() != null) {
            if (kos.getTipeKos().name().equals("PUTRI")) {
                tipeStr = "🚺 Putri";
                tipeBg = "#D6336C";
            } else if (kos.getTipeKos().name().equals("CAMPUR")) {
                tipeStr = "👥 Campur";
                tipeBg = "#16A34A";
            }
        }
        Label lblTipe = new Label(tipeStr);
        lblTipe.setStyle("-fx-background-color: " + tipeBg + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");

        String durasiStr = kos.getDurasiSewa() != null ? kos.getDurasiSewa().name() : "BULANAN";
        // Capitalize first letter properly
        durasiStr = durasiStr.substring(0, 1).toUpperCase() + durasiStr.substring(1).toLowerCase();
        Label lblDurasi = new Label(durasiStr);
        lblDurasi.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");

        badgesBox.getChildren().addAll(lblTipe, lblDurasi);

        Label lblNama = new Label(kos.getNamaKos() != null ? kos.getNamaKos() : "N/A");
        lblNama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A2744;");

        Label lblAlamat = new Label("📍 " + (kos.getAlamat() != null ? kos.getAlamat() : "Alamat tidak tersedia"));
        lblAlamat.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        HBox hargaBoxContainer = new HBox();
        hargaBoxContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox hargaBox = new VBox();
        Label lblMulaiDari = new Label("Mulai dari");
        lblMulaiDari.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        BigDecimal hargaMin = kos.getHarga();
        String hargaStr = hargaMin != null ? "Rp " + String.format("%,.0f", hargaMin).replace(",", ".") : "Hubungi Pemilik";
        Label lblHargaVal = new Label(hargaStr);
        lblHargaVal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2D6BE4;");
        hargaBox.getChildren().addAll(lblMulaiDari, lblHargaVal);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hargaBoxContainer.getChildren().addAll(hargaBox, spacer);

        Button btnDetail = new Button("Lihat Detail");
        btnDetail.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 9 18 9 18;");
        btnDetail.setOnAction(e -> showKosDetail(kos));

        contentBox.getChildren().addAll(badgesBox, lblNama, lblAlamat, hargaBoxContainer, btnDetail);

        card.getChildren().addAll(topPane, contentBox);

        return card;
    }

    private void showKosDetail(Kos kos) {
        BigDecimal hargaMin = kos.getHarga();
        int jumlahKamar = kos.getKamarList() != null ? kos.getKamarList().size() : 0;

        StringBuilder detail = new StringBuilder();
        detail.append("=== DETAIL KOS ===\n\n");
        detail.append("Nama: ").append(kos.getNamaKos()).append("\n");
        detail.append("Alamat: ").append(kos.getAlamat()).append("\n");
        if (hargaMin != null) {
            detail.append("Harga Mulai: Rp. ").append(hargaMin.toPlainString()).append("\n");
        } else {
            detail.append("Harga: Hubungi Pemilik\n");
        }
        detail.append("Jumlah Kamar: ").append(jumlahKamar).append("\n");
        detail.append("Status: ").append(Boolean.TRUE.equals(kos.getIsVerified()) ? "Terverifikasi" : "Belum Terverifikasi");
        
        showAlert(Alert.AlertType.INFORMATION, "Detail Kos", detail.toString());
    }

    // ==========================================
    // EVENT HANDLERS
    // ==========================================

    @FXML
    void handleCari(ActionEvent event) {
        applyAllFilters();
    }

    @FXML
    void filterPutra(ActionEvent event) {
        activeTipeFilter = (activeTipeFilter == Kos.TipeKos.PUTRA) ? null : Kos.TipeKos.PUTRA;
        updateTipeButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterPutri(ActionEvent event) {
        activeTipeFilter = (activeTipeFilter == Kos.TipeKos.PUTRI) ? null : Kos.TipeKos.PUTRI;
        updateTipeButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterCampur(ActionEvent event) {
        activeTipeFilter = (activeTipeFilter == Kos.TipeKos.CAMPUR) ? null : Kos.TipeKos.CAMPUR;
        updateTipeButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterHarian(ActionEvent event) {
        activeDurasiFilter = (activeDurasiFilter == Kos.DurasiSewa.HARIAN) ? null : Kos.DurasiSewa.HARIAN;
        updateDurasiButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterMingguan(ActionEvent event) {
        activeDurasiFilter = (activeDurasiFilter == Kos.DurasiSewa.MINGGUAN) ? null : Kos.DurasiSewa.MINGGUAN;
        updateDurasiButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterBulanan(ActionEvent event) {
        activeDurasiFilter = (activeDurasiFilter == Kos.DurasiSewa.BULANAN) ? null : Kos.DurasiSewa.BULANAN;
        updateDurasiButtonsStyles();
        applyAllFilters();
    }

    @FXML
    void filterTahunan(ActionEvent event) {
        activeDurasiFilter = (activeDurasiFilter == Kos.DurasiSewa.TAHUNAN) ? null : Kos.DurasiSewa.TAHUNAN;
        updateDurasiButtonsStyles();
        applyAllFilters();
    }

    private void updateTipeButtonsStyles() {
        String baseStyle = "-fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10; -fx-alignment: CENTER_LEFT; ";
        String stylePutra = baseStyle + "-fx-background-color: #EEF3FF; -fx-text-fill: #2D6BE4; ";
        String stylePutri = baseStyle + "-fx-background-color: #FFF0F5; -fx-text-fill: #D6336C; ";
        String styleCampur = baseStyle + "-fx-background-color: #F0FDF4; -fx-text-fill: #16A34A; ";

        btnTipePutra.setStyle(stylePutra);
        btnTipePutri.setStyle(stylePutri);
        btnTipeCampur.setStyle(styleCampur);

        if (activeTipeFilter != null) {
            switch (activeTipeFilter) {
                case PUTRA: btnTipePutra.setStyle(stylePutra + "-fx-border-color: #2D6BE4; -fx-border-radius: 8; -fx-border-width: 2px; -fx-font-weight: bold;"); break;
                case PUTRI: btnTipePutri.setStyle(stylePutri + "-fx-border-color: #D6336C; -fx-border-radius: 8; -fx-border-width: 2px; -fx-font-weight: bold;"); break;
                case CAMPUR: btnTipeCampur.setStyle(styleCampur + "-fx-border-color: #16A34A; -fx-border-radius: 8; -fx-border-width: 2px; -fx-font-weight: bold;"); break;
            }
        }
    }

    private void updateDurasiButtonsStyles() {
        String defaultStyle = "-fx-background-color: #F8F9FF; -fx-text-fill: #333; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10; -fx-alignment: CENTER_LEFT; -fx-border-color: #D0D9E8; -fx-border-radius: 8;";
        String activeStyle = "-fx-background-color: #E2E8F0; -fx-text-fill: #1A3A6B; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10; -fx-alignment: CENTER_LEFT; -fx-border-color: #1A3A6B; -fx-border-radius: 8; -fx-font-weight: bold; -fx-border-width: 2px;";
        
        btnDurasiHarian.setStyle(defaultStyle);
        btnDurasiMingguan.setStyle(defaultStyle);
        btnDurasiBulanan.setStyle(defaultStyle);
        btnDurasiTahunan.setStyle(defaultStyle);

        if (activeDurasiFilter != null) {
            switch (activeDurasiFilter) {
                case HARIAN: btnDurasiHarian.setStyle(activeStyle); break;
                case MINGGUAN: btnDurasiMingguan.setStyle(activeStyle); break;
                case BULANAN: btnDurasiBulanan.setStyle(activeStyle); break;
                case TAHUNAN: btnDurasiTahunan.setStyle(activeStyle); break;
            }
        }
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
    void applyFilter(ActionEvent event) {
        applyAllFilters();
    }

    private void applyAllFilters() {
        if (allKosList == null) return;
        
        try {
            String keyword = tfCari.getText().trim().toLowerCase();
            String minHargaStr = tfHargaMin.getText().trim();
            String maxHargaStr = tfHargaMax.getText().trim();
            String urutan = cbUrutan.getValue();

            filteredKosList = allKosList.stream()
                .filter(kos -> {
                    // Keyword Match
                    if (!keyword.isEmpty()) {
                        boolean matchNama = kos.getNamaKos() != null && kos.getNamaKos().toLowerCase().contains(keyword);
                        boolean matchAlamat = kos.getAlamat() != null && kos.getAlamat().toLowerCase().contains(keyword);
                        if (!matchNama && !matchAlamat) return false;
                    }
                    
                    // Harga Match
                    BigDecimal hMin = kos.getHarga();
                    if (!minHargaStr.isEmpty() && hMin != null) {
                        try {
                            if (hMin.doubleValue() < Double.parseDouble(minHargaStr)) return false;
                        } catch (NumberFormatException ignored) {}
                    }
                    if (!maxHargaStr.isEmpty() && hMin != null) {
                        try {
                            if (hMin.doubleValue() > Double.parseDouble(maxHargaStr)) return false;
                        } catch (NumberFormatException ignored) {}
                    }
                    
                    // Tipe Kos Match
                    if (activeTipeFilter != null && kos.getTipeKos() != activeTipeFilter) {
                        return false;
                    }
                    
                    // Durasi Sewa Match
                    if (activeDurasiFilter != null && kos.getDurasiSewa() != activeDurasiFilter) {
                        return false;
                    }

                    return true;
                })
                .toList();

            // Sort berdasarkan pilihan
            if (urutan != null && !filteredKosList.isEmpty()) {
                switch (urutan) {
                    case "Harga Terendah":
                        filteredKosList = filteredKosList.stream()
                            .sorted((a, b) -> {
                                BigDecimal hA = a.getHarga();
                                BigDecimal hB = b.getHarga();
                                if (hA == null && hB == null) return 0;
                                if (hA == null) return 1;
                                if (hB == null) return -1;
                                return hA.compareTo(hB);
                            }).toList();
                        break;
                    case "Harga Tertinggi":
                        filteredKosList = filteredKosList.stream()
                            .sorted((a, b) -> {
                                BigDecimal hA = a.getHarga();
                                BigDecimal hB = b.getHarga();
                                if (hA == null && hB == null) return 0;
                                if (hA == null) return 1;
                                if (hB == null) return -1;
                                return hB.compareTo(hA);
                            }).toList();
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
        
        activeTipeFilter = null;
        activeDurasiFilter = null;
        updateTipeButtonsStyles();
        updateDurasiButtonsStyles();
        
        applyAllFilters();
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