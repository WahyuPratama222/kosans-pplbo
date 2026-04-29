package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Integer idBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_penyewa", nullable = false)
    private User penyewa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kamar", nullable = false)
    private Kamar kamar;

    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_selesai", nullable = false)
    private LocalDate tanggalSelesai;

    @Column(name = "tanggal_booking")
    private LocalDateTime tanggalBooking;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_booking", nullable = false, length = 20)
    @Builder.Default // Agar nilai default tidak hilang saat pakai builder
    private StatusBooking statusBooking = StatusBooking.PENDING;

    @Column(name = "total_harga", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalHarga;

    @CreationTimestamp // Otomatis diisi oleh Hibernate saat insert
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Otomatis diupdate oleh Hibernate saat update
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pembayaran> pembayaranList;

    public enum StatusBooking {
        PENDING, DITERIMA, DITOLAK, SELESAI, DIBATALKAN
    }

    @PrePersist
    protected void onCreate() {
        if (this.tanggalBooking == null) {
            this.tanggalBooking = LocalDateTime.now();
        }
        // CreatedAt dan UpdatedAt sudah dihandle @CreationTimestamp/@UpdateTimestamp
    }
}