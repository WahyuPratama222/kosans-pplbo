package com.kosku.controller.admin;

import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class LaporanBookingAdminController {

    @FXML private Label lblTotalBooking;
    @FXML private Label lblBookingAktif;
    @FXML private Label lblBookingPending;

    private final BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                List<Booking> all = bookingDAO.getAll(Booking.class);
                long aktif = all.stream()
                        .filter(b -> b.getStatusBooking() == Booking.StatusBooking.DITERIMA)
                        .count();
                long pending = all.stream()
                        .filter(b -> b.getStatusBooking() == Booking.StatusBooking.PENDING)
                        .count();

                Platform.runLater(() -> {
                    if (lblTotalBooking != null)  lblTotalBooking.setText(String.valueOf(all.size()));
                    if (lblBookingAktif != null)   lblBookingAktif.setText(String.valueOf(aktif));
                    if (lblBookingPending != null) lblBookingPending.setText(String.valueOf(pending));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
