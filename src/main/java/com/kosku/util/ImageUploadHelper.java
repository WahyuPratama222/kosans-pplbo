package com.kosku.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ImageUploadHelper {

    /**
     * Membuka dialog FileChooser untuk memilih gambar
     * 
     * @param stage Stage utama untuk parent dialog
     * @return File yang dipilih, atau null jika dibatalkan
     */
    public static File chooseImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Properti");
        
        // Filter format gambar
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Contoh penggunaan di Controller:
     * 
     * File selectedFile = ImageUploadHelper.chooseImage(Main.getPrimaryStage());
     * if (selectedFile != null) {
     *     String savedPath = FileUtil.saveImage(selectedFile, "kos");
     *     currentKos.setGambarKos(savedPath);
     * }
     */
}