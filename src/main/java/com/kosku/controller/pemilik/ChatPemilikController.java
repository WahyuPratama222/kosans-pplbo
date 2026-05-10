package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import com.kosku.dao.ChatDAO;
import com.kosku.dao.UserDAO;
import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.PopupManager;
import com.kosku.util.SessionManager;

public class ChatPemilikController implements Initializable {

    public static User targetPenyewaChat;

    @FXML
    private NavbarPemilikController navbarController;
    @FXML
    private TextField tfCariKontak;
    @FXML
    private VBox vboxKontak;

    @FXML
    private ImageView ivHeaderProfile;
    @FXML
    private Label lblHeaderName;
    @FXML
    private Label lblHeaderStatus;

    @FXML
    private ScrollPane scrollPaneChat;
    @FXML
    private VBox vboxMessages;
    @FXML
    private TextField tfPesan;

    private ChatDAO chatDAO;
    private User activePartner;
    private Integer currentUserId;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Timeline chatPoller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Chat Pemilik berhasil dimuat!");
        chatDAO = new ChatDAO();
        currentUserId = SessionManager.getCurrentUserId();

        if (navbarController != null) {
            navbarController.setActivePage("chat");
        }

        loadContacts();

        // Auto-scroll logic
        vboxMessages.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPaneChat.setVvalue(1.0);
        });

        chatPoller = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (activePartner != null && currentUserId != null) {
                List<Chat> messages = chatDAO.getMessagesBetweenUsers(currentUserId, activePartner.getIdUser());
                if (messages.size() != vboxMessages.getChildren().size()) {
                    loadMessages();
                }
            }
        }));
        chatPoller.setCycleCount(Timeline.INDEFINITE);
        chatPoller.play();
    }

    private void loadContacts() {
        if (vboxKontak == null)
            return;
        vboxKontak.getChildren().clear();

        if (currentUserId == null)
            return;

        try {
            List<User> partners = chatDAO.getChatPartners(currentUserId);
            // Tampilkan semua penyewa agar pemilik bisa memulai chat baru
            UserDAO userDAO = new UserDAO();
            List<User> semuaPenyewa = userDAO.getUsersByRole(User.Role.PENYEWA);
            for (User p : semuaPenyewa) {
                boolean exists = partners.stream().anyMatch(u -> u.getIdUser().equals(p.getIdUser()));
                if (!exists) {
                    partners.add(p);
                }
            }
            
            // Tampilkan juga semua Admin agar bisa komplain/lapor
            List<User> semuaAdmin = userDAO.getUsersByRole(User.Role.ADMIN);
            for (User a : semuaAdmin) {
                boolean exists = partners.stream().anyMatch(u -> u.getIdUser().equals(a.getIdUser()));
                if (!exists) {
                    partners.add(a);
                }
            }

            if (partners.isEmpty()) {
                Label lblEmpty = new Label("Belum ada penyewa.");
                lblEmpty.setStyle("-fx-padding: 20; -fx-text-fill: #888;");
                vboxKontak.getChildren().add(lblEmpty);
                return;
            }

            for (User partner : partners) {
                HBox contactItem = createContactItem(partner);
                vboxKontak.getChildren().add(contactItem);
            }

            // Select first by default if nothing selected
            if (activePartner == null && !partners.isEmpty()) {
                selectPartner(partners.get(0));
            }
        } catch (Exception e) {
            System.err.println("Error loading contacts: " + e.getMessage());
        }
    }

    private HBox createContactItem(User partner) {
        HBox hbox = new HBox();
        hbox.setPrefHeight(80);
        hbox.setSpacing(14);
        hbox.getStyleClass().add("contact-item");
        hbox.setStyle(
                "-fx-padding: 14 20; -fx-cursor: hand; -fx-border-color: transparent transparent #E8EDF5 transparent; -fx-border-width: 0 0 1 0;");

        if (activePartner != null && activePartner.getIdUser().equals(partner.getIdUser())) {
            hbox.getStyleClass().add("contact-item-active");
        }

        // Profile Image
        AnchorPane imagePane = new AnchorPane();
        imagePane.setPrefSize(46, 46);
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/tesProfile.png")));
        } catch (Exception e) {
        }
        imageView.setFitWidth(46);
        imageView.setFitHeight(46);
        Circle clip = new Circle(23, 23, 23);
        imageView.setClip(clip);
        imagePane.getChildren().add(imageView);

        // Details
        VBox detailBox = new VBox();
        detailBox.setSpacing(3);
        HBox.setHgrow(detailBox, Priority.ALWAYS);

        Label lblName = new Label(partner.getUsername());
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1A2744;");

        Chat lastMsg = chatDAO.getLastMessage(User.builder().idUser(currentUserId).build(), partner);
        String preview = (lastMsg != null) ? lastMsg.getIsiPesan() : "Klik untuk membalas...";
        if (preview.length() > 35)
            preview = preview.substring(0, 32) + "...";

        Label lblPreview = new Label(preview);
        lblPreview.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        detailBox.getChildren().addAll(lblName, lblPreview);
        hbox.getChildren().addAll(imagePane, detailBox);

        hbox.setOnMouseClicked(e -> {
            for (var child : vboxKontak.getChildren()) {
                child.getStyleClass().remove("contact-item-active");
            }
            hbox.getStyleClass().add("contact-item-active");
            selectPartner(partner);
        });

        return hbox;
    }

    private void selectPartner(User partner) {
        this.activePartner = partner;
        lblHeaderName.setText(partner.getUsername());
        lblHeaderStatus.setText("🟢 Aktif");
        loadMessages();
    }

    private void loadMessages() {
        if (vboxMessages == null || activePartner == null || currentUserId == null)
            return;
        vboxMessages.getChildren().clear();

        try {
            List<Chat> messages = chatDAO.getMessagesBetweenUsers(currentUserId, activePartner.getIdUser());
            for (Chat chat : messages) {
                boolean isMine = chat.getPengirim().getIdUser().equals(currentUserId);
                HBox bubble = createMessageBubble(chat.getIsiPesan(),
                        chat.getWaktuPesan() != null ? chat.getWaktuPesan().format(timeFormatter) : "",
                        isMine);
                vboxMessages.getChildren().add(bubble);
            }
        } catch (Exception e) {
            System.err.println("Error loading messages: " + e.getMessage());
        }
    }

    private HBox createMessageBubble(String text, String time, boolean isMine) {
        HBox container = new HBox();
        container.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox(5);
        bubble.setMaxWidth(450);
        bubble.setPadding(new javafx.geometry.Insets(12, 18, 12, 18));
        bubble.getStyleClass().add(isMine ? "chat-bubble-mine" : "chat-bubble-other");

        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setStyle("-fx-text-fill: " + (isMine ? "white" : "#1E293B") + "; -fx-font-size: 14px;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle(
                "-fx-text-fill: " + (isMine ? "rgba(255,255,255,0.7)" : "#94A3B8") + "; -fx-font-size: 10px;");

        bubble.getChildren().addAll(msgLabel, timeLabel);
        container.getChildren().add(bubble);

        return container;
    }

    @FXML
    void kirimPesan(ActionEvent event) {
        if (activePartner == null) {
            PopupManager.showWarning("Peringatan", "Silakan pilih kontak terlebih dahulu sebelum mengirim pesan.");
            return;
        }
        if (tfPesan == null || tfPesan.getText().trim().isEmpty() || currentUserId == null) {
            return;
        }

        String text = tfPesan.getText().trim();

        try {
            Chat.TipeChat tipe = (activePartner.getRole() == User.Role.ADMIN) ? Chat.TipeChat.PEMILIK_ADMIN : Chat.TipeChat.PENYEWA_PEMILIK;
            
            Chat newChat = Chat.builder()
                    .pengirim(User.builder().idUser(currentUserId).build())
                    .penerima(activePartner)
                    .isiPesan(text)
                    .sudahDibaca(false)
                    .waktuPesan(LocalDateTime.now())
                    .tipeChat(tipe)
                    .build();

            chatDAO.saveOrUpdate(newChat);
            tfPesan.clear();
            loadMessages();
            loadContacts(); // Update preview
        } catch (Exception e) {
            System.err.println("Gagal kirim pesan: " + e.getMessage());
        }
    }

    @FXML
    void handleQuickReply(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            tfPesan.setText(btn.getText());
            kirimPesan(event);
        }
    }
}
