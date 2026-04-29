package com.kosku.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            Dotenv dotenv = Dotenv.load();
            Configuration configuration = new Configuration().configure(); // Baca XML dulu


            String dbUrl = dotenv.get("DB_URL");
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");

            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);

            configuration.addAnnotatedClass(com.kosku.model.User.class);
            configuration.addAnnotatedClass(com.kosku.model.Kos.class);
            configuration.addAnnotatedClass(com.kosku.model.Kamar.class);
            configuration.addAnnotatedClass(com.kosku.model.Booking.class);
            configuration.addAnnotatedClass(com.kosku.model.Pembayaran.class);

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