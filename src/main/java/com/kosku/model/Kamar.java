package com.kosku.model;

import java.time.LocalDateTime;

public class Kamar {

    private int idKamar;
    private int idKos; // Relasi ke Kos
    private String nomorKamar;
    private String tipeKamar; // "REGULER", "EXCLUSIVE"
    private double harga;
    private String durasiSewa; // "HARIAN", "BULANAN", dst.
    private boolean statusTersedia; // true = kosong, false = terisi
    private String catatanTambahan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, Getter, dan Setter
}
