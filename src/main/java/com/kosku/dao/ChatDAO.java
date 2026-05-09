package com.kosku.dao;

import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
     * @return List pesan diurut dari yang terlama ke terbaru
     */
    public List<Chat> getMessagesBetweenUsers(Integer pengirimId, Integer penerimaId) {
        String hql = "SELECT c FROM Chat c " +
                "JOIN FETCH c.pengirim " +
                "JOIN FETCH c.penerima " +
                "WHERE (c.pengirim.idUser = :pengirimId AND c.penerima.idUser = :penerimaId) " +
                "OR (c.pengirim.idUser = :penerimaId AND c.penerima.idUser = :pengirimId) " +
                "ORDER BY c.waktuPesan ASC";
        return listByQuery(hql, Map.of("pengirimId", pengirimId, "penerimaId", penerimaId), Chat.class);
    }

    /**
     * Mengambil riwayat percakapan antara dua user (Versi Object)
     */
    public List<Chat> getChatHistory(User user1, User user2) {
        return getMessagesBetweenUsers(user1.getIdUser(), user2.getIdUser());
    }

    /**
     * Ambil daftar user yang pernah berkomunikasi dengan user tertentu
     */
    public List<User> getChatPartners(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT u FROM User u WHERE u.idUser IN (" +
                    "  SELECT c.pengirim.idUser FROM Chat c WHERE c.penerima.idUser = :userId " +
                    "  UNION " +
                    "  SELECT c.penerima.idUser FROM Chat c WHERE c.pengirim.idUser = :userId" +
                    ") AND u.idUser != :userId";
            return session.createQuery(hql, User.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    /**
     * Mengambil daftar user yang pernah berinteraksi chat dengan user tertentu (Versi Object)
     */
    public List<User> getChatContacts(User user) {
        return getChatPartners(user.getIdUser());
    }

    /**
     * Mendapatkan pesan terakhir antara dua user untuk preview
     */
    public Chat getLastMessage(User user1, User user2) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Chat WHERE " +
                         "(pengirim = :u1 AND penerima = :u2) OR " +
                         "(pengirim = :u2 AND penerima = :u1) " +
                         "ORDER BY waktuPesan DESC";
            Query<Chat> query = session.createQuery(hql, Chat.class);
            query.setParameter("u1", user1);
            query.setParameter("u2", user2);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Hitung jumlah pesan yang belum dibaca untuk user tertentu
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
     * Hapus pesan
     */
    public void deleteMessage(Integer chatId) {
        delete(Chat.class, chatId);
    }
}
