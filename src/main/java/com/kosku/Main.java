package com.kosku;

import com.kosku.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main - Entry point utama aplikasi KosKu.
 */
public class Main extends Application {

    // DIUBAH: Menjadi public agar bisa diakses oleh class Testing untuk mencegah NullPointerException
    public static Stage primaryStage; 

    @Override
    public void start(Stage stage) {
        primaryStage = stage; // Memastikan referensi stage disimpan
        primaryStage.setTitle("KosKu - Aplikasi Pencarian Kos");

        try {
            if (HibernateUtil.getSessionFactory() != null) {
                System.out.println("Database Connected Successfully!");
            } else {
                System.err.println("Database connection failed during initialization.");
            }
        } catch (Throwable e) {
            System.err.println("Critical error connecting to database: " + e.getMessage());
        }

        navigateTo("view/auth/login.fxml", "KosKu - Login");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");

        primaryStage.show();
    }

    public static void navigateTo(String fxmlPath, String title) {
        try {
            String finalPath = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;

            java.net.URL fxmlLocation = Main.class.getResource(finalPath);
            if (fxmlLocation == null) {
                throw new IllegalStateException("File FXML tidak ditemukan di folder resources: " + finalPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();

            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            String cssPath = "/css/style.css";
            var cssResource = Main.class.getResource(cssPath);
            if (cssResource != null) {
                scene.getStylesheets().clear(); 
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setTitle(title);

        } catch (IOException e) {
            System.err.println("Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void navigateTo(String fxmlPath) {
        navigateTo(fxmlPath, primaryStage.getTitle());
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}