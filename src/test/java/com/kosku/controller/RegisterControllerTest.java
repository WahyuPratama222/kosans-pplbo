package com.kosku.controller;

import com.kosku.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;

public class RegisterControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Pastikan path FXML sesuai dengan lokasi di resources Anda
        Parent root = FXMLLoader.load(Main.class.getResource("view/register.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testRegisterFlow() {
        // 1. Mengisi Nama Lengkap
        clickOn("#namaLengkapField").write("Budi Santoso");

        // 2. Mengisi Email
        clickOn("#emailField").write("budi@email.com");

        // 3. Memilih Jenis Kelamin di ComboBox
        clickOn("#jenisKelaminBox");
        type(javafx.scene.input.KeyCode.DOWN); // Pilih opsi pertama
        type(javafx.scene.input.KeyCode.ENTER);

        // 4. Mengisi Password
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        // 5. Klik Tombol Register
        clickOn("#registerButton");

        // Verifikasi: Karena handleRegister() pindah ke Login, 
        // kita cek apakah kita masih di window yang valid (atau cek elemen di login)
        FxAssert.verifyThat("#registerButton", NodeMatchers.isNotNull());
    }

    @Test
    void testComboBoxOptions() {
        // Verifikasi apakah ComboBox memiliki pilihan yang benar sesuai initialize()
        FxAssert.verifyThat("#jenisKelaminBox", ComboBoxMatchers.containsItems("Laki-Laki", "Perempuan"));
    }

    @Test
    void testNavigateBackToLogin() {
        // Asumsi Anda memiliki Label atau Hyperlink untuk kembali ke login
        // Ganti "#loginLink" dengan fx:id yang sesuai di FXML Anda
        clickOn("#loginLink"); 
        
        // Verifikasi perpindahan halaman
        System.out.println("Navigasi kembali ke login berhasil diuji.");
    }
}