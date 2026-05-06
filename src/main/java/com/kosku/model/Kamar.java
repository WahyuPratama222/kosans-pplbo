package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kamar", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_kos", "nomor_kamar"})
})
public class Kamar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kamar")
    private Integer idKamar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kos", nullable = false)
    private Kos kos;

    @Column(name = "nomor_kamar", nullable = false, length = 20)
    private String nomorKamar;

    @Builder.Default // Agar nilai true tidak hilang saat pakai builder
    @Column(name = "status_tersedia")
    private Boolean statusTersedia = true;

    @Column(name = "catatan_tambahan", columnDefinition = "TEXT")
    private String catatanTambahan;

    @CreationTimestamp // Lebih ringkas dari @PrePersist
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Lebih ringkas dari @PreUpdate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "kamar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookingList;

    @PrePersist
    protected void onCreate() {
        if (statusTersedia == null) {
            statusTersedia = true;
        }
    }
}