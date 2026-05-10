package com.kosku.service;

import com.kosku.dao.NotifikasiDAO;
import com.kosku.model.Booking;
import com.kosku.model.Notifikasi;
import com.kosku.model.Notifikasi.TipeNotifikasi;
import com.kosku.model.User;

/**
 * Service untuk membuat notifikasi sistem secara terpusat.
 * Dipanggil saat terjadi event penting (booking diterima/ditolak, pembayaran, dll).
 */
public class NotifikasiService {

    private static final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();

    // =============================================
    // NOTIFIKASI BOOKING
    // =============================================

    /**
     * Kirim notifikasi ke penyewa bahwa booking-nya DITERIMA oleh pemilik.
     *
     * @param booking Booking yang diterima
     */
    public static void kirimNotifBookingDiterima(Booking booking) {
        try {
            User penyewa = booking.getPenyewa();
            if (penyewa == null) return;

            String namaKos  = getKosName(booking);
            String noKamar  = getKamarNo(booking);
            String tglMulai = booking.getTanggalMulai() != null ? booking.getTanggalMulai().toString() : "-";

            Notifikasi notif = Notifikasi.builder()
                    .user(penyewa)
                    .judul("✅ Booking Diterima!")
                    .isi("Selamat! Booking Anda untuk kos \"" + namaKos +
                            "\" (Kamar " + noKamar + ") telah DITERIMA oleh pemilik. " +
                            "Tanggal mulai: " + tglMulai + ". " +
                            "Silakan lakukan pembayaran sesuai petunjuk.")
                    .tipe(TipeNotifikasi.BOOKING)
                    .booking(booking)
                    .kos(booking.getKamar() != null ? booking.getKamar().getKos() : null)
                    .sudahDibaca(false)
                    .build();

            notifikasiDAO.createNotification(notif);
            System.out.println("[NotifikasiService] Notif booking DITERIMA dikirim ke user ID: " + penyewa.getIdUser());
        } catch (Exception e) {
            System.err.println("[NotifikasiService] Gagal kirim notif booking diterima: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kirim notifikasi ke penyewa bahwa booking-nya DITOLAK oleh pemilik.
     *
     * @param booking Booking yang ditolak
     * @param alasan  Alasan penolakan yang dimasukkan pemilik
     */
    public static void kirimNotifBookingDitolak(Booking booking, String alasan) {
        try {
            User penyewa = booking.getPenyewa();
            if (penyewa == null) return;

            String namaKos = getKosName(booking);
            String noKamar = getKamarNo(booking);

            Notifikasi notif = Notifikasi.builder()
                    .user(penyewa)
                    .judul("❌ Booking Ditolak")
                    .isi("Maaf, booking Anda untuk kos \"" + namaKos +
                            "\" (Kamar " + noKamar + ") DITOLAK oleh pemilik. " +
                            "Alasan: " + alasan +
                            ". Silakan pilih kos lain yang tersedia.")
                    .tipe(TipeNotifikasi.BOOKING)
                    .booking(booking)
                    .kos(booking.getKamar() != null ? booking.getKamar().getKos() : null)
                    .sudahDibaca(false)
                    .build();

            notifikasiDAO.createNotification(notif);
            System.out.println("[NotifikasiService] Notif booking DITOLAK dikirim ke user ID: " + penyewa.getIdUser());
        } catch (Exception e) {
            System.err.println("[NotifikasiService] Gagal kirim notif booking ditolak: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kirim notifikasi ke penyewa bahwa pembayaran sudah diverifikasi.
     *
     * @param booking Booking terkait
     */
    public static void kirimNotifPembayaranVerified(Booking booking) {
        try {
            User penyewa = booking.getPenyewa();
            if (penyewa == null) return;

            String namaKos = getKosName(booking);

            Notifikasi notif = Notifikasi.builder()
                    .user(penyewa)
                    .judul("💰 Pembayaran Terverifikasi")
                    .isi("Pembayaran Anda untuk kos \"" + namaKos +
                            "\" telah terverifikasi. Selamat tinggal di kos kami!")
                    .tipe(TipeNotifikasi.PEMBAYARAN)
                    .booking(booking)
                    .kos(booking.getKamar() != null ? booking.getKamar().getKos() : null)
                    .sudahDibaca(false)
                    .build();

            notifikasiDAO.createNotification(notif);
            System.out.println("[NotifikasiService] Notif pembayaran verified dikirim ke user ID: " + penyewa.getIdUser());
        } catch (Exception e) {
            System.err.println("[NotifikasiService] Gagal kirim notif pembayaran: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =============================================
    // HELPER
    // =============================================

    private static String getKosName(Booking booking) {
        try {
            if (booking.getKamar() != null && booking.getKamar().getKos() != null) {
                return booking.getKamar().getKos().getNamaKos();
            }
        } catch (Exception ignored) {}
        return "Tidak diketahui";
    }

    private static String getKamarNo(Booking booking) {
        try {
            if (booking.getKamar() != null) {
                return booking.getKamar().getNomorKamar();
            }
        } catch (Exception ignored) {}
        return "-";
    }
}
