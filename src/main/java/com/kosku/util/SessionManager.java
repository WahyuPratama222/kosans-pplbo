package com.kosku.util;

import com.kosku.model.User;

/**
 * Session Manager untuk menyimpan data user yang sedang login
 * Digunakan untuk mengakses informasi user di seluruh aplikasi
 */
public class SessionManager {
    
    private static Integer currentUserId = null;
    private static String currentUsername = null;
    private static User.Role currentRole = null;
    private static User currentUser = null;

    /**
     * Set user yang sedang login
     */
    public static void login(User user) {
        if (user != null) {
            currentUserId = user.getIdUser();
            currentUsername = user.getUsername();
            currentRole = user.getRole();
            currentUser = user;
            System.out.println("User login: " + currentUsername + " (ID: " + currentUserId + ")");
        }
    }

    /**
     * Get ID user saat ini
     */
    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Get username user saat ini
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Get role user saat ini
     */
    public static User.Role getCurrentRole() {
        return currentRole;
    }

    /**
     * Get user object saat ini
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check apakah user sudah login
     */
    public static boolean isLoggedIn() {
        return currentUserId != null && currentUsername != null;
    }

    /**
     * Check apakah user adalah PENYEWA
     */
    public static boolean isPenyewa() {
        return currentRole == User.Role.PENYEWA;
    }

    /**
     * Check apakah user adalah PEMILIK
     */
    public static boolean isPemilik() {
        return currentRole == User.Role.PEMILIK;
    }

    /**
     * Check apakah user adalah ADMIN
     */
    public static boolean isAdmin() {
        return currentRole == User.Role.ADMIN;
    }

    /**
     * Logout - clear session
     */
    public static void logout() {
        System.out.println("User logout: " + currentUsername);
        currentUserId = null;
        currentUsername = null;
        currentRole = null;
        currentUser = null;
    }

    /**
     * Clear session (biasa digunakan di testing)
     */
    public static void clearSession() {
        currentUserId = null;
        currentUsername = null;
        currentRole = null;
        currentUser = null;
    }

    /**
     * Get current session info untuk logging/debugging
     */
    public static String getSessionInfo() {
        if (isLoggedIn()) {
            return String.format("Session: User[ID=%d, Username=%s, Role=%s]", 
                currentUserId, currentUsername, currentRole);
        }
        return "Session: Not logged in";
    }
}
