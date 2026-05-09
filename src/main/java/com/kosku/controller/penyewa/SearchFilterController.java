package com.kosku.controller.penyewa;

import com.kosku.dao.KosDAO;
import com.kosku.dao.ReviewDAO;
import com.kosku.model.Kos;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SearchFilterController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private CheckBox regulerCheck;
    @FXML private CheckBox exclusiveCheck;
    @FXML private CheckBox putraCheck;
    @FXML private CheckBox putriCheck;
    @FXML private CheckBox campurCheck;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox acCheck;
    @FXML private CheckBox kmDalamCheck;
    @FXML private CheckBox parkirCheck;
    @FXML private ComboBox<String> sortCombo;
    @FXML private FlowPane kosGrid;
    @FXML private Label lblJumlahHasil;

    private KosDAO kosDAO = new KosDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private List<Kos> allKosList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi ComboBox sorting
        sortCombo.setItems(FXCollections.observableArrayList(
            "Terbaru", "Harga Terendah", "Harga Tertinggi", "Rating Tertinggi"
        ));
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applyFilter());

        // Load semua kos dari database
        loadAllKos();
    }

    private void loadAllKos() {
        try {
            allKosList = kosDAO.getAllWithKamar();
            displayKos(allKosList);
        } catch (Exception e) {
            System.err.println("Error loading kos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===================== FXML HANDLERS =====================

    @FXML
    void handleSearch(ActionEvent event) {
        applyFilter();
    }

    @FXML
    void handleApplyFilter(ActionEvent event) {
        applyFilter();
    }

    @FXML
    void handleResetFilter(ActionEvent event) {
        searchField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        if (regulerCheck != null) regulerCheck.setSelected(false);
        if (exclusiveCheck != null) exclusiveCheck.setSelected(false);
        if (putraCheck != null) putraCheck.setSelected(false);
        if (putriCheck != null) putriCheck.setSelected(false);
        if (campurCheck != null) campurCheck.setSelected(false);
        if (wifiCheck != null) wifiCheck.setSelected(false);
        if (acCheck != null) acCheck.setSelected(false);
        if (kmDalamCheck != null) kmDalamCheck.setSelected(false);
        if (parkirCheck != null) parkirCheck.setSelected(false);
        sortCombo.getSelectionModel().selectFirst();
        displayKos(allKosList);
    }

    // ===================== FILTER LOGIC =====================

    private void applyFilter() {
        if (allKosList == null) return;

        try {
            String keyword = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
            String minStr  = minPriceField.getText() != null ? minPriceField.getText().trim() : "";
            String maxStr  = maxPriceField.getText() != null ? maxPriceField.getText().trim() : "";

            boolean filterPutra  = putraCheck != null  && putraCheck.isSelected();
            boolean filterPutri  = putriCheck != null  && putriCheck.isSelected();
            boolean filterCampur = campurCheck != null && campurCheck.isSelected();
            boolean anyTipeSelected = filterPutra || filterPutri || filterCampur;

            List<Kos> result = allKosList.stream()
                .filter(kos -> {
                    // Keyword
                    if (!keyword.isEmpty()) {
                        boolean matchNama  = kos.getNamaKos() != null && kos.getNamaKos().toLowerCase().contains(keyword);
                        boolean matchAlamat = kos.getAlamat() != null && kos.getAlamat().toLowerCase().contains(keyword);
                        if (!matchNama && !matchAlamat) return false;
                    }
                    // Harga Min
                    if (!minStr.isEmpty() && kos.getHarga() != null) {
                        try {
                            if (kos.getHarga().doubleValue() < Double.parseDouble(minStr)) return false;
                        } catch (NumberFormatException ignored) {}
                    }
                    // Harga Max
                    if (!maxStr.isEmpty() && kos.getHarga() != null) {
                        try {
                            if (kos.getHarga().doubleValue() > Double.parseDouble(maxStr)) return false;
                        } catch (NumberFormatException ignored) {}
                    }
                    // Tipe Kos
                    if (anyTipeSelected && kos.getTipeKos() != null) {
                        boolean match =
                            (filterPutra  && kos.getTipeKos() == Kos.TipeKos.PUTRA) ||
                            (filterPutri  && kos.getTipeKos() == Kos.TipeKos.PUTRI) ||
                            (filterCampur && kos.getTipeKos() == Kos.TipeKos.CAMPUR);
                        if (!match) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

            // Sorting
            String sortVal = sortCombo.getValue();
            if ("Harga Terendah".equals(sortVal)) {
                result.sort((a, b) -> {
                    BigDecimal ha = a.getHarga(), hb = b.getHarga();
                    if (ha == null && hb == null) return 0;
                    if (ha == null) return 1;
                    if (hb == null) return -1;
                    return ha.compareTo(hb);
                });
            } else if ("Harga Tertinggi".equals(sortVal)) {
                result.sort((a, b) -> {
                    BigDecimal ha = a.getHarga(), hb = b.getHarga();
                    if (ha == null && hb == null) return 0;
                    if (ha == null) return 1;
                    if (hb == null) return -1;
                    return hb.compareTo(ha);
                });
            }

            displayKos(result);

        } catch (Exception e) {
            System.err.println("Error applying filter: " + e.getMessage());
        }
    }

    // ===================== DISPLAY =====================

    private void displayKos(List<Kos> kosList) {
        if (kosGrid == null) return;
        kosGrid.getChildren().clear();

        if (kosList == null || kosList.isEmpty()) {
            Label empty = new Label("😕 Tidak ada kos yang sesuai dengan filter Anda.");
            empty.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748B; -fx-padding: 40;");
            kosGrid.getChildren().add(empty);
            if (lblJumlahHasil != null) lblJumlahHasil.setText("0 kos ditemukan");
            return;
        }

        for (Kos kos : kosList) {
            VBox card = createKosCard(kos);
            kosGrid.getChildren().add(card);
        }
        if (lblJumlahHasil != null) {
            lblJumlahHasil.setText("Menampilkan " + kosList.size() + " kos");
        }
    }

    private VBox createKosCard(Kos kos) {
        VBox card = new VBox(0);
        card.setPrefWidth(460.0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.09), 12, 0, 0, 4);");

        // ---- Gambar ----
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(220.0);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(220.0);
        imageView.setFitWidth(460.0);
        imageView.setPreserveRatio(false);

        String imagePath = kos.getFotoKos() != null ? "/" + kos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                imageView.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                URL def = getClass().getResource("/images/tesKos.png");
                if (def != null) imageView.setImage(new Image(def.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Gagal load image: " + e.getMessage());
        }

        AnchorPane.setLeftAnchor(imageView, 0.0);
        AnchorPane.setRightAnchor(imageView, 0.0);
        AnchorPane.setTopAnchor(imageView, 0.0);

        // Rating label
        Double avgRating = reviewDAO.getAverageRating(kos.getIdKos());
        String ratingStr = avgRating != null ? String.format("⭐ %.1f", avgRating) : "⭐ N/A";
        Label lblRating = new Label(ratingStr);
        lblRating.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-text-fill: white; " +
                           "-fx-font-size: 12px; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");
        AnchorPane.setRightAnchor(lblRating, 14.0);
        AnchorPane.setTopAnchor(lblRating, 14.0);

        topPane.getChildren().addAll(imageView, lblRating);

        // ---- Konten ----
        VBox contentBox = new VBox(10.0);
        contentBox.setStyle("-fx-padding: 18 20 20 20;");

        // Badge tipe kos
        HBox badgesBox = new HBox(8.0);
        String tipeStr = "🚹 Putra", tipeBg = "#2D6BE4";
        if (kos.getTipeKos() != null) {
            switch (kos.getTipeKos()) {
                case PUTRI  -> { tipeStr = "🚺 Putri";  tipeBg = "#D6336C"; }
                case CAMPUR -> { tipeStr = "👥 Campur"; tipeBg = "#16A34A"; }
            }
        }
        Label lblTipe = new Label(tipeStr);
        lblTipe.setStyle("-fx-background-color: " + tipeBg +
                         "; -fx-text-fill: white; -fx-font-size: 11px; " +
                         "-fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");
        badgesBox.getChildren().add(lblTipe);

        if (kos.getDurasiSewa() != null) {
            String dur = kos.getDurasiSewa().name().substring(0, 1).toUpperCase()
                       + kos.getDurasiSewa().name().substring(1).toLowerCase();
            Label lblDurasi = new Label(dur);
            lblDurasi.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-size: 11px; " +
                               "-fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 20;");
            badgesBox.getChildren().add(lblDurasi);
        }

        Label lblNama = new Label(kos.getNamaKos() != null ? kos.getNamaKos() : "N/A");
        lblNama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A2744;");

        Label lblAlamat = new Label("📍 " + (kos.getAlamat() != null ? kos.getAlamat() : "Alamat tidak tersedia"));
        lblAlamat.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        lblAlamat.setWrapText(true);

        // Harga
        HBox hargaRow = new HBox();
        hargaRow.setAlignment(Pos.CENTER_LEFT);
        VBox hargaBox = new VBox();
        Label lblFrom = new Label("Mulai dari");
        lblFrom.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        String hargaStr = kos.getHarga() != null
            ? "Rp " + String.format("%,.0f", kos.getHarga()).replace(",", ".") + "/bln"
            : "Hubungi Pemilik";
        Label lblHarga = new Label(hargaStr);
        lblHarga.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2D6BE4;");
        hargaBox.getChildren().addAll(lblFrom, lblHarga);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hargaRow.getChildren().addAll(hargaBox, spacer);

        // Tombol
        Button btnDetail = new Button("Lihat Detail");
        btnDetail.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 13px; " +
                           "-fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 9 18 9 18;");
        btnDetail.setOnAction(e -> navigateToDetail(kos));

        contentBox.getChildren().addAll(badgesBox, lblNama, lblAlamat, hargaRow, btnDetail);
        card.getChildren().addAll(topPane, contentBox);
        return card;
    }

    private void navigateToDetail(Kos kos) {
        DetailKosPenyewaController.selectedKos = kos;
        com.kosku.Main.navigateTo("view/penyewa/DetailKosPenyewa.fxml", "KosKu - Detail Kos");
    }
}
