# Walkthrough: Lapisan Backend & Frontend Admin

Pekerjaan penambahan fitur Admin secara komprehensif telah selesai dilakukan. Berikut ini adalah rangkuman perubahan dan implementasi yang baru saja diselesaikan.

## 1. Data Access Objects (DAOs)
Semua method yang diperlukan untuk fitur verifikasi dan dashboard telah ditambahkan ke masing-masing DAO:
- **`UserDAO`**: Penambahan `getUnverifiedUsers()` untuk menarik calon pemilik kos, dan `getTotalUsers()` untuk dashboard.
- **`KosDAO`**: Penambahan `getUnverifiedKos()` dan `getTotalVerifiedKos()`.
- **`PembayaranDAO`**: Penambahan `getPembayaranWaiting()` untuk daftar verifikasi bayar, dan `getTotalPembayaranBulanan()` untuk menghitung sum seluruh transaksi *VERIFIED*.
- **`BookingDAO`**: Penambahan `getTotalActiveBookings()`.

## 2. Navigasi & Dashboard
- **`DashboardAdminController.java`**: Menghubungkan semua tombol di *sidebar* untuk me-load halaman FXML lainnya secara penuh. Juga memiliki fitur *logout* yang secara aman membersihkan `SessionManager`.
- **`DashboardAdmin.fxml`**: Dimodifikasi untuk mengaktifkan controller, menambahkan ID untuk komponen, dan menggunakan `<fx:include source="MainMenuAdmin.fxml" />` pada bagian `center` agar bertindak sebagai layar awal yang memuat dashboard statistik secara dinamis.
- **`MainMenuAdminController.java` & `MainMenuAdmin.fxml`**: Berisi UI metrik utama. Controller ini menarik data secara riil dari database (di dalam background thread untuk menjaga UI tetap responsif) dan menampilkannya di layar Admin.

## 3. Fitur Verifikasi Pengguna Baru (Pemilik)
- Telah dibuat halaman `VerifikasiPengguna.fxml` yang menampilkan daftar pendaftar dengan `role = 'PEMILIK'` dan `is_verified = false`.
- Controller menempatkan tombol **Setujui** dan **Tolak** di setiap baris. Klik Setujui akan mengubah status ke diverifikasi, dan Tolak akan menghapus data registrasi pendaftar tersebut.

## 4. Fitur Verifikasi Kos Baru
- Telah dibuat `VerifikasiKos.fxml` dan controllernya. Serupa dengan verifikasi pengguna, halaman ini hanya menarik kos yang belum terverifikasi, memungkinkan Admin untuk memeriksa nama, tipe, dan harganya, sebelum akhirnya memberi persetujuan.

## 5. Laporan Pembayaran
- `LaporanPembayaran.fxml` dimodifikasi untuk memiliki kolom *Aksi* yang dipasangkan dengan `LaporanPembayaranAdminController.java`.
- Memungkinkan admin melihat semua transaksi, dan untuk transaksi berstatus `WAITING`, ada tombol **Verifikasi** dan **Tolak**.

---
> [!TIP]
> **Pengujian**
> Silakan jalankan `Launcher.java` atau `Main.java` kemudian login menggunakan akun Admin. Anda dapat mengklik seluruh menu di sebelah kiri untuk melihat tabel-tabel verifikasi, serta nilai rupiah/pengguna secara langsung yang terhubung ke dalam database MariaDB.
