package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Profil Penyewa
 * Menampilkan dan mengelola informasi profil pengguna penyewa
 */
public class ProfilController implements Initializable {

    @FXML
    private TextField tfNama;
    
    @FXML
    private TextField tfEmail;
    
    @FXML
    private TextField tfNoHp;
    
    @FXML
    private PasswordField pfPasswordLama;
    
    @FXML
    private PasswordField pfPasswordBaru;
    
    @FXML
    private PasswordField pfKonfirmasiPassword;

    @FXML
    private NavbarController navbarController;

    private UserDAO userDAO;
    private User userCurrent;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Profil Penyewa berhasil dimuat!");
        
        userDAO = new UserDAO();
        
        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("profil");
        }
        
        // Load user data
        loadUserData();
        
        // Set initial state
        setEditMode(false);
    }

    private void loadUserData() {
        try {
            // Dapatkan ID penyewa dari SessionManager
            Integer idPenyewa = SessionManager.getCurrentUserId();
            
            if (idPenyewa == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Anda harus login terlebih dahulu");
                return;
            }
            
            userCurrent = userDAO.getById(User.class, idPenyewa);
            
            if (userCurrent != null) {
                tfNama.setText(userCurrent.getUsername() != null ? userCurrent.getUsername() : "");
                tfEmail.setText(userCurrent.getEmail() != null ? userCurrent.getEmail() : "");
                tfNoHp.setText(userCurrent.getNomorHp() != null ? userCurrent.getNomorHp() : "");
            } else {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Data pengguna tidak ditemukan");
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal memuat data profil: " + e.getMessage());
        }
    }

    @FXML
    void toggleEditMode(ActionEvent event) {
        setEditMode(!isEditMode);
    }

    private void setEditMode(boolean edit) {
        isEditMode = edit;
        tfNama.setEditable(edit);
        tfEmail.setEditable(edit);
        tfNoHp.setEditable(edit);
        
        pfPasswordLama.setDisable(!edit);
        pfPasswordBaru.setDisable(!edit);
        pfKonfirmasiPassword.setDisable(!edit);
    }

    @FXML
    void simpanProfil(ActionEvent event) {
        if (!isEditMode) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", 
                "Klik tombol Edit terlebih dahulu");
            return;
        }
        try {
            // Validasi input
            if (tfNama.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", 
                    "Nama tidak boleh kosong");
                return;
            }
            if (tfEmail.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", 
                    "Email tidak boleh kosong");
                return;
            }
            // Update user data
            userCurrent.setUsername(tfNama.getText());
            userCurrent.setEmail(tfEmail.getText());
            userCurrent.setNomorHp(tfNoHp.getText());
            // Update password jika ada
            if (!pfPasswordBaru.getText().isEmpty()) {
                // Validasi password lama
                if (pfPasswordLama.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", 
                        "Masukkan password lama terlebih dahulu");
                    return;
                }
                // Validasi password baru dan konfirmasi
                if (!pfPasswordBaru.getText().equals(pfKonfirmasiPassword.getText())) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", 
                        "Password baru dan konfirmasi tidak cocok");
                    return;
                }
                // Validasi password lama dari database
                boolean valid = org.mindrot.jbcrypt.BCrypt.checkpw(pfPasswordLama.getText(), userCurrent.getPassword());
                if (!valid) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Password lama salah!");
                    return;
                }
                userCurrent.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(pfPasswordBaru.getText(), org.mindrot.jbcrypt.BCrypt.gensalt()));
            }
            // Simpan ke database
            userDAO.saveOrUpdate(userCurrent);
            // Reset form
            pfPasswordLama.clear();
            pfPasswordBaru.clear();
            pfKonfirmasiPassword.clear();
            setEditMode(false);
            showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                "Profil berhasil diperbarui!");
        } catch (Exception e) {
            System.err.println("Error simpan profil: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal menyimpan profil: " + e.getMessage());
        }
    }

    @FXML
    void batal(ActionEvent event) {
        if (isEditMode) {
            // Reload data sebelumnya
            loadUserData();
            setEditMode(false);
            pfPasswordLama.clear();
            pfPasswordBaru.clear();
            pfKonfirmasiPassword.clear();
        }
    }

    @FXML
    void ubahPassword(ActionEvent event) {
        if (pfPasswordLama.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", 
                "Masukkan password lama");
            return;
        }
        if (pfPasswordBaru.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", 
                "Masukkan password baru");
            return;
        }
        if (!pfPasswordBaru.getText().equals(pfKonfirmasiPassword.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validasi", 
                "Password baru dan konfirmasi tidak cocok");
            return;
        }
        // Validasi panjang password
        if (pfPasswordBaru.getText().length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validasi", 
                "Password minimal 6 karakter");
            return;
        }
        try {
            // Validasi password lama dari database
            Integer userId = SessionManager.getCurrentUserId();
            User user = userDAO.getById(User.class, userId);
            boolean valid = org.mindrot.jbcrypt.BCrypt.checkpw(pfPasswordLama.getText(), user.getPassword());
            if (!valid) {
                showAlert(Alert.AlertType.ERROR, "Error", "Password lama salah!");
                return;
            }
            // Update password
            user.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(pfPasswordBaru.getText(), org.mindrot.jbcrypt.BCrypt.gensalt()));
            userDAO.saveOrUpdate(user);
            showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                "Password berhasil diubah!");
            pfPasswordLama.clear();
            pfPasswordBaru.clear();
            pfKonfirmasiPassword.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal mengubah password: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
