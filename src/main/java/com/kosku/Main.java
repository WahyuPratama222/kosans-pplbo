package com.kosku;

import com.kosku.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main - Entry point utama aplikasi KosKu.
 *
 * Class ini berfungsi sebagai pusat untuk menjalankan seluruh page (FXML).
 * Gunakan method navigateTo() dari controller mana saja untuk berpindah halaman.
 *
 * Daftar FXML yang tersedia:
 *   - view/login.fxml              → Halaman Login
 *   - view/register.fxml           → Halaman Register
 *   - view/penyewa/MainMenuPenyewa.fxml  → Dashboard Penyewa
 *   - view/penyewa/ChatPenyewa.fxml      → Chat Penyewa
 *   - view/penyewa/NotifPenyewa.fxml     → Notifikasi Penyewa
 *   - view/penyewa/RiwayatPenyewa.fxml   → Riwayat Penyewa
 */
public class Main extends Application {

    private static Stage primaryStage;

    // Ukuran default window
    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("KosKu - Aplikasi Pencarian Kos");

        // Inisialisasi koneksi database (opsional, app tetap jalan tanpa DB)
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("✅ Database Connected Successfully!");
        } catch (Throwable e) {
            System.err.println("⚠️ Database belum tersedia - aplikasi berjalan tanpa database.");
            System.err.println("   Untuk mengaktifkan database, jalankan: docker compose up -d");
        }

        // Muat halaman pertama: Login
        navigateTo("view/login.fxml", "KosKu - Login");

        primaryStage.show();
    }

    /**
     * Navigasi ke halaman FXML tertentu.
     *
     * Contoh penggunaan di Controller:
     *   Main.navigateTo("view/register.fxml", "KosKu - Register");
     *   Main.navigateTo("view/penyewa/MainMenuPenyewa.fxml", "KosKu - Dashboard");
     *
     * @param fxmlPath path relatif dari folder resources (contoh: "view/login.fxml")
     * @param title    judul window yang akan ditampilkan
     */
    public static void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Muat stylesheet jika ada
            String cssPath = Main.class.getResource("/css/style.css") != null
                    ? Main.class.getResource("/css/style.css").toExternalForm()
                    : null;
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            System.err.println("❌ Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Navigasi ke halaman FXML (tanpa mengubah judul window).
     *
     * @param fxmlPath path relatif dari folder resources
     */
    public static void navigateTo(String fxmlPath) {
        navigateTo(fxmlPath, primaryStage.getTitle());
    }

    /**
     * Mendapatkan referensi ke primary stage.
     * Berguna jika controller perlu akses langsung ke window (misal: resize, close).
     *
     * @return Stage utama aplikasi
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Entry point aplikasi.
     */
    public static void main(String[] args) {
        launch(args);
    }
}