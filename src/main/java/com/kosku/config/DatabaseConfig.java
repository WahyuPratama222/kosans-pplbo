package com.kosku.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException {
        // Baca .env dari direktori project (bukan classpath)
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();

        String url = dotenv.get("DB_URL", "jdbc:mysql://localhost:3306/kosans_db");
        String user = dotenv.get("DB_USER", "root");
        String password = dotenv.get("DB_PASSWORD", "041206").split("#")[0].trim();

        return DriverManager.getConnection(url, user, password);
    }
}
