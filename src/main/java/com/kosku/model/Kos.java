package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kos")
public class Kos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kos")
    private int idKos;

    // --- RELASI: Banyak Kos dimiliki oleh satu Pemilik ---
    @ManyToOne
    @JoinColumn(name = "id_pemilik", nullable = false)
    private User pemilik; 

    @Column(name = "nama_kos", nullable = false)
    private String namaKos;

    @Column(columnDefinition = "TEXT") // Pakai TEXT biar deskripsi bisa panjang
    private String alamat;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "is_verified")
    private boolean isVerified; 

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Callback Otomatisasi Waktu ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Logika default: Kos baru biasanya belum diverifikasi admin
        this.isVerified = false; 
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}