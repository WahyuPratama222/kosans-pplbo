package com.kosku.dao;

import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class KamarDAO extends BaseDAO<Kamar> {

    /**
     * Ambil semua kamar dalam satu kos berdasarkan objek Kos.
     */
    public List<Kamar> getByKos(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Kamar k WHERE k.kos = :kos", Kamar.class)
                    .setParameter("kos", kos)
                    .list();
        }
    }

    /**
     * Ambil semua kamar yang tersedia dalam satu kos.
     */
    public List<Kamar> getKamarTersedia(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.kosku.model.Kamar k WHERE k.kos = :kos AND k.statusTersedia = true", Kamar.class)
                    .setParameter("kos", kos)
                    .list();
        }
    }

    /**
     * Hitung jumlah kamar tersedia dalam satu kos.
     */
    public long countKamarTersedia(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(k) FROM com.kosku.model.Kamar k WHERE k.kos = :kos AND k.statusTersedia = true", Long.class)
                    .setParameter("kos", kos)
                    .uniqueResult();
        }
    }

    /**
     * Hitung total kamar dalam satu kos.
     */
    public long countTotalKamar(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(k) FROM com.kosku.model.Kamar k WHERE k.kos = :kos", Long.class)
                    .setParameter("kos", kos)
                    .uniqueResult();
        }
    }
}
