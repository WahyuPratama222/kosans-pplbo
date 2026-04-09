package com.kosku;

import com.kosku.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n~~~ Sistem Manajemen Kos (KosKu) ~~~\n");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Status: Terhubung ke Database MySQL");
                System.out.println("Database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("Status: Gagal Terhubung!");
            System.err.println("Error: " + e.getMessage());
            
            System.out.println("\nFix: Pastikan MySQL di XAMPP sudah ON dan database sudah dibuat.");
            System.out.println("\nFix: Pastikan Database di .env sudah dibuat manual di MySQL.");
        }
    }
}