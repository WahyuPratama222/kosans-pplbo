package com.kosku.model;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity untuk menyimpan pesan chat antara penyewa dan pemilik kos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Integer idChat;

    /**
     * Pengirim pesan (user yang mengirim)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengirim", nullable = false)
    private User pengirim;

    /**
     * Penerima pesan (user yang menerima)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_penerima", nullable = false)
    private User penerima;

    /**
     * Kos yang menjadi topik chat (optional, untuk konteks)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kos")
    private Kos kos;

    /**
     * Isi pesan
     */
    @Column(name = "isi_pesan", columnDefinition = "TEXT", nullable = false)
    private String isiPesan;

    /**
     * Apakah pesan sudah dibaca
     */
    @Builder.Default
    @Column(name = "sudah_dibaca")
    private Boolean sudahDibaca = false;

    /**
     * Waktu pesan dikirim
     */
    @CreationTimestamp
    @Column(name = "waktu_pesan", updatable = false)
    private LocalDateTime waktuPesan;

    /**
     * Enum untuk tipe chat
     */
    public enum TipeChat {
        PENYEWA_PEMILIK,    // Chat antara penyewa dan pemilik
        PENYEWA_ADMIN,      // Chat antara penyewa dan admin
        PEMILIK_ADMIN       // Chat antara pemilik dan admin
    }

    /**
     * Tipe chat
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipe_chat", nullable = false)
    @Builder.Default
    private TipeChat tipeChat = TipeChat.PENYEWA_PEMILIK;
}
