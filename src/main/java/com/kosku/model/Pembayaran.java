package com.kosku.model;

import java.time.LocalDateTime;

public class Pembayaran {
    private int idPembayaran;
    private int idBooking; // Relasi ke Booking
    private double jumlahBayar;
    private String buktiBayar; // Nama file/path gambar
    private String statusVerifikasi; // "WAITING", "VERIFIED", "REJECTED"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, Getter, dan Setter
}
