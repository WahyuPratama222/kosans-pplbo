package com.kosku.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            Dotenv dotenv = Dotenv.load();

            String dbUrl = dotenv.get("DB_URL", "jdbc:mysql://localhost:3306/db_kosku");
            String dbUser = dotenv.get("DB_USER", "root");
            String dbPassword = dotenv.get("DB_PASSWORD", "");

            Configuration configuration = new Configuration();
            
            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);
            
            configuration.configure(); 

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Database connection failed!");
            System.err.println("Error Message: " + ex.getMessage());
            System.err.println("Tip: Make sure Docker/MySQL is running and the database is created.");
            return null;
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static boolean isConnected() {
        return sessionFactory != null;
    }
}