package com.kosku.dao;

import com.kosku.model.Pembayaran;

public class PembayaranDAO extends BaseDAO<Pembayaran> {
    
    public java.util.List<Pembayaran> getPembayaranWaiting() {
        String hql = "FROM Pembayaran p WHERE p.statusVerifikasi = 'WAITING'";
        return listByQuery(hql, null, Pembayaran.class);
    }

    public java.util.List<Pembayaran> getAllPembayaran() {
        return getAll(Pembayaran.class);
    }

    public java.math.BigDecimal getTotalPembayaranBulanan() {
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            // Kita asumsikan hanya menghitung yang VERIFIED
            String hql = "select sum(p.jumlahBayar) from Pembayaran p where p.statusVerifikasi = 'VERIFIED'";
            org.hibernate.query.Query<java.math.BigDecimal> query = session.createQuery(hql, java.math.BigDecimal.class);
            java.math.BigDecimal total = query.uniqueResult();
            return total != null ? total : java.math.BigDecimal.ZERO;
        } catch (Exception e) {
            throw new RuntimeException("Gagal menghitung total pembayaran: " + e.getMessage(), e);
        }
    }
}
