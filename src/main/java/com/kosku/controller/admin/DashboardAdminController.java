package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.dao.BookingDAO;
import com.kosku.dao.KosDAO;
import com.kosku.dao.PembayaranDAO;
import com.kosku.dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardAdminController {

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalKos;
    @FXML private Label lblTotalBooking;
    @FXML private Label lblTotalPembayaran;
    @FXML private Button btnLihatPengguna;
    @FXML private Button btnLihatKos;

    private final UserDAO userDAO = new UserDAO();
    private final KosDAO kosDAO = new KosDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                long totalUsers    = userDAO.getTotalUsers();
                long totalKos      = kosDAO.getTotalVerifiedKos();
                long totalBooking  = bookingDAO.getTotalActiveBookings();
                BigDecimal totalPembayaran = pembayaranDAO.getTotalPembayaranBulanan();

                NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String fmtPembayaran = fmt.format(totalPembayaran);

                Platform.runLater(() -> {
                    if (lblTotalUsers != null)      lblTotalUsers.setText(String.valueOf(totalUsers));
                    if (lblTotalKos != null)         lblTotalKos.setText(String.valueOf(totalKos));
                    if (lblTotalBooking != null)     lblTotalBooking.setText(String.valueOf(totalBooking));
                    if (lblTotalPembayaran != null)  lblTotalPembayaran.setText(fmtPembayaran);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // onAction handlers untuk tombol di FXML
    @FXML
    private void goToManajemenPengguna() {
        Main.navigateTo("view/Admin/ManagementPengguna.fxml", "KosKu - Manajemen Pengguna");
    }

    @FXML
    private void goToManajemenKos() {
        Main.navigateTo("view/Admin/ManagementKos.fxml", "KosKu - Manajemen Kos");
    }

    @FXML
    private void goToLaporanPembayaran() {
        Main.navigateTo("view/Admin/LaporanPembayaran.fxml", "KosKu - Laporan Pembayaran");
    }

    @FXML
    private void goToLaporanBooking() {
        Main.navigateTo("view/Admin/LaporanBooking.fxml", "KosKu - Laporan Booking");
    }
}
