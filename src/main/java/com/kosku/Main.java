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

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("KosKu - Aplikasi Pencarian Kos");

        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Database Connected Successfully!");
        } catch (Throwable e) {
            System.err.println("Database belum tersedia...");
        }

        navigateTo("view/login.fxml", "KosKu - Login");
        primaryStage.setFullScreen(true); 
        // Opsional: Biar nggak muncul tulisan "Press ESC to exit full screen"
        primaryStage.setFullScreenExitHint(""); 
        
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

            // Ambil scene yang sudah ada di stage
            Scene scene = primaryStage.getScene();

            if (scene == null) {
                // Kalau pertama kali jalan (saat start), baru buat scene baru
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                // Kalau sudah ada scene, cukup ganti root-nya saja (GAK ADA JEDA)
                scene.setRoot(root);
            }

            // Muat CSS (Hanya perlu sekali, tapi kalau mau aman tiap ganti root gak apa-apa)
            String cssPath = "/css/style.css";
            var cssResource = Main.class.getResource(cssPath);
            if (cssResource != null) {
                scene.getStylesheets().clear(); // Bersihkan biar gak numpuk
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setTitle(title);
            // Hapus primaryStage.show() atau maximized di sini kalau sudah diatur di start()

        } catch (IOException e) {
            System.err.println("Gagal memuat halaman: " + fxmlPath);
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