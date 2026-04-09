package com.kosku.model;

import java.time.LocalDateTime;

public class User {

    private int idUser;
    private String username;
    private String password;
    private String email;
    private String role; // "ADMIN", "PEMILIK", "PENYEWA"
    private String nomorHP;
    private boolean isVerified; // Untuk pemilik kos (KF-04)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, Getter, dan Setter
}
