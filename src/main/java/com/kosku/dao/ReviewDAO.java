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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Review r JOIN r.booking b JOIN b.kamar k WHERE k.kos.idKos = :idKos";
            return session.createQuery(hql, Review.class)
                    .setParameter("idKos", idKos)
                    .list();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil review kos: " + e.getMessage(), e);
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
}