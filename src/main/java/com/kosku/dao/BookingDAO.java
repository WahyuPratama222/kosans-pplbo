package com.kosku.dao;

import com.kosku.model.Booking;
import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class BookingDAO extends BaseDAO<Booking> {

    /**
     * Ambil semua booking untuk kamar-kamar dalam satu kos tertentu.
     * Digunakan oleh PEMILIK untuk melihat booking masuk.
     */
    public List<Booking> getBookingByKos(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Booking b WHERE b.kamar.kos = :kos ORDER BY b.tanggalBooking DESC", Booking.class)
                    .setParameter("kos", kos)
                    .list();
        }
    }

    /**
     * Ambil booking yang masih PENDING untuk kamar dalam kos tertentu.
     */
    public List<Booking> getBookingPendingByKos(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Booking b WHERE b.kamar.kos = :kos AND b.statusBooking = 'PENDING' ORDER BY b.tanggalBooking ASC", Booking.class)
                    .setParameter("kos", kos)
                    .list();
        }
    }

    /**
     * Ambil semua booking milik seorang penyewa.
     */
    public List<Booking> getBookingByPenyewa(int idPenyewa) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Booking b WHERE b.penyewa.idUser = :idPenyewa ORDER BY b.tanggalBooking DESC", Booking.class)
                    .setParameter("idPenyewa", idPenyewa)
                    .list();
        }
    }

    /**
     * Hitung booking aktif (DITERIMA) untuk sebuah kamar.
     */
    public long countBookingAktifByKamar(Kamar kamar) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(b) FROM com.kosku.model.Booking b WHERE b.kamar = :kamar AND b.statusBooking = 'DITERIMA'", Long.class)
                    .setParameter("kamar", kamar)
                    .uniqueResult();
        }
    }
}
