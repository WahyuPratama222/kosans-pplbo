package com.kosku.dao;

import com.kosku.model.Kamar;
import com.kosku.model.Kos;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.Map;

public class KamarDAO extends BaseDAO<Kamar> {

    /**
     * Mengambil semua kamar dalam satu kos.
     * Menggunakan JOIN FETCH agar data Kos ikut terbawa dan tidak LazyInitializationException.
     */
    public List<Kamar> getByKos(Kos kos) {
        String hql = "SELECT k FROM Kamar k JOIN FETCH k.kos WHERE k.kos = :kos";
        return listByQuery(hql, Map.of("kos", kos), Kamar.class);
    }

    /**
     * Ambil semua kamar yang tersedia (statusTersedia = true).
     */
    public List<Kamar> getKamarTersedia(Kos kos) {
        String hql = "SELECT k FROM Kamar k WHERE k.kos = :kos AND k.statusTersedia = true";
        return listByQuery(hql, Map.of("kos", kos), Kamar.class);
    }

    /**
     * Hitung jumlah kamar tersedia. 
     * Untuk count, kita tetap buka session manual karena returnnya Long (bukan List).
     */
    public long countKamarTersedia(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(k) FROM Kamar k WHERE k.kos = :kos AND k.statusTersedia = true", Long.class)
                    .setParameter("kos", kos)
                    .uniqueResult();
        }
    }

    /**
     * Hitung total semua kamar dalam satu kos.
     */
    public long countTotalKamar(Kos kos) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(k) FROM Kamar k WHERE k.kos = :kos", Long.class)
                    .setParameter("kos", kos)
                    .uniqueResult();
        }
    }
}