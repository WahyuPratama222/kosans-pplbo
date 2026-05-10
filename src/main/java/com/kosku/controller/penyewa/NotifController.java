package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.kosku.dao.NotifikasiDAO;
import com.kosku.model.Notifikasi;
import com.kosku.model.Notifikasi.TipeNotifikasi;
import com.kosku.util.PopupManager;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Notifikasi Penyewa
 * Menampilkan berbagai notifikasi terkait booking, pembayaran, dan pesan dari pemilik
 */
public class NotifController implements Initializable {

    @FXML
    private VBox vboxNotif;

    @FXML
    private Label lblJumlahBelumDibaca;

    @FXML
    private NavbarController navbarController;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private NotifikasiDAO notifikasiDAO;

    // Menyimpan daftar notifikasi yang sedang ditampilkan
    private List<Notifikasi> currentNotifList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Notifikasi Penyewa berhasil dimuat!");

        notifikasiDAO = new NotifikasiDAO();

        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("notifikasi");
        }

        // Load semua notifikasi
        loadNotifikasi(null);
    }

    // ==========================================
    // LOAD & RENDER
    // ==========================================

    private void loadNotifikasi(TipeNotifikasi filter) {
        try {
            Integer userId = SessionManager.getCurrentUserId();

            if (userId == null) {
                PopupManager.showWarning("Peringatan", "Anda harus login terlebih dahulu");
                return;
            }

            if (filter == null) {
                currentNotifList = notifikasiDAO.getNotificationsByUser(userId);
            } else {
                currentNotifList = notifikasiDAO.getNotificationsByType(userId, filter);
            }

            renderNotifikasi(currentNotifList);
            updateBadge(userId);

        } catch (Exception e) {
            System.err.println("Error loading notifikasi: " + e.getMessage());
            e.printStackTrace();
            PopupManager.showError("Error", "Gagal memuat notifikasi: " + e.getMessage());
        }
    }

    private void renderNotifikasi(List<Notifikasi> notifList) {
        vboxNotif.getChildren().clear();

        if (notifList == null || notifList.isEmpty()) {
            Label empty = new Label("Tidak ada notifikasi.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 40;");
            vboxNotif.getChildren().add(empty);
            return;
        }

        for (Notifikasi notif : notifList) {
            HBox card = createNotifCard(notif);
            vboxNotif.getChildren().add(card);
        }
    }

    private HBox createNotifCard(Notifikasi notif) {
        boolean sudahDibaca = Boolean.TRUE.equals(notif.getSudahDibaca());

        // Pilih ikon & warna berdasarkan tipe
        String icon;
        String bgColor;
        String borderColor;
        String textColor;

        TipeNotifikasi tipe = notif.getTipe();
        if (tipe == TipeNotifikasi.PEMBAYARAN) {
            icon = "💰"; bgColor = sudahDibaca ? "white" : "#FFF8EC";
            borderColor = sudahDibaca ? "#E8EDF5" : "#FFD98E"; textColor = sudahDibaca ? "#666" : "#92400E";
        } else if (tipe == TipeNotifikasi.BOOKING) {
            icon = "🏠"; bgColor = sudahDibaca ? "white" : "#EEF3FF";
            borderColor = sudahDibaca ? "#E8EDF5" : "#C5D8FF"; textColor = sudahDibaca ? "#666" : "#1A3A6B";
        } else if (tipe == TipeNotifikasi.PESAN) {
            icon = "💬"; bgColor = sudahDibaca ? "white" : "#F0FDF4";
            borderColor = sudahDibaca ? "#E8EDF5" : "#86EFAC"; textColor = sudahDibaca ? "#666" : "#14532D";
        } else if (tipe == TipeNotifikasi.REMINDER) {
            icon = "📋"; bgColor = sudahDibaca ? "white" : "#FFF8EC";
            borderColor = sudahDibaca ? "#E8EDF5" : "#FFD98E"; textColor = sudahDibaca ? "#666" : "#92400E";
        } else {
            icon = "📢"; bgColor = sudahDibaca ? "white" : "#EEF3FF";
            borderColor = sudahDibaca ? "#E8EDF5" : "#C5D8FF"; textColor = sudahDibaca ? "#666" : "#1A3A6B";
        }

        HBox card = new HBox(16);
        card.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 14; " +
            "-fx-padding: 20 24 20 24; " +
            "-fx-border-color: " + borderColor + "; " +
            "-fx-border-radius: 14; " +
            "-fx-border-width: 1;"
        );
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        // Icon
        Label lblIcon = new Label(icon);
        lblIcon.setStyle("-fx-font-size: 28px;" + (sudahDibaca ? " -fx-opacity: 0.65;" : ""));

        // Content VBox
        VBox content = new VBox(6);
        HBox.setHgrow(content, Priority.ALWAYS);

        // Header row
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblJudul = new Label(notif.getJudul() != null ? notif.getJudul() : "Notifikasi");
        lblJudul.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String waktuStr = notif.getWaktuNotifikasi() != null
            ? notif.getWaktuNotifikasi().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) : "";
        Label lblWaktu = new Label(waktuStr);
        lblWaktu.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        headerRow.getChildren().addAll(lblJudul);
        if (!sudahDibaca) {
            Label lblBaru = new Label("● BARU");
            lblBaru.setStyle("-fx-font-size: 11px; -fx-text-fill: #2D6BE4; -fx-font-weight: bold;");
            headerRow.getChildren().add(lblBaru);
        }
        headerRow.getChildren().addAll(spacer, lblWaktu);

        // Isi notifikasi
        Label lblIsi = new Label(notif.getIsi() != null ? notif.getIsi() : "");
        lblIsi.setWrapText(true);
        lblIsi.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (sudahDibaca ? "#999" : "#444") + "; -fx-max-width: 1200;");

        // Action buttons
        HBox actions = new HBox(8);
        actions.setPadding(new Insets(6, 0, 0, 0));

        if (!sudahDibaca) {
            Button btnBaca = new Button("✓ Tandai Dibaca");
            btnBaca.setStyle("-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 16 7 16;");
            btnBaca.setOnAction(e -> markAsRead(notif));
            actions.getChildren().add(btnBaca);
        }

        Button btnHapus = new Button("Hapus");
        btnHapus.setStyle("-fx-background-color: #FFF0F0; -fx-text-fill: #CC3333; -fx-font-size: 12px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 16 7 16; -fx-border-color: #FFCCCC; -fx-border-radius: 8;");
        btnHapus.setOnAction(e -> hapusSatuNotif(notif));
        actions.getChildren().add(btnHapus);

        content.getChildren().addAll(headerRow, lblIsi, actions);
        card.getChildren().addAll(lblIcon, content);
        return card;
    }

    private void updateBadge(Integer userId) {
        try {
            long unreadCount = notifikasiDAO.countUnreadNotifications(userId);
            if (lblJumlahBelumDibaca != null) {
                lblJumlahBelumDibaca.setText(unreadCount + " belum dibaca");
            }
        } catch (Exception e) {
            System.err.println("Error update badge: " + e.getMessage());
        }
    }

    // ==========================================
    // AKSI DATABASE
    // ==========================================

    private void markAsRead(Notifikasi notif) {
        try {
            notifikasiDAO.markAsRead(notif.getIdNotifikasi());
            Integer userId = SessionManager.getCurrentUserId();
            loadNotifikasi(null);
        } catch (Exception e) {
            PopupManager.showError("Error", "Gagal menandai notifikasi: " + e.getMessage());
        }
    }

    private void hapusSatuNotif(Notifikasi notif) {
        try {
            notifikasiDAO.deleteNotification(notif.getIdNotifikasi());
            loadNotifikasi(null);
        } catch (Exception e) {
            PopupManager.showError("Error", "Gagal menghapus notifikasi: " + e.getMessage());
        }
    }

    // ==========================================
    // FXML EVENT HANDLERS (dari sidebar filter)
    // ==========================================

    @FXML
    void filterSemua(ActionEvent event) {
        loadNotifikasi(null);
    }

    @FXML
    void filterKamar(ActionEvent event) {
        loadNotifikasi(TipeNotifikasi.BOOKING);
    }

    @FXML
    void filterBayar(ActionEvent event) {
        loadNotifikasi(TipeNotifikasi.PEMBAYARAN);
    }

    @FXML
    void filterKontrak(ActionEvent event) {
        loadNotifikasi(TipeNotifikasi.REMINDER);
    }

    @FXML
    void filterPengumuman(ActionEvent event) {
        loadNotifikasi(TipeNotifikasi.INFO);
    }

    @FXML
    void tandaiSemua(ActionEvent event) {
        try {
            Integer userId = SessionManager.getCurrentUserId();
            if (userId == null) {
                PopupManager.showWarning("Peringatan", "Anda harus login terlebih dahulu");
                return;
            }
            notifikasiDAO.markAllAsRead(userId);
            loadNotifikasi(null);
            PopupManager.showInfo("Sukses", "Semua notifikasi ditandai sudah dibaca");
        } catch (Exception e) {
            PopupManager.showError("Error", "Gagal menandai semua: " + e.getMessage());
        }
    }

    @FXML
    void hapusSemua(ActionEvent event) {
        boolean confirmed = PopupManager.showConfirmation("Konfirmasi", "Hapus semua notifikasi?");
        if (!confirmed) {
            return;
        }

        try {
            Integer userId = SessionManager.getCurrentUserId();
            if (userId == null) {
                PopupManager.showWarning("Peringatan", "Anda harus login terlebih dahulu");
                return;
            }
            notifikasiDAO.deleteAllNotifications(userId);
            loadNotifikasi(null);
            PopupManager.showInfo("Sukses", "Semua notifikasi berhasil dihapus");
        } catch (Exception e) {
            PopupManager.showError("Error", "Gagal menghapus semua notifikasi: " + e.getMessage());
        }
    }

}
