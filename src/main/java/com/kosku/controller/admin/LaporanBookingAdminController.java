package com.kosku.controller.admin;

import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class LaporanBookingAdminController {

    @FXML private Label lblTotalBooking;
    @FXML private Label lblBookingAktif;
    @FXML private Label lblBookingPending;

    @FXML private TableView<Booking> tabelBooking;
    @FXML private TableColumn<Booking, Integer> colId;
    @FXML private TableColumn<Booking, String> colPenyewa;
    @FXML private TableColumn<Booking, String> colKos;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, String> colTotal;
    @FXML private TableColumn<Booking, String> colTanggal;

    private final BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        setupTable();
        loadStatistics();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBooking() != null ? data.getValue().getIdBooking() : 0).asObject());
        colPenyewa.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getPenyewa().getUsername());
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colKos.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getKamar().getKos().getNamaKos() + " (Kamar " + data.getValue().getKamar().getNomorKamar() + ")");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colStatus.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getStatusBooking() != null ? data.getValue().getStatusBooking().name() : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colTotal.setCellValueFactory(data -> {
            try {
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                return new SimpleStringProperty(data.getValue().getTotalHarga() != null ? formatRupiah.format(data.getValue().getTotalHarga()) : "Rp0");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colTanggal.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(data.getValue().getTanggalMulai() != null ? data.getValue().getTanggalMulai().toString() : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Unknown");
            }
        });
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                List<Booking> all = bookingDAO.getAllBookingWithDetails();
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

                    if (tabelBooking != null) {
                        ObservableList<Booking> observableList = FXCollections.observableArrayList(all);
                        tabelBooking.setItems(observableList);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
