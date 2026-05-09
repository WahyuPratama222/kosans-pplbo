package com.kosku.dao;

import com.kosku.model.Pembayaran;

public class PembayaranDAO extends BaseDAO<Pembayaran> {
    
    public java.util.List<Pembayaran> getPembayaranWaiting() {
        String hql = "FROM Pembayaran p WHERE p.statusVerifikasi = 'WAITING'";
        return listByQuery(hql, null, Pembayaran.class);
    }

    public java.util.List<Pembayaran> getAllPembayaran() {
        String hql = "SELECT p FROM Pembayaran p LEFT JOIN FETCH p.booking b LEFT JOIN FETCH b.penyewa";
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Pembayaran.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
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

    /**
     * Mengambil rekap pendapatan bulanan untuk chart
     */
    public java.util.Map<String, java.math.BigDecimal> getMonthlyRevenue(int limitMonths) {
        java.util.Map<String, java.math.BigDecimal> revenueData = new java.util.LinkedHashMap<>();
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUBSTRING(CAST(p.createdAt AS string), 1, 7) as month, SUM(p.jumlahBayar) " +
                         "FROM Pembayaran p " +
                         "WHERE p.statusVerifikasi = 'VERIFIED' " +
                         "GROUP BY SUBSTRING(CAST(p.createdAt AS string), 1, 7) " +
                         "ORDER BY month DESC";
            org.hibernate.query.Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setMaxResults(limitMonths);
            java.util.List<Object[]> results = query.list();
            
            // Balik urutan agar kronologis (Jan -> Feb -> Mar)
            java.util.Collections.reverse(results);
            
            for (Object[] row : results) {
                revenueData.put((String) row[0], (java.math.BigDecimal) row[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return revenueData;
    }

    /**
     * Mengambil status verifikasi pembayaran untuk statistik
     */
    public java.util.Map<String, Long> getPaymentStatusCounts() {
        java.util.Map<String, Long> counts = new java.util.HashMap<>();
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p.statusVerifikasi, COUNT(p) FROM Pembayaran p GROUP BY p.statusVerifikasi";
            org.hibernate.query.Query<Object[]> query = session.createQuery(hql, Object[].class);
            for (Object[] row : query.list()) {
                counts.put(row[0].toString(), (Long) row[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counts;
    }
}
