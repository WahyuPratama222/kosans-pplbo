package com.kosku.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User pengirim;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User penerima;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pesan;

    @CreationTimestamp
    @Column(name = "waktu_kirim")
    private LocalDateTime waktuKirim;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;
}
