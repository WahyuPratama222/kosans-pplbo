package com.kosku.controller.penyewa;

import com.kosku.dao.KosDAO;
import com.kosku.dao.ReviewDAO;
import com.kosku.model.Kos;
import com.kosku.util.FileUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.File;
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
    private ReviewDAO reviewDAO = new ReviewDAO();

    @FXML
    public void initialize() {
        // ... (rest of initialize)
    }

    // ... (other methods)

    private VBox createKosCard(Kos kos) {
        VBox card = new VBox(0); // Zero spacing for image to touch top
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        // 1. IMAGE SECTION (Feature #3)
        VBox imageHolder = new VBox();
        imageHolder.setPrefHeight(150);
        imageHolder.getStyleClass().add("image-placeholder");

        if (kos.getGambarKos() != null) {
            try {
                String fullPath = FileUtil.getAbsolutePath(kos.getGambarKos());
                File file = new File(fullPath);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString(), 280, 150, true, true);
                    ImageView imageView = new ImageView(img);
                    imageView.getStyleClass().add("kos-card-image");
                    imageHolder.getChildren().add(imageView);
                } else {
                    imageHolder.getChildren().add(new Label("🖼️ No Image"));
                }
            } catch (Exception e) {
                imageHolder.getChildren().add(new Label("⚠️ Error Load"));
            }
        } else {
            imageHolder.getChildren().add(new Label("📷 Belum ada foto"));
        }

        // 2. CONTENT SECTION
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 12;");

        Label nameLabel = new Label(kos.getNamaKos());
        nameLabel.getStyleClass().add("label-subtitle");
        
        Label addressLabel = new Label("📍 " + kos.getAlamat());
        addressLabel.setWrapText(true);
        addressLabel.getStyleClass().add("label-muted");

        // 3. RATING SECTION (Feature #4)
        Double avgRating = reviewDAO.getAverageRating(kos.getIdKos());
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label starLabel = new Label("⭐");
        starLabel.getStyleClass().add("rating-star");
        
        Label rateValue = new Label(avgRating != null ? String.format("%.1f", avgRating) : "0.0");
        rateValue.getStyleClass().add("rating-value");
        
        Label countReview = new Label("(" + (reviewDAO.getReviewsByKos(kos.getIdKos()).size()) + " ulasan)");
        countReview.getStyleClass().add("label-muted");
        
        ratingBox.getChildren().addAll(starLabel, rateValue, countReview);

        Button detailBtn = new Button("Lihat Detail");
        detailBtn.setMaxWidth(Double.MAX_VALUE);
        detailBtn.getStyleClass().add("btn-primary");
        VBox.setMargin(detailBtn, new javafx.geometry.Insets(10, 0, 0, 0));

        content.getChildren().addAll(nameLabel, addressLabel, ratingBox, detailBtn);
        card.getChildren().addAll(imageHolder, content);
        
        return card;
    }
}
