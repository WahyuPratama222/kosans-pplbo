-- Kosans Database Schema
-- Sistem Manajemen Kos

-- Tabel: users
-- Menyimpan data pengguna (Admin, Pemilik, Penyewa)
CREATE DATABASE IF NOT EXISTS kosan_db;
USE kosan_db;

CREATE TABLE IF NOT EXISTS users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'PEMILIK', 'PENYEWA') NOT NULL,
    nomor_hp VARCHAR(20),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabel: kos
-- Menyimpan data properti kos

CREATE TABLE IF NOT EXISTS kos (
    id_kos INT AUTO_INCREMENT PRIMARY KEY,
    id_pemilik INT NOT NULL,
    nama_kos VARCHAR(100) NOT NULL,
    alamat TEXT NOT NULL,
    deskripsi TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_pemilik) REFERENCES users(id_user) ON DELETE CASCADE,
    INDEX idx_pemilik (id_pemilik),
    INDEX idx_verified (is_verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabel: kamar
-- Menyimpan data kamar-kamar di kos

CREATE TABLE IF NOT EXISTS kamar (
    id_kamar INT AUTO_INCREMENT PRIMARY KEY,
    id_kos INT NOT NULL,
    nomor_kamar VARCHAR(20) NOT NULL,
    tipe_kamar ENUM('REGULER', 'EXCLUSIVE') NOT NULL,
    harga DECIMAL(15, 2) NOT NULL,
    durasi_sewa ENUM('HARIAN', 'MINGGUAN', 'BULANAN', 'TAHUNAN') NOT NULL,
    status_tersedia BOOLEAN DEFAULT TRUE,
    catatan_tambahan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_kos) REFERENCES kos(id_kos) ON DELETE CASCADE,
    UNIQUE KEY unique_nomor_kamar_per_kos (id_kos, nomor_kamar),
    INDEX idx_kos (id_kos),
    INDEX idx_status (status_tersedia),
    INDEX idx_tipe (tipe_kamar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabel: booking
-- Menyimpan data pemesanan kamar

CREATE TABLE IF NOT EXISTS booking (
    id_booking INT AUTO_INCREMENT PRIMARY KEY,
    id_penyewa INT NOT NULL,
    id_kamar INT NOT NULL,
    tanggal_mulai DATE NOT NULL,
    tanggal_selesai DATE NOT NULL,
    tanggal_booking TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_booking ENUM('PENDING', 'DITERIMA', 'DITOLAK', 'SELESAI', 'DIBATALKAN') NOT NULL DEFAULT 'PENDING',
    total_harga DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_penyewa) REFERENCES users(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_kamar) REFERENCES kamar(id_kamar) ON DELETE CASCADE,
    INDEX idx_penyewa (id_penyewa),
    INDEX idx_kamar (id_kamar),
    INDEX idx_status (status_booking),
    INDEX idx_tanggal_mulai (tanggal_mulai),
    INDEX idx_tanggal_selesai (tanggal_selesai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabel: pembayaran
-- Menyimpan data pembayaran booking

CREATE TABLE IF NOT EXISTS pembayaran (
    id_pembayaran INT AUTO_INCREMENT PRIMARY KEY,
    id_booking INT NOT NULL,
    jumlah_bayar DECIMAL(15, 2) NOT NULL,
    bukti_bayar VARCHAR(255),
    status_verifikasi ENUM('WAITING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_booking) REFERENCES booking(id_booking) ON DELETE CASCADE,
    INDEX idx_booking (id_booking),
    INDEX idx_status (status_verifikasi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;