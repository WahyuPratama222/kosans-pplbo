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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "nomor_hp", length = 20)
    private String nomorHp;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relasi ke Kos (untuk role PEMILIK)
    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kos> kosList;

    // Relasi ke Booking (untuk role PENYEWA)
    @OneToMany(mappedBy = "penyewa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookingList;

    public enum Role {
        ADMIN, PEMILIK, PENYEWA
    }

    @PrePersist
    protected void onCreate() {
        if (isVerified == null) {
            isVerified = false;
        }
    }
}