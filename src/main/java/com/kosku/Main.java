package com.kosku;

import com.kosku.util.HibernateUtil;
import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Database Connected Successfully!");
            // Lanjut koding logika Kosku di sini...
        }
    }
}