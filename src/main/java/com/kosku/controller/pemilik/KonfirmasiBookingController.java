package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import com.kosku.util.SessionManager;

public class KonfirmasiBookingController implements Initializable {

    @FXML private NavbarPemilikController navbarController;
    @FXML private VBox vboxContainer;

    private BookingDAO bookingDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (navbarController != null) {
            navbarController.setActivePage("booking");
        }
        bookingDAO = new BookingDAO();
        loadPendingBookings();
    }

    @FXML
    private void handleKembali() {
        com.kosku.Main.navigateTo("view/Pemilik/BookingPenyewa.fxml");
    }

    private void loadPendingBookings() {
        vboxContainer.getChildren().clear();
        Integer pemilikId = SessionManager.getCurrentUserId();
        if (pemilikId == null) return;

        List<Booking> allBookings = bookingDAO.getBookingByPemilik(pemilikId);
        List<Booking> pendingBookings = allBookings.stream()
                .filter(b -> b.getStatusBooking() == Booking.StatusBooking.PENDING)
                .collect(Collectors.toList());

        if (pendingBookings.isEmpty()) {
            Label emptyLabel = new Label("🎉 Tidak ada booking baru yang menunggu konfirmasi saat ini.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748B;");
            vboxContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Booking b : pendingBookings) {
            vboxContainer.getChildren().add(createBookingCard(b));
        }
    }

    private HBox createBookingCard(Booking b) {
        HBox card = new HBox(20);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));

        // Info Penyewa
        VBox penyewaInfo = new VBox(5);
        Label titlePenyewa = new Label("Data Penyewa:");
        titlePenyewa.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label nama = new Label("👤 " + (b.getPenyewa() != null ? b.getPenyewa().getUsername() : "Unknown"));
        Label hp = new Label("📱 " + (b.getPenyewa() != null ? b.getPenyewa().getNomorHp() : "Unknown"));
        penyewaInfo.getChildren().addAll(titlePenyewa, nama, hp);
        penyewaInfo.setPrefWidth(250);

        // Info Properti
        VBox propertiInfo = new VBox(5);
        Label titleProperti = new Label("Detail Properti:");
        titleProperti.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        String kosName = (b.getKamar() != null && b.getKamar().getKos() != null) ? b.getKamar().getKos().getNamaKos() : "Unknown";
        String noKamar = (b.getKamar() != null) ? b.getKamar().getNomorKamar() : "Unknown";
        Label properti = new Label("🏠 " + kosName);
        Label kamar = new Label("🚪 Kamar " + noKamar);
        Label tanggal = new Label("📅 Masuk: " + (b.getTanggalMulai() != null ? b.getTanggalMulai() : "-"));
        propertiInfo.getChildren().addAll(titleProperti, properti, kamar, tanggal);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Tombol Aksi
        VBox actionInfo = new VBox(10);
        actionInfo.setAlignment(Pos.CENTER);
        Button btnTerima = new Button("✅ Terima Booking");
        btnTerima.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnTerima.setOnAction(e -> handleTerima(b));

        Button btnTolak = new Button("❌ Tolak Booking");
        btnTolak.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnTolak.setOnAction(e -> handleTolak(b));

        actionInfo.getChildren().addAll(btnTerima, btnTolak);

        card.getChildren().addAll(penyewaInfo, propertiInfo, spacer, actionInfo);
        return card;
    }

    private void handleTerima(Booking b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Anda yakin ingin menerima booking ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                b.setStatusBooking(Booking.StatusBooking.DITERIMA);
                bookingDAO.saveOrUpdate(b);
                loadPendingBookings(); // Refresh UI
            }
        });
    }

    private void handleTolak(Booking b) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Tolak Booking");
        dialog.setHeaderText(null);
        
        // Custom Dialog Pane styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-padding: 20;");
        
        VBox content = new VBox(15);
        
        Label titleLabel = new Label("Tolak Booking dari " + (b.getPenyewa() != null ? b.getPenyewa().getUsername() : "Penyewa"));
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        
        Label infoLabel = new Label("Silakan masukkan alasan penolakan di bawah ini. Alasan ini akan dikirimkan kepada penyewa.");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
        infoLabel.setWrapText(true);
        infoLabel.setPrefWidth(400);
        
        TextArea textArea = new TextArea();
        textArea.setPromptText("Contoh: Maaf, kamar sedang dalam perbaikan...");
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #CBD5E1;");
        
        content.getChildren().addAll(titleLabel, infoLabel, textArea);
        dialogPane.setContent(content);

        ButtonType tolakButtonType = new ButtonType("Tolak Booking", ButtonBar.ButtonData.OK_DONE);
        ButtonType batalButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(tolakButtonType, batalButtonType);

        // Styling the buttons
        Button tolakButton = (Button) dialogPane.lookupButton(tolakButtonType);
        tolakButton.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button batalButton = (Button) dialogPane.lookupButton(batalButtonType);
        batalButton.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == tolakButtonType) {
                return textArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(alasan -> {
            if (alasan.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Alasan penolakan tidak boleh kosong!");
                alert.showAndWait();
            } else {
                b.setStatusBooking(Booking.StatusBooking.DITOLAK);
                b.setAlasanTolak(alasan.trim());
                bookingDAO.saveOrUpdate(b);
                loadPendingBookings(); // Refresh UI
            }
        });
    }
}
