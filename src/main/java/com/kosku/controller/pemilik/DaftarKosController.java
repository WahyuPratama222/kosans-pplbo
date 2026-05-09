package com.kosku.controller.pemilik;

import com.kosku.dao.KosDAO;
import com.kosku.dao.KamarDAO;
import com.kosku.model.Kos;
import com.kosku.model.Kamar;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller untuk daftarKosPemilik.fxml
 * Menampilkan daftar kos milik pemilik yang login, dengan search & sort.
 */
public class DaftarKosController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label totalKosLabel;
    @FXML private Label totalKamarLabel;
    @FXML private Label kamarTerisiLabel;
    @FXML private VBox kosContainer;
    @FXML private NavbarPemilikController navbarController;

    private KosDAO kosDAO = new KosDAO();
    private KamarDAO kamarDAO = new KamarDAO();
    private List<Kos> allKosList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (navbarController != null) {
            navbarController.setActivePage("kelolakos");
        }
        if (sortComboBox != null) {
            sortComboBox.getItems().addAll("Nama (A-Z)", "Nama (Z-A)", "Terbaru", "Kamar Terbanyak");
            sortComboBox.getSelectionModel().select("Terbaru");
            sortComboBox.setOnAction(e -> displayKos(allKosList));
        }
        loadKosData();
    }

    private void loadKosData() {
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Sesi login tidak ditemukan.");
                return;
            }

            allKosList = kosDAO.getByPemilik(currentUser);

            // Update summary stats
            int totalKos = allKosList != null ? allKosList.size() : 0;
            long totalKamar = 0, kamarTersedia = 0;
            if (allKosList != null) {
                for (Kos kos : allKosList) {
                    totalKamar += kamarDAO.countTotalKamar(kos);
                    kamarTersedia += kamarDAO.countKamarTersedia(kos);
                }
            }
            long kamarTerisi = totalKamar - kamarTersedia;

            if (totalKosLabel   != null) totalKosLabel.setText(String.valueOf(totalKos));
            if (totalKamarLabel != null) totalKamarLabel.setText(String.valueOf(totalKamar));
            if (kamarTerisiLabel != null) kamarTerisiLabel.setText(String.valueOf(kamarTerisi));

            displayKos(allKosList);

        } catch (Exception e) {
            System.err.println("Error loading kos: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data kos: " + e.getMessage());
        }
    }

    private void displayKos(List<Kos> list) {
        if (kosContainer == null) return;
        kosContainer.getChildren().clear();

        if (list == null || list.isEmpty()) {
            Label empty = new Label("🏠 Belum ada kos yang didaftarkan.\nKlik 'Tambah Kos Baru' untuk mulai.");
            empty.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748B; -fx-text-alignment: center; -fx-alignment: center; -fx-padding: 40;");
            empty.setWrapText(true);
            kosContainer.getChildren().add(empty);
            return;
        }

        // Sorting
        String sort = sortComboBox != null ? sortComboBox.getValue() : "Terbaru";
        List<Kos> sorted = list.stream().collect(Collectors.toList());
        if ("Nama (A-Z)".equals(sort)) {
            sorted.sort((a, b) -> a.getNamaKos().compareToIgnoreCase(b.getNamaKos()));
        } else if ("Nama (Z-A)".equals(sort)) {
            sorted.sort((a, b) -> b.getNamaKos().compareToIgnoreCase(a.getNamaKos()));
        }

        for (Kos kos : sorted) {
            HBox card = createKosCard(kos);
            kosContainer.getChildren().add(card);
        }
    }

    private HBox createKosCard(Kos kos) {
        HBox card = new HBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(25,35,56,0.08), 12, 0, 0, 4); " +
                      "-fx-border-color: #E8EDF5; -fx-border-radius: 16; -fx-border-width: 1;");

        // Gambar
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(false);
        String imgPath = kos.getFotoKos() != null ? "/" + kos.getFotoKos() + ".png" : "/images/tesKos.png";
        try {
            URL imgUrl = getClass().getResource(imgPath);
            if (imgUrl == null) imgUrl = getClass().getResource("/images/tesKos.png");
            if (imgUrl != null) imageView.setImage(new Image(imgUrl.toExternalForm()));
        } catch (Exception ignored) {}

        // Detail
        VBox details = new VBox(8);
        details.setStyle("-fx-padding: 16 20 16 20;");
        HBox.setHgrow(details, Priority.ALWAYS);

        Label lblNama = new Label(kos.getNamaKos() != null ? kos.getNamaKos() : "N/A");
        lblNama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A3A6B;");

        Label lblAlamat = new Label("📍 " + (kos.getAlamat() != null ? kos.getAlamat() : "Alamat tidak tersedia"));
        lblAlamat.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        lblAlamat.setWrapText(true);

        BigDecimal harga = kos.getHarga();
        String hargaStr = harga != null
            ? "Rp " + String.format("%,.0f", harga).replace(",", ".") + " / bulan"
            : "Hubungi Pemilik";
        Label lblHarga = new Label(hargaStr);
        lblHarga.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D6BE4;");

        String verStr = Boolean.TRUE.equals(kos.getIsVerified()) ? "✅ Terverifikasi" : "⏳ Menunggu Verifikasi";
        String verColor = Boolean.TRUE.equals(kos.getIsVerified()) ? "#16A34A" : "#F59E0B";
        Label lblVerif = new Label(verStr);
        lblVerif.setStyle("-fx-font-size: 12px; -fx-text-fill: " + verColor + "; -fx-font-weight: bold;");

        // Tombol aksi
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        Button btnEdit = new Button("✏️ Edit");
        btnEdit.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2D6BE4; -fx-font-size: 12px; " +
                         "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 16 7 16; " +
                         "-fx-border-color: #BFDBFE; -fx-border-radius: 8;");
        btnEdit.setOnAction(e -> handleEditKos(kos));

        Button btnKamar = new Button("🛏 Kelola Kamar");
        btnKamar.setStyle("-fx-background-color: #1E4DB7; -fx-text-fill: white; -fx-font-size: 12px; " +
                          "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 16 7 16;");
        btnKamar.setOnAction(e -> com.kosku.Main.navigateTo("view/Pemilik/ManagementKamar.fxml", "KosKu - Kelola Kamar"));

        btnBox.getChildren().addAll(btnEdit, btnKamar);
        details.getChildren().addAll(lblNama, lblAlamat, lblHarga, lblVerif, btnBox);
        card.getChildren().addAll(imageView, details);
        return card;
    }

    private void handleEditKos(Kos kos) {
        // TODO: implementasi edit kos (bisa via popup dialog atau halaman baru)
        showAlert(Alert.AlertType.INFORMATION, "Info", "Fitur edit kos: " + kos.getNamaKos() + " akan segera tersedia.");
    }

    @FXML
    private void handleTambahKos() {
        com.kosku.Main.navigateTo("view/Pemilik/daftarKosPemilik.fxml", "KosKu - Tambah Kos");
    }

    @FXML
    private void handleSearch() {
        if (allKosList == null) return;
        String keyword = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        if (keyword.isEmpty()) {
            displayKos(allKosList);
            return;
        }
        List<Kos> filtered = allKosList.stream()
            .filter(k -> (k.getNamaKos() != null && k.getNamaKos().toLowerCase().contains(keyword))
                      || (k.getAlamat() != null && k.getAlamat().toLowerCase().contains(keyword)))
            .collect(Collectors.toList());
        displayKos(filtered);
    }

    @FXML
    private void handleReset() {
        if (searchField != null) searchField.clear();
        if (sortComboBox != null) sortComboBox.getSelectionModel().select("Terbaru");
        displayKos(allKosList);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
