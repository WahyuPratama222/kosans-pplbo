package com.kosku.dao;

import com.kosku.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class BaseDAO<T> {

    // Save atau update entitas, dengan handling untuk objek "Detached"
    public T saveOrUpdate(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // merge mengembalikan instance yang "managed"
            T mergedEntity = (T) session.merge(entity); 
            transaction.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Gagal simpan/update: " + e.getMessage(), e);
        }
    }

    // Delete berdasarkan ID, dengan pengecekan apakah entitas ada sebelum dihapus
    public void delete(Class<T> type, int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(type, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Gagal hapus: " + e.getMessage(), e);
        }
    }

    // Get by ID, dengan handling untuk kasus tidak ditemukan (mengembalikan null)
    public T getById(Class<T> type, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(type, id);
        } catch (Exception e) {
            throw new RuntimeException("Gagal ambil data by ID: " + e.getMessage(), e);
        }
    }

    // Get all, dengan query yang lebih aman (gunakan nama class lengkap jika perlu)
    public List<T> getAll(Class<T> type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Gunakan Simple Name jika entity sudah terdaftar, tapi getName() tetap paling aman
            return session.createQuery("from " + type.getSimpleName(), type).list();
        } catch (Exception e) {
            throw new RuntimeException("Gagal ambil semua data: " + e.getMessage(), e);
        }
    }

    // Metode tambahan untuk query dinamis, dengan parameter map untuk fleksibilitas
    protected <R> List<R> listByQuery(String hql, java.util.Map<String, Object> params, Class<R> resultType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<R> query = session.createQuery(hql, resultType);
            if (params != null) {
                params.forEach(query::setParameter);
            }
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Gagal menjalankan query: " + e.getMessage(), e);
        }
    }
}