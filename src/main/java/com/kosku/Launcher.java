package com.kosku;

/**
 * Launcher - Class pembantu untuk menjalankan aplikasi dari IDE.
 *
 * Kenapa perlu class ini?
 * Sejak Java 11+, JavaFX tidak lagi dibundel bersama JDK.
 * Jika kita langsung menjalankan class yang extends Application,
 * JVM akan memvalidasi modul JavaFX dan gagal dengan error:
 * "JavaFX runtime components are missing"
 *
 * Class ini TIDAK extends Application, sehingga JVM tidak melakukan
 * pengecekan modul JavaFX di awal, dan JavaFX akan dimuat dari
 * dependency Maven secara normal.
 *
 * Cara pakai:
 *   - Klik kanan file ini di IDE → Run 'Launcher.main()'
 *   - Atau jalankan via terminal: mvn javafx:run
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
