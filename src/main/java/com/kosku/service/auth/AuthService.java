package com.kosku.service.auth;

import com.kosku.dao.UserDAO;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String identifier, String password) throws Exception {
        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            throw new Exception("Username/Email dan Password tidak boleh kosong.");
        }

        // Pakai trim agar spasi tidak sengaja bikin gagal login
        User user = userDAO.findByIdentifier(identifier.trim());
        
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            // Tips: Pesan error dibuat ambigu agar hacker tidak tahu 
            // apakah yang salah itu emailnya atau passwordnya (Security Best Practice)
            throw new Exception("Username/Email atau Password salah.");
        }

        SessionManager.login(user);
        return user;
    }

    public void register(User user) throws Exception {
        // Normalisasi data sebelum masuk DB
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());

        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new Exception("Username '" + user.getUsername() + "' sudah digunakan.");
        }

        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new Exception("Email '" + user.getEmail() + "' sudah terdaftar.");
        }

        String hashedPw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPw);

        try {
            userDAO.saveOrUpdate(user);
        } catch (Exception e) {
            throw new Exception("Gagal menyimpan akun: " + e.getMessage());
        }
    }

    public void logout() {
        SessionManager.logout();
    }
}