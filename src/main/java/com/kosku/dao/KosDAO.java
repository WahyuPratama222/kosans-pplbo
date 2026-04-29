package com.kosku.dao;

import com.kosku.model.Kos;
import com.kosku.model.User;
import java.util.List;
import java.util.Map;

public class KosDAO extends BaseDAO<Kos> {

    /**
     * Ambil semua kos milik seorang pemilik.
     * Gunakan JOIN FETCH jika ingin data pemilik langsung terbawa tanpa query tambahan.
     */
    public List<Kos> getByPemilik(User pemilik) {
        String hql = "SELECT k FROM Kos k JOIN FETCH k.pemilik WHERE k.pemilik = :pemilik";
        return listByQuery(hql, Map.of("pemilik", pemilik), Kos.class);
    }

    /**
     * Ambil semua kos yang sudah terverifikasi admin.
     */
    public List<Kos> getKosVerified() {
        String hql = "FROM Kos k WHERE k.isVerified = true";
        return listByQuery(hql, null, Kos.class);
    }

    /**
     * Cari kos berdasarkan nama atau alamat (case-insensitive).
     */
    public List<Kos> searchKos(String keyword) {
        String hql = "FROM Kos k WHERE LOWER(k.namaKos) LIKE :kw OR LOWER(k.alamat) LIKE :kw";
        String likeKeyword = "%" + keyword.toLowerCase() + "%";
        return listByQuery(hql, Map.of("kw", likeKeyword), Kos.class);
    }
}