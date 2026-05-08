package com.kosku.dao;

import com.kosku.model.Notifikasi;
import com.kosku.model.Notifikasi.TipeNotifikasi;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.Map;

/**
 * DAO untuk operasi Notifikasi
 */
public class NotifikasiDAO extends BaseDAO<Notifikasi> {

    /**
     * Ambil semua notifikasi untuk user tertentu
     * 
     * @param userId ID user
     * @return List notifikasi diurut dari yang terbaru
     */
    public List<Notifikasi> getNotificationsByUser(Integer userId) {
        String hql = "SELECT n FROM Notifikasi n " +
                "LEFT JOIN FETCH n.kos " + // Tambahkan fetch
                "LEFT JOIN FETCH n.booking " + // Tambahkan fetch
                "WHERE n.user.idUser = :userId " +
                "ORDER BY n.waktuNotifikasi DESC";
        return listByQuery(hql, Map.of("userId", userId), Notifikasi.class);
    }

    /**
     * Ambil notifikasi yang belum dibaca untuk user tertentu
     * 
     * @param userId ID user
     * @return List notifikasi yang belum dibaca
     */
    public List<Notifikasi> getUnreadNotifications(Integer userId) {
        String hql = "SELECT n FROM Notifikasi n " +
                "WHERE n.user.idUser = :userId AND n.sudahDibaca = false " +
                "ORDER BY n.waktuNotifikasi DESC";
        return listByQuery(hql, Map.of("userId", userId), Notifikasi.class);
    }

    /**
     * Ambil notifikasi berdasarkan tipe untuk user tertentu
     * 
     * @param userId ID user
     * @param tipe   Tipe notifikasi
     * @return List notifikasi dengan tipe tertentu
     */
    public List<Notifikasi> getNotificationsByType(Integer userId, TipeNotifikasi tipe) {
        String hql = "SELECT n FROM Notifikasi n " +
                "WHERE n.user.idUser = :userId AND n.tipe = :tipe " +
                "ORDER BY n.waktuNotifikasi DESC";
        return listByQuery(hql, Map.of("userId", userId, "tipe", tipe), Notifikasi.class);
    }

    /**
     * Hitung jumlah notifikasi yang belum dibaca untuk user tertentu
     * 
     * @param userId ID user
     * @return Jumlah notifikasi yang belum dibaca
     */
    public long countUnreadNotifications(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(n) FROM Notifikasi n " +
                    "WHERE n.user.idUser = :userId AND n.sudahDibaca = false";
            return session.createQuery(hql, Long.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }

    /**
     * Mark notifikasi sebagai sudah dibaca
     * 
     * @param notifikasiId ID notifikasi
     */
    public void markAsRead(Integer notifikasiId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                Notifikasi notifikasi = session.get(Notifikasi.class, notifikasiId);
                if (notifikasi != null) {
                    notifikasi.markAsRead();
                    session.merge(notifikasi);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Mark semua notifikasi untuk user sebagai sudah dibaca
     * 
     * @param userId ID user
     */
    public void markAllAsRead(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                String hql = "UPDATE Notifikasi n SET n.sudahDibaca = true " +
                        "WHERE n.user.idUser = :userId AND n.sudahDibaca = false";
                session.createMutationQuery(hql)
                        .setParameter("userId", userId)
                        .executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Hapus notifikasi tertentu
     * 
     * @param notifikasiId ID notifikasi
     */
    public void deleteNotification(Integer notifikasiId) {
        delete(Notifikasi.class, notifikasiId);
    }

    /**
     * Hapus semua notifikasi untuk user
     * 
     * @param userId ID user
     */
    public void deleteAllNotifications(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                String hql = "DELETE FROM Notifikasi n WHERE n.user.idUser = :userId";
                session.createMutationQuery(hql)
                        .setParameter("userId", userId)
                        .executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Hapus notifikasi yang sudah dibaca untuk user tertentu (cleanup)
     * 
     * @param userId ID user
     */
    public void deleteReadNotifications(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                String hql = "DELETE FROM Notifikasi n " +
                        "WHERE n.user.idUser = :userId AND n.sudahDibaca = true";
                session.createMutationQuery(hql)
                        .setParameter("userId", userId)
                        .executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Create notifikasi baru
     * 
     * @param notifikasi Object notifikasi
     * @return Notifikasi yang sudah disimpan
     */
    public Notifikasi createNotification(Notifikasi notifikasi) {
        return saveOrUpdate(notifikasi);
    }
}
