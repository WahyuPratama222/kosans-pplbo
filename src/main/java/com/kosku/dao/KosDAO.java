package com.kosku.dao;

import com.kosku.model.Kos;
import com.kosku.model.User;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class KosDAO extends BaseDAO<Kos> {

    /**
     * Ambil semua kos milik seorang pemilik berdasarkan objek User (pemilik).
     */
    public List<Kos> getByPemilik(User pemilik) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Kos k WHERE k.pemilik = :pemilik", Kos.class)
                    .setParameter("pemilik", pemilik)
                    .list();
        }
    }

    /**
     * Ambil semua kos yang sudah terverifikasi admin (untuk ditampilkan ke penyewa).
     */
    public List<Kos> getKosVerified() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Kos k WHERE k.isVerified = true", Kos.class)
                    .list();
        }
    }

    /**
     * Cari kos berdasarkan nama atau alamat (case-insensitive).
     */
    public List<Kos> searchKos(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String like = "%" + keyword.toLowerCase() + "%";
            return session.createQuery(
                    "FROM com.kosku.model.Kos k WHERE LOWER(k.namaKos) LIKE :kw OR LOWER(k.alamat) LIKE :kw", Kos.class)
                    .setParameter("kw", like)
                    .list();
        }
    }
}
