package com.kosku.controller.admin;

import com.kosku.dao.BookingDAO;
import com.kosku.dao.KosDAO;
import com.kosku.dao.PembayaranDAO;
import com.kosku.dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MainMenuAdminController {

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalKos;
    @FXML private Label lblTotalBooking;
    @FXML private Label lblTotalPembayaran;

    private UserDAO userDAO = new UserDAO();
    private KosDAO kosDAO = new KosDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        // Run in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                long totalUsers = userDAO.getTotalUsers();
                long totalKos = kosDAO.getTotalVerifiedKos();
                long totalBooking = bookingDAO.getTotalActiveBookings();
                BigDecimal totalPembayaran = pembayaranDAO.getTotalPembayaranBulanan();

                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String formattedPembayaran = formatter.format(totalPembayaran);

                Platform.runLater(() -> {
                    lblTotalUsers.setText(String.valueOf(totalUsers));
                    lblTotalKos.setText(String.valueOf(totalKos));
                    lblTotalBooking.setText(String.valueOf(totalBooking));
                    lblTotalPembayaran.setText(formattedPembayaran);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
