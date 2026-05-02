package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private int idBooking;

    // --- RELASI PRO: Banyak Booking dilakukan oleh satu Penyewa ---
    @ManyToOne
    @JoinColumn(name = "id_penyewa", nullable = false)
    private User penyewa; 

    // --- RELASI PRO: Banyak Booking merujuk ke satu Kamar ---
    @ManyToOne
    @JoinColumn(name = "id_kamar", nullable = false)
    private Kamar kamar; 

    @Column(name = "tanggal_mulai")
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_selesai")
    private LocalDate tanggalSelesai;

    @Column(name = "tanggal_booking")
    private LocalDateTime tanggalBooking;

    @Column(name = "status_booking", length = 20)
    private String statusBooking; // "PENDING", "DITERIMA", "DITOLAK"

    @Column(name = "total_harga")
    private double totalHarga;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relasi ke Review (Satu booking hanya bisa satu ulasan)
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review;

    // --- Callback Otomatisasi ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.tanggalBooking == null) {
            this.tanggalBooking = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}