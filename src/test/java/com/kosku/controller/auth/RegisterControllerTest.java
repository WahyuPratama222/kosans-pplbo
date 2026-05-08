package com.kosku.controller.auth;

import com.kosku.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.sql.Time;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;

public class RegisterControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // TAMBAHKAN BARIS INI:
        com.kosku.Main.primaryStage = stage;

        Parent root = FXMLLoader.load(Main.class.getResource("/view/auth/register.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testRegisterFlow() {
        // 1. Mengisi Nama Lengkap
        clickOn("#usernameField").write("Arkan Syah " + String.valueOf(System.currentTimeMillis()));

        // 2. Mengisi Email
        clickOn("#emailField").write("arkn"+String.valueOf(System.currentTimeMillis())+"@email.com");

        // 3. Memilih Jenis Kelamin di ComboBox
        clickOn("#nomorHpField").write(String.valueOf(System.currentTimeMillis()));

        // 4. Mengisi Password
        clickOn("#passwordField").write("password123");

        clickOn("#confirmPasswordField").write("password123");

        // 5. Klik// PAKSA SCROLL KE BAWAH
        interact(() -> {
            // Cari ScrollPane yang ada di FXML tadi (menggunakan styleClass 'auth-scroll')
            ScrollPane scrollPane = lookup(".auth-scroll").queryAs(ScrollPane.class);
            if (scrollPane != null) {
                scrollPane.setVvalue(1.0); // 1.0 berarti geser ke paling bawah
            }
        });

        // Tunggu sebentar agar animasi scroll selesai (opsional, tapi disarankan)
        sleep(500);

        // SEKARANG KLIK
        clickOn("#registerButton");

        // Verifikasi: Karena handleRegister() pindah ke Login,
        // kita cek apakah kita masih di window yang valid (atau cek elemen di login)
        // FxAssert.verifyThat("#registerButton", NodeMatchers.isNotNull());

        // 3. VERIFIKASI (Gunakan teks yang lebih umum dulu untuk debugging)
        // Cek apakah ada window Alert yang muncul
        // FxAssert.verifyThat(".dialog-pane", NodeMatchers.isVisible());

        // 5. Cek Pesan Detail
        FxAssert.verifyThat("Akun Pemilik berhasil dibuat.", NodeMatchers.isVisible());

        // clickOn("OK");

        System.out.println("Test Login Berhasil dijalankan");
    }

    // @Test
    // void testNavigateBackToLogin() {
    // // Asumsi Anda memiliki Label atau Hyperlink untuk kembali ke login
    // // Ganti "#loginLink" dengan fx:id yang sesuai di FXML Anda
    // clickOn("#loginLink");

    // // Verifikasi perpindahan halaman
    // System.out.println("Navigasi kembali ke login berhasil diuji.");
    // }
}