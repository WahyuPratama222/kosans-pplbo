-- ==========================================
-- Database Schema untuk Sistem Manajemen Kos (KosKu)
-- Database: kosku_db
-- ==========================================

-- Buat Database
CREATE DATABASE IF NOT EXISTS kosku_db;
USE kosku_db;

-- ==========================================
-- Tabel 1: USER
-- Untuk menyimpan data pengguna (Admin, Pemilik Kos, Penyewa)
-- ==========================================
CREATE TABLE IF NOT EXISTS user (
    idUser INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'PEMILIK', 'PENYEWA') NOT NULL,
    nomorHP VARCHAR(15),
    isVerified BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Tabel 2: KOS
-- Untuk menyimpan data Kos milik Pemilik
-- ==========================================
CREATE TABLE IF NOT EXISTS kos (
    idKos INT PRIMARY KEY AUTO_INCREMENT,
    idPemilik INT NOT NULL,
    namaKos VARCHAR(100) NOT NULL,
    alamat VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    isVerified BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (idPemilik) REFERENCES user(idUser) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_idPemilik (idPemilik),
    INDEX idx_isVerified (isVerified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Tabel 3: KAMAR
-- Untuk menyimpan data Kamar/Ruangan dalam Kos
-- ==========================================
CREATE TABLE IF NOT EXISTS kamar (
    idKamar INT PRIMARY KEY AUTO_INCREMENT,
    idKos INT NOT NULL,
    nomorKamar VARCHAR(20) NOT NULL,
    tipeKamar ENUM('REGULER', 'EXCLUSIVE') NOT NULL,
    harga DECIMAL(10, 2) NOT NULL,
    durasiSewa VARCHAR(50) NOT NULL DEFAULT 'BULANAN',
    statusTersedia BOOLEAN DEFAULT TRUE,
    catatanTambahan TEXT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (idKos) REFERENCES kos(idKos) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_idKos (idKos),
    INDEX idx_statusTersedia (statusTersedia),
    UNIQUE KEY uk_nomorKamar (idKos, nomorKamar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Tabel 4: BOOKING
-- Untuk menyimpan data Pemesanan Kamar
-- ==========================================
CREATE TABLE IF NOT EXISTS booking (
    idBooking INT PRIMARY KEY AUTO_INCREMENT,
    idPenyewa INT NOT NULL,
    idKamar INT NOT NULL,
    tanggalMulai DATE NOT NULL,
    tanggalSelesai DATE NOT NULL,
    tanggalBooking TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statusBooking ENUM('PENDING', 'DITERIMA', 'DITOLAK') DEFAULT 'PENDING',
    totalHarga DECIMAL(12, 2) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (idPenyewa) REFERENCES user(idUser) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (idKamar) REFERENCES kamar(idKamar) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_idPenyewa (idPenyewa),
    INDEX idx_idKamar (idKamar),
    INDEX idx_statusBooking (statusBooking),
    INDEX idx_tanggalMulai (tanggalMulai),
    INDEX idx_tanggalSelesai (tanggalSelesai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Tabel 5: PEMBAYARAN
-- Untuk menyimpan data Pembayaran Booking
-- ==========================================
CREATE TABLE IF NOT EXISTS pembayaran (
    idPembayaran INT PRIMARY KEY AUTO_INCREMENT,
    idBooking INT NOT NULL,
    jumlahBayar DECIMAL(12, 2) NOT NULL,
    buktiBayar VARCHAR(255),
    statusVerifikasi ENUM('WAITING', 'VERIFIED', 'REJECTED') DEFAULT 'WAITING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (idBooking) REFERENCES booking(idBooking) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_idBooking (idBooking),
    INDEX idx_statusVerifikasi (statusVerifikasi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Data Contoh (Optional - Uncomment untuk testing)
-- ==========================================
/*
-- Insert sample user
INSERT INTO user (username, password, email, role, nomorHP, isVerified) VALUES
('admin123', MD5('admin123'), 'admin@kosku.com', 'ADMIN', '081234567890', TRUE),
('pemilik1', MD5('pemilik123'), 'pemilik1@kosku.com', 'PEMILIK', '082345678901', TRUE),
('penyewa1', MD5('penyewa123'), 'penyewa1@kosku.com', 'PENYEWA', '083456789012', FALSE);

-- Insert sample kos
INSERT INTO kos (idPemilik, namaKos, alamat, deskripsi, isVerified) VALUES
(2, 'Kos Nyaman Jaya', 'Jl. Merdeka No. 123, Jakarta', 'Kos dengan fasilitas lengkap', TRUE);

-- Insert sample kamar
INSERT INTO kamar (idKos, nomorKamar, tipeKamar, harga, durasiSewa, statusTersedia) VALUES
(1, '101', 'REGULER', 500000, 'BULANAN', TRUE),
(1, '102', 'EXCLUSIVE', 800000, 'BULANAN', FALSE);
*/

-- ==========================================
-- Verifikasi Struktur Database
-- ==========================================
SHOW TABLES;
DESC user;
DESC kos;
DESC kamar;
DESC booking;
DESC pembayaran;
