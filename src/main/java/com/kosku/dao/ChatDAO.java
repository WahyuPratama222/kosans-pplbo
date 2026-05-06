package com.kosku.dao;

import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.Map;

/**
 * DAO untuk operasi Chat
 */
public class ChatDAO extends BaseDAO<Chat> {

    /**
     * Ambil semua pesan antara dua user
     * 
     * @param pengirimId ID user pengirim
     * @param penerimaId ID user penerima
     * @return List pesan diurut dari yang terbaru
     */
    public List<Chat> getMessagesBetweenUsers(Integer pengirimId, Integer penerimaId) {
        String hql = "SELECT c FROM Chat c " +
                "JOIN FETCH c.pengirim " + // Ambil info pengirim sekalian
                "JOIN FETCH c.penerima " + // Ambil info penerima sekalian
                "WHERE (c.pengirim.idUser = :pengirimId AND c.penerima.idUser = :penerimaId) " +
                "OR (c.pengirim.idUser = :penerimaId AND c.penerima.idUser = :pengirimId) " +
                "ORDER BY c.waktuPesan ASC"; // Biasanya chat diurutkan dari yang terlama ke terbaru (bawah)
        return listByQuery(hql, Map.of("pengirimId", pengirimId, "penerimaId", penerimaId), Chat.class);
    }

    /**
     * Ambil semua pesan untuk user tertentu (sebagai penerima)
     * 
     * @param penerimaId ID user penerima
     * @return List pesan yang diterima
     */
    public List<Chat> getReceivedMessages(Integer penerimaId) {
        String hql = "SELECT c FROM Chat c " +
                "WHERE c.penerima.idUser = :penerimaId " +
                "ORDER BY c.waktuPesan DESC";
        return listByQuery(hql, Map.of("penerimaId", penerimaId), Chat.class);
    }

    /**
     * Ambil semua pesan yang dikirim user tertentu (sebagai pengirim)
     * 
     * @param pengirimId ID user pengirim
     * @return List pesan yang dikirim
     */
    public List<Chat> getSentMessages(Integer pengirimId) {
        String hql = "SELECT c FROM Chat c " +
                "WHERE c.pengirim.idUser = :pengirimId " +
                "ORDER BY c.waktuPesan DESC";
        return listByQuery(hql, Map.of("pengirimId", pengirimId), Chat.class);
    }

    /**
     * Hitung jumlah pesan yang belum dibaca untuk user tertentu
     * 
     * @param penerimaId ID user penerima
     * @return Jumlah pesan yang belum dibaca
     */
    public long countUnreadMessages(Integer penerimaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(c) FROM Chat c " +
                    "WHERE c.penerima.idUser = :penerimaId AND c.sudahDibaca = false";
            return session.createQuery(hql, Long.class)
                    .setParameter("penerimaId", penerimaId)
                    .uniqueResult();
        }
    }

    /**
     * Mark pesan sebagai sudah dibaca
     * 
     * @param chatId ID chat
     */
    public void markMessageAsRead(Integer chatId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                Chat chat = session.get(Chat.class, chatId);
                if (chat != null) {
                    chat.setSudahDibaca(true);
                    session.merge(chat);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Mark semua pesan dari pengirim tertentu sebagai sudah dibaca
     * 
     * @param penerimaId ID user penerima
     * @param pengirimId ID user pengirim
     */
    public void markAllMessagesAsRead(Integer penerimaId, Integer pengirimId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                String hql = "UPDATE Chat c SET c.sudahDibaca = true " +
                        "WHERE c.penerima.idUser = :penerimaId " +
                        "AND c.pengirim.idUser = :pengirimId " +
                        "AND c.sudahDibaca = false";
                session.createMutationQuery(hql)
                        .setParameter("penerimaId", penerimaId)
                        .setParameter("pengirimId", pengirimId)
                        .executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    /**
     * Ambil daftar user yang pernah berkomunikasi dengan user tertentu
     * 
     * @param userId ID user
     * @return List user yang pernah chat
     */
    public List<User> getChatPartners(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT u FROM User u WHERE u.idUser IN (" +
                    "  SELECT c.pengirim.idUser FROM Chat c WHERE c.penerima.idUser = :userId " +
                    "  UNION " +
                    "  SELECT c.penerima.idUser FROM Chat c WHERE c.pengirim.idUser = :userId" +
                    ")";
            return session.createQuery(hql, User.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    /**
     * Hapus pesan (soft delete atau hard delete)
     * 
     * @param chatId ID chat yang akan dihapus
     */
    public void deleteMessage(Integer chatId) {
        delete(Chat.class, chatId);
    }
}
