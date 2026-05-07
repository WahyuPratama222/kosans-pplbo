package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity untuk menyimpan notifikasi sistem untuk user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifikasi")
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notifikasi")
    private Integer idNotifikasi;

    /**
     * User penerima notifikasi
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    /**
     * Judul notifikasi
     */
    @Column(name = "judul", nullable = false, length = 255)
    private String judul;

    /**
     * Isi notifikasi
     */
    @Column(name = "isi", columnDefinition = "TEXT", nullable = false)
    private String isi;

    /**
     * Tipe notifikasi
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipe", nullable = false, length = 30)
    private TipeNotifikasi tipe;

    /**
     * Apakah notifikasi sudah dibaca
     */
    @Builder.Default
    @Column(name = "sudah_dibaca")
    private Boolean sudahDibaca = false;

    /**
     * Reference ke booking (jika terkait)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booking")
    private Booking booking;

    /**
     * Reference ke kos (jika terkait)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kos")
    private Kos kos;

    /**
     * Waktu notifikasi dibuat
     */
    @CreationTimestamp
    @Column(name = "waktu_notifikasi", updatable = false)
    private LocalDateTime waktuNotifikasi;

    /**
     * Waktu notifikasi dibaca
     */
    @Column(name = "waktu_dibaca")
    private LocalDateTime waktuDibaca;

    /**
     * Enum untuk tipe notifikasi
     */
    public enum TipeNotifikasi {
        BOOKING,        // Terkait booking
        PEMBAYARAN,     // Terkait pembayaran
        PESAN,          // Pesan dari pemilik/admin
        REMINDER,       // Pengingat (e.g., booking akan berakhir)
        INFO,           // Informasi umum
        WARNING,        // Peringatan
        ERROR           // Error/masalah
    }

    /**
     * Mark notifikasi sebagai sudah dibaca
     */
    public void markAsRead() {
        this.sudahDibaca = true;
        this.waktuDibaca = LocalDateTime.now();
    }
}
