package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter // Gunakan Getter & Setter secara terpisah agar lebih aman untuk JPA
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pembayaran")
public class Pembayaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembayaran")
    private Integer idPembayaran;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booking", nullable = false)
    private Booking booking;

    @Column(name = "jumlah_bayar", nullable = false, precision = 15, scale = 2)
    private BigDecimal jumlahBayar;

    @Column(name = "bukti_bayar", length = 255)
    private String buktiBayar;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_verifikasi", nullable = false, length = 20)
    @Builder.Default // Agar nilai default WAITING_PEMILIK tidak hilang saat pakai builder
    private StatusVerifikasi statusVerifikasi = StatusVerifikasi.WAITING_PEMILIK;

    @CreationTimestamp // Menggantikan manual set di @PrePersist
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Menggantikan manual set di @PreUpdate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StatusVerifikasi {
        WAITING_PEMILIK, WAITING_ADMIN, VERIFIED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        if (this.statusVerifikasi == null) {
            this.statusVerifikasi = StatusVerifikasi.WAITING_PEMILIK;
        }
    }
}