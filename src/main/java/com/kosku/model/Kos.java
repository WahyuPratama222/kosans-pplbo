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
@Table(name = "kos")
public class Kos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kos")
    private Integer idKos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pemilik", nullable = false)
    private User pemilik;

    @Column(name = "nama_kos", nullable = false, length = 100)
    private String namaKos;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String alamat;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "kos", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kamar> kamarList;

    @PrePersist
    protected void onCreate() {
        if (isVerified == null) {
            isVerified = false;
        }
    }
}