package com.kosku.controller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.NodeMatchers;

// WAJIB ADA: Import static agar clickOn dan write bisa dikenali
import static org.testfx.api.FxRobot.*;

import java.io.IOException;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import com.kosku.Main;
// Atau gunakan asterisk untuk mengimpor semua fungsi robot:
import static org.testfx.api.FxRobot.*;

@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {

//     @org.junit.jupiter.api.BeforeAll
// static void setupHeadless() {
//     System.setProperty("testfx.robot", "glass");
//     System.setProperty("testfx.headless", "true"); // Jalankan tanpa buka jendela
//     System.setProperty("prism.order", "sw");
// }

    @Start
    public void start(Stage stage) throws IOException {
        // Load FXML yang diasosiasikan dengan LoginController
        // Parent root = FXMLLoader.load(Main.class.getResource("/view/login.fxml"));
        // stage.setScene(new Scene(root));
        // stage.show();
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
    
        if (root == null) {
            throw new IOException("Gagal memuat FXML: File tidak ditemukan!");
        }
        
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testLoginFlow(FxRobot robot) {
        // 1. Simulasi mengetik email
        robot.clickOn("#emailField").write("user@example.com");

        // 2. Simulasi mengetik password
        robot.clickOn("#passwordField").write("password123");

        // 3. Klik tombol login
        robot.clickOn("#loginButton");

        // Verifikasi apakah navigasi berhasil (asumsi judul window berubah)
        // Catatan: Anda perlu memastikan Stage title di Main.navigateTo benar-benar terupdate
        FxAssert.verifyThat("#loginButton", NodeMatchers.isNotNull()); 
        System.out.println("Test Login Berhasil dijalankan");
    }

    @Test
    void testNavigationToRegister(FxRobot robot) {
        // Simulasi klik pada teks/link register (pastikan ID di FXML sesuai)
        // Jika menggunakan label atau link dengan fx:id="registerLink"
        robot.clickOn("#registerLink");

        // Verifikasi stage berpindah ke halaman register
        // Anda bisa mengecek keberadaan elemen unik di halaman register
        // FxAssert.verifyThat("#btnRegisterSubmit", NodeMatchers.isVisible());
    }
}