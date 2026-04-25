package com.kosku.controller.penyewa;

import com.kosku.dao.KosDAO;
import com.kosku.model.Kos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFilterController {

    @FXML private TextField searchField;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private CheckBox regulerCheck;
    @FXML private CheckBox exclusiveCheck;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox acCheck;
    @FXML private CheckBox kmDalamCheck;
    @FXML private CheckBox parkirCheck;
    @FXML private ComboBox<String> sortCombo;
    @FXML private GridPane kosGrid;

    private KosDAO kosDAO = new KosDAO();

    @FXML
    public void initialize() {
        // Inisialisasi ComboBox Sort
        sortCombo.getItems().addAll("Terbaru", "Harga Terendah", "Harga Tertinggi");
        sortCombo.getSelectionModel().selectFirst();

        // Load data awal (semua kos terverifikasi)
        loadKosData(kosDAO.getKosVerified());
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        List<Kos> results = kosDAO.searchKos(keyword);
        applyFilters(results);
    }

    @FXML
    private void handleApplyFilter() {
        // Ambil data dasar dan terapkan filter tambahan di sisi Java (untuk fleksibilitas)
        List<Kos> baseList = kosDAO.getKosVerified();
        applyFilters(baseList);
    }

    private void applyFilters(List<Kos> list) {
        // Logika filter (Dummy implementation for UI feedback)
        // Di aplikasi nyata, filter ini akan digabung ke Query HQL/Criteria API
        
        List<Kos> filteredList = list.stream()
            .filter(k -> {
                // Contoh filter nama jika searchField diisi
                if (!searchField.getText().isEmpty() && !k.getNamaKos().toLowerCase().contains(searchField.getText().toLowerCase())) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());

        loadKosData(filteredList);
    }

    private void loadKosData(List<Kos> list) {
        kosGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;

        for (Kos kos : list) {
            VBox card = createKosCard(kos);
            kosGrid.add(card, column++, row);
            
            if (column == 3) { // 3 Kolom per baris
                column = 0;
                row++;
            }
        }
    }

    private VBox createKosCard(Kos kos) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        card.setPrefWidth(200);

        Label nameLabel = new Label(kos.getNamaKos());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label addressLabel = new Label(kos.getAlamat());
        addressLabel.setWrapText(true);
        addressLabel.setStyle("-fx-text-fill: #636e72; -fx-font-size: 11px;");

        Button detailBtn = new Button("Lihat Detail");
        detailBtn.setMaxWidth(Double.MAX_VALUE);
        detailBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white;");

        card.getChildren().addAll(nameLabel, addressLabel, detailBtn);
        return card;
    }
}
