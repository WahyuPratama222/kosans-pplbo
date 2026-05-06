package com.kosku.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            // Cari .env dari direktori project (bukan classpath)
            // Coba beberapa lokasi umum agar kompatibel saat run dari IDE maupun terminal
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir"))  // direktori saat mvn/IDE dijalankan
                    .filename(".env")
                    .ignoreIfMissing()  // tidak crash jika .env tidak ditemukan
                    .load();

            if (dotenv.get("DB_URL") == null) {
                System.out.println("[HibernateUtil] WARNING: .env tidak ditemukan atau kosong di: " + System.getProperty("user.dir"));
            } else {
                System.out.println("[HibernateUtil] Berhasil memuat .env dari: " + System.getProperty("user.dir"));
            }
            String dbUrl      = dotenv.get("DB_URL",      "jdbc:mysql://localhost:3306/kosans_db");
            String dbUser     = dotenv.get("DB_USER",     "root");
            // Hapus komentar inline jika ada (contoh: "password123 # ini komentar")
            String dbPassword = dotenv.get("DB_PASSWORD", "041206").split("#")[0].trim();

            System.out.println("[HibernateUtil] Connecting to: " + dbUrl + " (user: " + dbUser + ")");

            Configuration configuration = new Configuration().configure(); // Baca hibernate.cfg.xml

            configuration.setProperty("hibernate.connection.url",      dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);

            // Daftarkan semua entity class
            configuration.addAnnotatedClass(com.kosku.model.User.class);
            configuration.addAnnotatedClass(com.kosku.model.Kos.class);
            configuration.addAnnotatedClass(com.kosku.model.Kamar.class);
            configuration.addAnnotatedClass(com.kosku.model.Booking.class);
            configuration.addAnnotatedClass(com.kosku.model.Pembayaran.class);
            configuration.addAnnotatedClass(com.kosku.model.Review.class);   // FIX: sebelumnya terlewat!
            configuration.addAnnotatedClass(com.kosku.model.Chat.class);
            configuration.addAnnotatedClass(com.kosku.model.Notifikasi.class);

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Database connection failed!");
            System.err.println("Error Message: " + ex.getMessage());
            System.err.println("Tip: Pastikan MySQL berjalan dan database sudah dibuat.");
            return null;
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static boolean isConnected() {
        return sessionFactory != null;
    }
}