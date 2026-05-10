package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import com.kosku.util.PopupManager;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Profil Penyewa
 * Menampilkan dan mengelola informasi profil: nama, email, no HP, dan password.
 */
public class ProfilController implements Initializable {

    @FXML private TextField tfNama;
    @FXML private TextField tfEmail;
    @FXML private TextField tfNoHp;

    @FXML private PasswordField pfPasswordLama;
    @FXML private PasswordField pfPasswordBaru;
    @FXML private PasswordField pfKonfirmasiPassword;

    @FXML private Label lblNamaProfil;
    @FXML private Label lblEmailProfil;

    @FXML private NavbarController navbarController;

    private UserDAO userDAO;
    private User userCurrent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();

        if (navbarController != null) {
            navbarController.setHighlight("profil");
        }

        loadUserData();
    }

    // =============================================
    // LOAD DATA
    // =============================================

    private void loadUserData() {
        try {
            Integer idPenyewa = SessionManager.getCurrentUserId();
            if (idPenyewa == null) {
                PopupManager.showWarning("Peringatan", "Anda harus login terlebih dahulu.");
                return;
            }

            userCurrent = userDAO.getById(User.class, idPenyewa);

            if (userCurrent != null) {
                String nama  = userCurrent.getUsername() != null ? userCurrent.getUsername() : "";
                String email = userCurrent.getEmail()    != null ? userCurrent.getEmail()    : "";
                String hp    = userCurrent.getNomorHp()  != null ? userCurrent.getNomorHp()  : "";

                tfNama.setText(nama);
                tfEmail.setText(email);
                tfNoHp.setText(hp);

                // Update kartu kiri
                if (lblNamaProfil  != null) lblNamaProfil.setText(nama.isEmpty()  ? "Pengguna" : nama);
                if (lblEmailProfil != null) lblEmailProfil.setText(email.isEmpty() ? "-" : email);
            } else {
                PopupManager.showWarning("Peringatan", "Data pengguna tidak ditemukan.");
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();
            PopupManager.showError("Error", "Gagal memuat data profil: " + e.getMessage());
        }
    }

    // =============================================
    // SIMPAN PROFIL (nama, email, no HP)
    // =============================================

    @FXML
    void simpanProfil(ActionEvent event) {
        String nama  = tfNama.getText().trim();
        String email = tfEmail.getText().trim();
        String hp    = tfNoHp.getText().trim();

        if (nama.isEmpty()) {
            PopupManager.showWarning("Validasi", "Nama lengkap tidak boleh kosong.");
            return;
        }
        if (email.isEmpty()) {
            PopupManager.showWarning("Validasi", "Email tidak boleh kosong.");
            return;
        }

        try {
            userCurrent.setUsername(nama);
            userCurrent.setEmail(email);
            userCurrent.setNomorHp(hp);

            userDAO.saveOrUpdate(userCurrent);

            // Update label kartu kiri secara real-time
            if (lblNamaProfil  != null) lblNamaProfil.setText(nama);
            if (lblEmailProfil != null) lblEmailProfil.setText(email);

            // Perbarui session username jika perlu
            SessionManager.updateCurrentUsername(nama);

            PopupManager.showInfo("Sukses", "Profil berhasil diperbarui!");
        } catch (Exception e) {
            System.err.println("Error simpan profil: " + e.getMessage());
            e.printStackTrace();
            PopupManager.showError("Error", "Gagal menyimpan profil: " + e.getMessage());
        }
    }

    // =============================================
    // BATAL (reload dari DB)
    // =============================================

    @FXML
    void batal(ActionEvent event) {
        loadUserData();
        pfPasswordLama.clear();
        pfPasswordBaru.clear();
        pfKonfirmasiPassword.clear();
    }

    // =============================================
    // UBAH PASSWORD
    // =============================================

    @FXML
    void ubahPassword(ActionEvent event) {
        String lama      = pfPasswordLama.getText();
        String baru      = pfPasswordBaru.getText();
        String konfirmasi = pfKonfirmasiPassword.getText();

        if (lama.isEmpty()) {
            PopupManager.showWarning("Validasi", "Masukkan password lama terlebih dahulu.");
            return;
        }
        if (baru.isEmpty()) {
            PopupManager.showWarning("Validasi", "Masukkan password baru.");
            return;
        }
        if (baru.length() < 6) {
            PopupManager.showWarning("Validasi", "Password baru minimal 6 karakter.");
            return;
        }
        if (!baru.equals(konfirmasi)) {
            PopupManager.showWarning("Validasi", "Password baru dan konfirmasi tidak cocok.");
            return;
        }

        try {
            // Verifikasi password lama
            boolean valid = org.mindrot.jbcrypt.BCrypt.checkpw(lama, userCurrent.getPassword());
            if (!valid) {
                PopupManager.showError("Gagal", "Password lama yang Anda masukkan salah!");
                return;
            }

            // Simpan password baru (hashed)
            userCurrent.setPassword(
                org.mindrot.jbcrypt.BCrypt.hashpw(baru, org.mindrot.jbcrypt.BCrypt.gensalt())
            );
            userDAO.saveOrUpdate(userCurrent);

            pfPasswordLama.clear();
            pfPasswordBaru.clear();
            pfKonfirmasiPassword.clear();

            PopupManager.showInfo("Sukses", "Password berhasil diubah!");
        } catch (Exception e) {
            System.err.println("Error ubah password: " + e.getMessage());
            e.printStackTrace();
            PopupManager.showError("Error", "Gagal mengubah password: " + e.getMessage());
        }
    }
}
