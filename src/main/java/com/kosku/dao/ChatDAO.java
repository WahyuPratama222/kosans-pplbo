package com.kosku.dao;

import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class ChatDAO extends BaseDAO<Chat> {

    /**
     * Mengambil riwayat percakapan antara dua user
     */
    public List<Chat> getChatHistory(User user1, User user2) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Chat WHERE " +
                         "(pengirim = :u1 AND penerima = :u2) OR " +
                         "(pengirim = :u2 AND penerima = :u1) " +
                         "ORDER BY waktuKirim ASC";
            Query<Chat> query = session.createQuery(hql, Chat.class);
            query.setParameter("u1", user1);
            query.setParameter("u2", user2);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil riwayat chat: " + e.getMessage(), e);
        }
    }

    /**
     * Mengambil daftar user yang pernah berinteraksi chat dengan user tertentu
     */
    public List<User> getChatContacts(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query untuk mendapatkan user unik yang pernah berkirim pesan dengan 'user'
            String hql = "SELECT DISTINCT u FROM User u WHERE u.idUser IN (" +
                         "  SELECT c.penerima.idUser FROM Chat c WHERE c.pengirim = :u UNION " +
                         "  SELECT c.pengirim.idUser FROM Chat c WHERE c.penerima = :u" +
                         ") AND u.idUser != :uid";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("u", user);
            query.setParameter("uid", user.getIdUser());
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil kontak chat: " + e.getMessage(), e);
        }
    }

    /**
     * Mendapatkan pesan terakhir antara dua user untuk preview
     */
    public Chat getLastMessage(User user1, User user2) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Chat WHERE " +
                         "(pengirim = :u1 AND penerima = :u2) OR " +
                         "(pengirim = :u2 AND penerima = :u1) " +
                         "ORDER BY waktuKirim DESC";
            Query<Chat> query = session.createQuery(hql, Chat.class);
            query.setParameter("u1", user1);
            query.setParameter("u2", user2);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }
}
