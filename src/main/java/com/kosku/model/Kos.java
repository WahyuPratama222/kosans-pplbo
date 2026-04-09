package com.kosku.model;

import java.time.LocalDateTime;

public class Kos {

    private int idKos;
    private int idPemilik; // Relasi ke User
    private String namaKos;
    private String alamat;
    private String deskripsi;
    private boolean isVerified; // Verifikasi oleh Admin
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, Getter, dan Setter
}
