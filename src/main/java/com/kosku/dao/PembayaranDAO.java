package com.kosku.dao;

import com.kosku.model.Pembayaran;

public class PembayaranDAO extends BaseDAO<Pembayaran> {

    public Pembayaran getPembayaranByBooking(com.kosku.model.Booking booking) {
        String hql = "FROM Pembayaran p WHERE p.booking = :booking";
        return singleByQuery(hql, java.util.Map.of("booking", booking), Pembayaran.class);
    }

    public java.util.List<Pembayaran> getAllPembayaranWithDetails() {
        String hql = "SELECT p FROM Pembayaran p " +
                "JOIN FETCH p.booking b " +
                "JOIN FETCH b.penyewa " +
                "ORDER BY p.createdAt DESC";
        return listByQuery(hql, null, Pembayaran.class);
    }

    public java.util.List<Pembayaran> getPembayaranByPemilik(int idPemilik) {
        String hql = "SELECT p FROM Pembayaran p " +
                "JOIN FETCH p.booking b " +
                "JOIN FETCH b.kamar k " +
                "JOIN FETCH k.kos kos " +
                "JOIN FETCH b.penyewa " +
                "WHERE kos.pemilik.idUser = :idPemilik " +
                "ORDER BY p.createdAt DESC";
        return listByQuery(hql, java.util.Map.of("idPemilik", idPemilik), Pembayaran.class);
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

            java.util.Collections.reverse(results);

            for (Object[] row : results) {
                revenueData.put((String) row[0], (java.math.BigDecimal) row[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return revenueData;
    }

    public java.util.Map<String, java.math.BigDecimal> getMonthlyRevenueByPemilik(int limitMonths, int idPemilik) {
        java.util.Map<String, java.math.BigDecimal> revenueData = new java.util.LinkedHashMap<>();
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUBSTRING(CAST(p.createdAt AS string), 1, 7) as month, SUM(p.jumlahBayar) " +
                    "FROM Pembayaran p " +
                    "JOIN p.booking b JOIN b.kamar k JOIN k.kos kos " +
                    "WHERE p.statusVerifikasi = 'VERIFIED' AND kos.pemilik.idUser = :idPemilik " +
                    "GROUP BY SUBSTRING(CAST(p.createdAt AS string), 1, 7) " +
                    "ORDER BY month DESC";
            org.hibernate.query.Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("idPemilik", idPemilik);
            query.setMaxResults(limitMonths);
            java.util.List<Object[]> results = query.list();

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

    public java.util.Map<String, Long> getPaymentStatusCountsByPemilik(int idPemilik) {
        java.util.Map<String, Long> counts = new java.util.HashMap<>();
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p.statusVerifikasi, COUNT(p) " +
                         "FROM Pembayaran p " +
                         "JOIN p.booking b JOIN b.kamar k JOIN k.kos kos " +
                         "WHERE kos.pemilik.idUser = :idPemilik " +
                         "GROUP BY p.statusVerifikasi";
            org.hibernate.query.Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("idPemilik", idPemilik);
            for (Object[] row : query.list()) {
                counts.put(row[0].toString(), (Long) row[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counts;
    }

    public java.math.BigDecimal getTotalPembayaranBulanan() {
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            // Sederhana: hitung total semua pembayaran verified
            // Idealnya bisa ditambahkan filter bulan berjalan (MONTH(createdAt))
            String hql = "SELECT SUM(p.jumlahBayar) FROM Pembayaran p WHERE p.statusVerifikasi = 'VERIFIED'";
            org.hibernate.query.Query<java.math.BigDecimal> query = session.createQuery(hql,
                    java.math.BigDecimal.class);
            java.math.BigDecimal result = query.uniqueResult();
            return result != null ? result : java.math.BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return java.math.BigDecimal.ZERO;
        }
    }

    public java.math.BigDecimal getTotalPembayaranBulananByPemilik(int idPemilik) {
        try (org.hibernate.Session session = com.kosku.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(p.jumlahBayar) FROM Pembayaran p " +
                         "JOIN p.booking b JOIN b.kamar k JOIN k.kos kos " +
                         "WHERE p.statusVerifikasi = 'VERIFIED' AND kos.pemilik.idUser = :idPemilik";
            org.hibernate.query.Query<java.math.BigDecimal> query = session.createQuery(hql, java.math.BigDecimal.class);
            query.setParameter("idPemilik", idPemilik);
            java.math.BigDecimal result = query.uniqueResult();
            return result != null ? result : java.math.BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return java.math.BigDecimal.ZERO;
        }
    }
}

    
    
