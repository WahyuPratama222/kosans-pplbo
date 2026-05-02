package com.kosku.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtil {

    private static final String UPLOAD_DIR = "uploads";

    static {
        // Pastikan folder uploads ada saat class dimuat
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Buat sub-folder untuk kerapihan
        new File(UPLOAD_DIR + "/kos").mkdirs();
        new File(UPLOAD_DIR + "/kamar").mkdirs();
        new File(UPLOAD_DIR + "/pembayaran").mkdirs();
    }

    /**
     * Menyimpan file gambar ke folder uploads dengan nama unik.
     * 
     * @param sourceFile File asal (dari FileChooser JavaFX)
     * @param category Sub-folder (kos, kamar, atau pembayaran)
     * @return Nama file baru yang disimpan (atau path relatif)
     * @throws IOException Jika terjadi kesalahan saat menyalin file
     */
    public static String saveImage(File sourceFile, String category) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        String extension = getFileExtension(sourceFile.getName());
        String fileName = UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(UPLOAD_DIR, category, fileName);

        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return category + "/" + fileName;
    }

    /**
     * Menghapus file gambar jika sudah tidak digunakan
     * 
     * @param relativePath Path relatif yang disimpan di database
     */
    public static void deleteImage(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return;
        
        try {
            Path path = Paths.get(UPLOAD_DIR, relativePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Gagal menghapus file: " + relativePath);
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return ".png"; // Default
    }
    
    public static String getAbsolutePath(String relativePath) {
        if (relativePath == null) return null;
        return new File(UPLOAD_DIR + "/" + relativePath).getAbsolutePath();
    }
}