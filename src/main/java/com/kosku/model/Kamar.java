package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kamar")
public class Kamar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kamar")
    private int idKamar;

    // --- RELASI PRO: Banyak Kamar dimiliki oleh satu Kos ---
    @ManyToOne
    @JoinColumn(name = "id_kos", nullable = false)
    private Kos kos; 

    @Column(name = "nomor_kamar", nullable = false, length = 20)
    private String nomorKamar;

    @Column(name = "tipe_kamar", length = 30)
    private String tipeKamar; // "REGULER", "EXCLUSIVE"

    @Column(nullable = false)
    private java.math.BigDecimal harga;

    @Column(name = "durasi_sewa")
    private String durasiSewa; // "HARIAN", "BULANAN"

    @Column(name = "status_tersedia")
    private boolean statusTersedia; // true = kosong, false = terisi

    @Column(name = "catatan_tambahan", columnDefinition = "TEXT")
    private String catatanTambahan;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Callback Otomatisasi ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Default: Kamar baru biasanya tersedia
        this.statusTersedia = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}