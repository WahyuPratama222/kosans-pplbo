package com.kosku.dao;

import com.kosku.model.Review;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class ReviewDAO extends BaseDAO<Review> {

    /**
     * Mengambil daftar ulasan untuk kos tertentu berdasarkan ID Kos
     */
    public List<Review> getReviewsByKos(int idKos) {
        return getReviewsByKos(idKos, 100, 0); // Default memanggil versi pagination dengan limit besar jika dipakai di tempat lama
    }

    /**
     * Mengambil daftar ulasan dengan pagination (limit & offset)
     */
    public List<Review> getReviewsByKos(int idKos, int limit, int offset) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Review r " +
                    "JOIN FETCH r.penyewa " + 
                    "JOIN r.booking b " +
                    "JOIN b.kamar k " +
                    "WHERE k.kos.idKos = :idKos " +
                    "ORDER BY r.createdAt DESC";
            return session.createQuery(hql, Review.class)
                    .setParameter("idKos", idKos)
                    .setMaxResults(limit)
                    .setFirstResult(offset)
                    .list();
        }
    }

    /**
     * Menghitung rata-rata rating untuk sebuah kos
     */
    public Double getAverageRating(int idKos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(r.rating) FROM Review r JOIN r.booking b JOIN b.kamar k WHERE k.kos.idKos = :idKos";
            return session.createQuery(hql, Double.class)
                    .setParameter("idKos", idKos)
                    .uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Gagal menghitung rata-rata rating: " + e.getMessage(), e);
        }
    }

    /**
     * Mengambil ulasan berdasarkan ID Booking
     * Mengembalikan null jika belum ada ulasan
     */
    public Review getReviewByBooking(int idBooking) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Review r WHERE r.booking.idBooking = :idBooking";
            return session.createQuery(hql, Review.class)
                    .setParameter("idBooking", idBooking)
                    .uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mencari review berdasarkan booking: " + e.getMessage(), e);
        }
    }
}