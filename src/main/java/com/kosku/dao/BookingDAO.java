package com.kosku.dao;

import com.kosku.model.Booking;
import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.Map;

public class BookingDAO extends BaseDAO<Booking> {

    /**
     * Menggunakan JOIN FETCH sangat penting agar saat session ditutup,
     * kamu tetap bisa akses data Kamar dan Penyewa di Frontend/UI.
     */
    public List<Booking> getBookingByKos(Kos kos) {
        String hql = "SELECT b FROM Booking b " +
                "JOIN FETCH b.kamar k " +
                "JOIN FETCH b.penyewa p " +
                "WHERE k.kos = :kos ORDER BY b.tanggalBooking DESC";
        return listByQuery(hql, Map.of("kos", kos), Booking.class);
    }

    public List<Booking> getBookingPendingByKos(Kos kos) {
        String hql = "SELECT b FROM Booking b " +
                "JOIN FETCH b.kamar k " +
                "WHERE k.kos = :kos AND b.statusBooking = :status " +
                "ORDER BY b.tanggalBooking ASC";
        return listByQuery(hql,
                Map.of("kos", kos, "status", Booking.StatusBooking.PENDING),
                Booking.class);
    }

    public List<Booking> getBookingByPenyewa(int idPenyewa) {
        String hql = "SELECT b FROM Booking b " +
                "JOIN FETCH b.kamar k " +
                "JOIN FETCH k.kos " + // Ambil info kosnya juga
                "WHERE b.penyewa.idUser = :idPenyewa ORDER BY b.tanggalBooking DESC";
        return listByQuery(hql, Map.of("idPenyewa", idPenyewa), Booking.class);
    }

    public List<Booking> getBookingByPemilik(int idPemilik) {
        String hql = "SELECT b FROM Booking b " +
                "JOIN FETCH b.kamar k " +
                "JOIN FETCH k.kos kos " +
                "JOIN FETCH b.penyewa p " +
                "WHERE kos.pemilik.idUser = :idPemilik " +
                "ORDER BY b.tanggalBooking DESC";
        return listByQuery(hql, Map.of("idPemilik", idPemilik), Booking.class);
    }

    public long countBookingAktifByKamar(Kamar kamar) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(b) FROM Booking b " +
                    "WHERE b.kamar = :kamar AND b.statusBooking = :status";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("kamar", kamar)
                    .setParameter("status", Booking.StatusBooking.DITERIMA)
                    .uniqueResult();
            return count != null ? count : 0L;
        }
    }

    public List<Booking> getAllBookingWithDetails() {
        String hql = "SELECT b FROM Booking b " +
                "JOIN FETCH b.kamar k " +
                "JOIN FETCH k.kos " +
                "JOIN FETCH b.penyewa " +
                "ORDER BY b.tanggalBooking DESC";
        return listByQuery(hql, null, Booking.class);
    }

    public long getTotalActiveBookings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(b) FROM Booking b WHERE b.statusBooking = :status";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("status", Booking.StatusBooking.DITERIMA)
                    .uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            System.err.println("Gagal menghitung total booking: " + e.getMessage());
            return 0L;
        }
    }
}