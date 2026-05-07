-- Kosans Database Seeder
-- Script untuk mengisi data sample ke database kosans_db

-- =====================================================
-- SEEDER DATA USERS
-- =====================================================

-- password seluruh password123
-- Admin Users
INSERT INTO users (username, password, email, role, nomor_hp, is_verified) VALUES
('admin_1', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'admin@kosans.com', 'ADMIN', '081234567890', TRUE),
('admin_2', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'admin2@kosans.com', 'ADMIN', '081234567891', TRUE);

-- Pemilik (Owner) Users
INSERT INTO users (username, password, email, role, nomor_hp, is_verified) VALUES
('pemilik_Rizki', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'budi@gmail.com', 'PEMILIK', '082123456789', TRUE),
('pemilik_Iqbal', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'siti@gmail.com', 'PEMILIK', '082987654321', TRUE),
('pemilik_Irpan', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'agus@gmail.com', 'PEMILIK', '082111111111', TRUE);

-- Penyewa (Tenant) Users
INSERT INTO users (username, password, email, role, nomor_hp, is_verified) VALUES
('penyewa_helen', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'aldi@student.com', 'PENYEWA', '083123456789', TRUE),
('penyewa_ropi', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'dina@student.com', 'PENYEWA', '083987654321', TRUE),
('penyewa_adha', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'roni@student.com', 'PENYEWA', '083222222222', TRUE),
('penyewa_bigmo', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'tina@student.com', 'PENYEWA', '083333333333', TRUE),
('penyewa_rian', '$2a$10$VE7gk0pdSV3uu6VYquDjHekoqZsd2K0vJt7gyJ3NJYz9ffBRoXoS6', 'rio@student.com', 'PENYEWA', '083444444444', TRUE);

-- =====================================================
-- SEEDER DATA KOS (Properties)
-- =====================================================

INSERT INTO kos (id_pemilik, nama_kos, alamat, deskripsi, tipe_kos, foto_kos, harga, durasi_sewa, is_verified) VALUES
(3, 'Kos Rizki Sentosa', 'Jl. Merdeka No. 123, Jakarta Pusat', 'Kos nyaman dekat kampus dengan fasilitas lengkap', 'PUTRA', 'images/tesKos', 500000, 'BULANAN', TRUE),
(4, 'Kos Iqbal Sejahtera', 'Jl. Ahmad Yani No. 456, Bandung', 'Kos eksklusif untuk mahasiswa perempuan', 'PUTRI', 'images/tesKos', 650000, 'BULANAN', TRUE),
(5, 'Kos Irpan Residence', 'Jl. Sudirman No. 789, Surabaya', 'Kos modern dengan WiFi dan AC', 'PUTRI', 'images/tesKos', 600000, 'BULANAN', TRUE),
(3, 'Kos Ropi Indah', 'Jl. Gatot Subroto No. 321, Jakarta Selatan', 'Kos terjangkau untuk semua kalangan', 'CAMPUR', 'images/tesKos', 400000, 'BULANAN', TRUE);

-- =====================================================
-- SEEDER DATA KAMAR (Rooms)
-- =====================================================

-- Kamar di Kos Rizki Sentosa (id_kos = 1)
INSERT INTO kamar (id_kos, nomor_kamar, status_tersedia, catatan_tambahan) VALUES
(1, 'A01', TRUE, 'Kamar dengan jendela besar'),
(1, 'A02', FALSE, 'Sedang dihuni'),
(1, 'A03', TRUE, 'Kamar premium dengan kamar mandi pribadi'),
(1, 'B01', TRUE, 'Kamar standard dengan ventilasi baik');

-- Kamar di Kos Iqbal Sejahtera (id_kos = 2)
INSERT INTO kamar (id_kos, nomor_kamar, status_tersedia, catatan_tambahan) VALUES
(2, 'R01', TRUE, 'Kamar rapi dan bersih'),
(2, 'R02', TRUE, 'Kamar dengan kulkas dan TV'),
(2, 'R03', FALSE, 'Sudah terisi');

-- Kamar di Kos Irpan Residence (id_kos = 3)
INSERT INTO kamar (id_kos, nomor_kamar, status_tersedia, catatan_tambahan) VALUES
(3, '101', TRUE, 'Kamar dengan AC'),
(3, '102', TRUE, 'Suite dengan ruang tamu'),
(3, '103', TRUE, 'Kamar tersedia'),
(3, '201', FALSE, 'Terisi penuh');

-- Kamar di Kos Rizki Indah (id_kos = 4)
INSERT INTO kamar (id_kos, nomor_kamar, status_tersedia, catatan_tambahan) VALUES
(4, 'D01', TRUE, 'Kamar ekonomis'),
(4, 'D02', TRUE, 'Dekat dapur bersama'),
(4, 'D03', TRUE, 'Kamar plus dengan TV kabel');

-- =====================================================
-- SEEDER DATA BOOKING
-- =====================================================

INSERT INTO booking (id_penyewa, id_kamar, tanggal_mulai, tanggal_selesai, status_booking, total_harga) VALUES
-- Booking aktif/selesai
(6, 2, '2026-01-15', '2026-04-15', 'DITERIMA', 1500000),
(7, 5, '2026-02-01', '2026-05-01', 'DITERIMA', 1350000),
(8, 7, '2026-03-01', '2026-06-01', 'DITERIMA', 1800000),
(9, 10, '2026-01-20', '2026-04-20', 'SELESAI', 1800000),
-- Booking pending
(6, 3, '2026-05-01', '2026-08-01', 'PENDING', 2250000),
(10, 6, '2026-05-15', '2026-08-15', 'PENDING', 1950000),
-- Booking ditolak/dibatalkan
(7, 8, '2026-02-15', '2026-02-22', 'DITOLAK', 600000),
(8, 4, '2026-01-01', '2026-01-10', 'DIBATALKAN', 150000);

-- =====================================================
-- SEEDER DATA PEMBAYARAN
-- =====================================================

INSERT INTO pembayaran (id_booking, jumlah_bayar, bukti_bayar, status_verifikasi) VALUES
-- Pembayaran terverifikasi
(1, 1500000, 'bukti_bayar_001.pdf', 'VERIFIED'),
(2, 1350000, 'bukti_bayar_002.pdf', 'VERIFIED'),
(3, 1800000, 'bukti_bayar_003.pdf', 'VERIFIED'),
(4, 1800000, 'bukti_bayar_004.pdf', 'VERIFIED'),
-- Pembayaran menunggu verifikasi
(5, 2250000, 'bukti_bayar_005.pdf', 'WAITING'),
(6, 1950000, 'bukti_bayar_006.pdf', 'WAITING'),
-- Pembayaran ditolak
(7, 600000, 'bukti_bayar_007.pdf', 'REJECTED');

-- =====================================================
-- Verifikasi data yang sudah diinsert
-- =====================================================
