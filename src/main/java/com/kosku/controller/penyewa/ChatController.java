package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.kosku.dao.ChatDAO;
import com.kosku.dao.UserDAO;
import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Chat Penyewa
 * Menampilkan interface untuk chat dengan pemilik kos atau admin
 */
public class ChatController implements Initializable {

    @FXML
    private ComboBox<String> cbPenerima;
    
    @FXML
    private TextArea taChat;
    
    @FXML
    private TextArea tfPesanBaru;
    
    @FXML
    private VBox vboxChat;
    
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private NavbarController navbarController;

    private ObservableList<String> daftarPenerima;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    private ChatDAO chatDAO;
    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Chat Penyewa berhasil dimuat!");
        
        chatDAO = new ChatDAO();
        userDAO = new UserDAO();
        
        // Set highlight navbar
        if (navbarController != null) {
            navbarController.setHighlight("chat");
        }
        
        // Inisialisasi data penerima dari database
        initializePenerima();
        
        // Setup TextArea untuk chat
        taChat.setWrapText(true);
        taChat.setEditable(false);
        
        // Setup TextArea untuk pesan baru
        tfPesanBaru.setWrapText(true);
        
        // Listener untuk perubahan penerima
        cbPenerima.setOnAction(e -> loadChatHistory());
        
        // Load data awal
        if (!daftarPenerima.isEmpty()) {
            cbPenerima.setValue(daftarPenerima.get(0));
            loadChatHistory();
        }
    }

    private void initializePenerima() {
        try {
            Integer userId = SessionManager.getCurrentUserId();
            
            if (userId == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                    "Anda harus login terlebih dahulu");
                return;
            }
            
            daftarPenerima = FXCollections.observableArrayList();
            
            // Ambil daftar chat partner dari database
            List<User> chatPartners = chatDAO.getChatPartners(userId);
            
            if (chatPartners != null && !chatPartners.isEmpty()) {
                for (User user : chatPartners) {
                    if (user.getUsername() != null) {
                        daftarPenerima.add(user.getUsername() + " (" + user.getRole() + ")");
                    }
                }
            }
            
            cbPenerima.setItems(daftarPenerima);
        } catch (Exception e) {
            System.err.println("Error loading penerima: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try {
            Integer userId = SessionManager.getCurrentUserId();
            String penerimaStr = cbPenerima.getValue();
            if (userId == null || penerimaStr == null) {
                return;
            }
            // Extract nama pengguna dari penerimaStr
            String penerimaNama = penerimaStr.split(" \\(")[0];
            // Cari user penerima dari database
            User penerima = userDAO.findByUsername(penerimaNama);
            if (penerima == null) {
                taChat.setText("Penerima tidak ditemukan di database.");
                return;
            }
            taChat.clear();
            // Ambil message history dari database (bisa filter by pengirim & penerima jika perlu)
            List<Chat> messages = chatDAO.getReceivedMessages(userId);
            if (messages != null && !messages.isEmpty()) {
                for (Chat chat : messages) {
                    String sender = chat.getPengirim().getUsername() != null ? 
                        chat.getPengirim().getUsername() : "User";
                    String timestamp = chat.getWaktuPesan().format(timeFormatter);
                    taChat.appendText("[" + timestamp + "] " + sender + ": " + chat.getIsiPesan() + "\n");
                }
            } else {
                taChat.setText("Belum ada pesan. Mulai percakapan sekarang!");
            }
        } catch (Exception e) {
            System.err.println("Error loading chat history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void kirimPesan(ActionEvent event) {
        try {
            String pesan = tfPesanBaru.getText().trim();
            String penerimaNama = cbPenerima.getValue();
            Integer userId = SessionManager.getCurrentUserId();
            if (pesan.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pesan tidak boleh kosong");
                return;
            }
            if (penerimaNama == null || penerimaNama.isEmpty() || userId == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih penerima terlebih dahulu");
                return;
            }
            // Extract username dari penerimaNama (format: "username (role)")
            String penerimaUsername = penerimaNama.split(" \\(")[0];
            // Cari user penerima dari database
            User penerima = userDAO.findByUsername(penerimaUsername);
            if (penerima == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Penerima tidak ditemukan di database");
                return;
            }
            // Create chat object
            Chat chat = Chat.builder()
                .pengirim(User.builder().idUser(userId).build())
                .penerima(User.builder().idUser(penerima.getIdUser()).build())
                .isiPesan(pesan)
                .sudahDibaca(false)
                .build();
            // Simpan ke database
            chatDAO.saveOrUpdate(chat);
            // Tambahkan ke tampilan chat
            String timestamp = LocalTime.now().format(timeFormatter);
            taChat.appendText("[" + timestamp + "] Anda: " + pesan + "\n");
            // Bersihkan input
            tfPesanBaru.clear();
            tfPesanBaru.requestFocus();
            System.out.println("Pesan terkirim ke " + penerimaUsername);
        } catch (Exception e) {
            System.err.println("Error mengirim pesan: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengirim pesan: " + e.getMessage());
        }
    }

    @FXML
    void refreshChat(ActionEvent event) {
        loadChatHistory();
    }

    @FXML
    void hapusChat(ActionEvent event) {
        // TODO: Implementasi untuk menghapus chat
        showAlert(Alert.AlertType.INFORMATION, "Info", 
            "Fitur hapus chat akan segera tersedia");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
