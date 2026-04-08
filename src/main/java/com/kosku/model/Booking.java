package com.kosku.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private int idBooking;
    private int idPenyewa; // Relasi ke User
    private int idKamar;   // Relasi ke Kamar
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private LocalDateTime tanggalBooking;
    private String statusBooking; // "PENDING", "DITERIMA", "DITOLAK"
    private double totalHarga;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, Getter, dan Setter
}
