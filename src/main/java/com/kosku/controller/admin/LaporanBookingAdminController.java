package com.kosku.controller.admin;

import com.kosku.Main;
import com.kosku.dao.BookingDAO;
import com.kosku.model.Booking;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller untuk LaporanBooking.fxml (Admin)
 * Menampilkan seluruh data booking dari semua kos di sistem.
 */
public class LaporanBookingAdminController implements Initializable {

    @FXML private Button btnKembali;
    @FXML private TextField tfCari;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblTotalBooking;
    @FXML private Label lblBookingAktif;
    @FXML private Label lblBookingPending;

    @FXML private TableView<Booking> tabelBooking;
    @FXML private TableColumn<Booking, String> colIdBooking;
    @FXML private TableColumn<Booking, String> colPenyewa;
    @FXML private TableColumn<Booking, String> colKos;
    @FXML private TableColumn<Booking, String> colKamar;
    @FXML private TableColumn<Booking, String> colTanggalMulai;
    @FXML private TableColumn<Booking, String> colTanggalSelesai;
    @FXML private TableColumn<Booking, String> colStatus;
    @FXML private TableColumn<Booking, String> colTotal;

    private BookingDAO bookingDAO = new BookingDAO();
    private List<Booking> allBookings;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnKembali != null) {
            btnKembali.setOnAction(e -> Main.navigateTo("view/Admin/DashboardAdmin.fxml"));
        }

        // Setup ComboBox filter status
        if (cbStatus != null) {
            cbStatus.setItems(FXCollections.observableArrayList(
                "Semua", "PENDING", "DITERIMA", "DITOLAK", "SELESAI", "DIBATALKAN"
            ));
            cbStatus.getSelectionModel().selectFirst();
        }

        setupTableColumns();
        loadAllBookings();
    }

    private void setupTableColumns() {
        if (tabelBooking == null) return;

        if (colIdBooking != null)
            colIdBooking.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdBooking())));

        if (colPenyewa != null)
            colPenyewa.setCellValueFactory(data -> {
                Booking b = data.getValue();
                String nama = b.getPenyewa() != null ? b.getPenyewa().getUsername() : "N/A";
                return new SimpleStringProperty(nama);
            });

        if (colKos != null)
            colKos.setCellValueFactory(data -> {
                Booking b = data.getValue();
                try {
                    String kos = b.getKamar() != null && b.getKamar().getKos() != null
                        ? b.getKamar().getKos().getNamaKos() : "N/A";
                    return new SimpleStringProperty(kos);
                } catch (Exception e) {
                    return new SimpleStringProperty("N/A");
                }
            });

        if (colKamar != null)
            colKamar.setCellValueFactory(data -> {
                Booking b = data.getValue();
                String kamar = b.getKamar() != null ? b.getKamar().getNomorKamar() : "N/A";
                return new SimpleStringProperty(kamar);
            });

        if (colTanggalMulai != null)
            colTanggalMulai.setCellValueFactory(data -> {
                Booking b = data.getValue();
                String tgl = b.getTanggalMulai() != null ? b.getTanggalMulai().format(dtf) : "-";
                return new SimpleStringProperty(tgl);
            });

        if (colTanggalSelesai != null)
            colTanggalSelesai.setCellValueFactory(data -> {
                Booking b = data.getValue();
                String tgl = b.getTanggalSelesai() != null ? b.getTanggalSelesai().format(dtf) : "-";
                return new SimpleStringProperty(tgl);
            });

        if (colStatus != null)
            colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusBooking().name()));

        if (colTotal != null)
            colTotal.setCellValueFactory(data -> {
                BigDecimal total = data.getValue().getTotalHarga();
                String str = total != null
                    ? "Rp " + String.format("%,.0f", total).replace(",", ".") : "-";
                return new SimpleStringProperty(str);
            });

        // Resize policy
        tabelBooking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadAllBookings() {
        try {
            // Ambil semua booking dari seluruh penyewa di sistem
            // (menggunakan query tanpa filter penyewa/kos tertentu)
            allBookings = fetchAllBookings();

            updateStats();
            displayBookings(allBookings);

        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetch semua booking di sistem via HQL langsung.
     */
    private List<Booking> fetchAllBookings() {
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                         "JOIN FETCH b.kamar k " +
                         "JOIN FETCH k.kos kos " +
                         "JOIN FETCH b.penyewa p " +
                         "ORDER BY b.tanggalBooking DESC";
            return session.createQuery(hql, Booking.class).list();
        } catch (Exception e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
            return List.of();
        }
    }

    private void updateStats() {
        if (allBookings == null) return;

        long total   = allBookings.size();
        long aktif   = allBookings.stream().filter(b -> b.getStatusBooking() == Booking.StatusBooking.DITERIMA).count();
        long pending = allBookings.stream().filter(b -> b.getStatusBooking() == Booking.StatusBooking.PENDING).count();

        if (lblTotalBooking  != null) lblTotalBooking.setText(String.valueOf(total));
        if (lblBookingAktif  != null) lblBookingAktif.setText(String.valueOf(aktif));
        if (lblBookingPending != null) lblBookingPending.setText(String.valueOf(pending));
    }

    private void displayBookings(List<Booking> list) {
        if (tabelBooking == null) return;
        ObservableList<Booking> obs = FXCollections.observableArrayList(list != null ? list : List.of());
        tabelBooking.setItems(obs);
    }

    // ==================== FXML HANDLERS ====================

    @FXML
    void handleFilter(ActionEvent event) {
        applyFilter();
    }

    @FXML
    void handleReset(ActionEvent event) {
        if (tfCari  != null) tfCari.clear();
        if (cbStatus != null) cbStatus.getSelectionModel().selectFirst();
        displayBookings(allBookings);
    }

    private void applyFilter() {
        if (allBookings == null) return;

        String keyword = tfCari != null ? tfCari.getText().trim().toLowerCase() : "";
        String statusVal = cbStatus != null ? cbStatus.getValue() : "Semua";

        List<Booking> filtered = allBookings.stream().filter(b -> {
            // Filter keyword
            if (!keyword.isEmpty()) {
                String penyewa = b.getPenyewa() != null ? b.getPenyewa().getUsername().toLowerCase() : "";
                String kos = "";
                try { kos = b.getKamar().getKos().getNamaKos().toLowerCase(); } catch (Exception ignored) {}
                String id = String.valueOf(b.getIdBooking());
                if (!penyewa.contains(keyword) && !kos.contains(keyword) && !id.contains(keyword)) return false;
            }
            // Filter status
            if (!"Semua".equals(statusVal)) {
                try {
                    Booking.StatusBooking st = Booking.StatusBooking.valueOf(statusVal);
                    if (b.getStatusBooking() != st) return false;
                } catch (Exception ignored) {}
            }
            return true;
        }).collect(Collectors.toList());

        displayBookings(filtered);
    }
}
