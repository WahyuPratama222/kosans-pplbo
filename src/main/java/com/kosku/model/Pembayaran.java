package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pembayaran")
public class Pembayaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembayaran")
    private int idPembayaran;

    @ManyToOne
    @JoinColumn(name = "id_booking", nullable = false)
    private Booking booking; // Sekarang dia pegang satu objek Booking utuh

    @Column(name = "jumlah_bayar", nullable = false)
    private java.math.BigDecimal jumlahBayar;

    @Column(name = "bukti_bayar")
    private String buktiBayar; // Path file gambar (misal: "uploads/bukti-01.jpg")

    @Column(name = "status_verifikasi", length = 20)
    private String statusVerifikasi; // "WAITING", "VERIFIED", "REJECTED"

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Callback Otomatisasi Waktu ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.statusVerifikasi == null) {
            this.statusVerifikasi = "WAITING"; // Default status
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}