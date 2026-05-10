package com.kosku.controller.penyewa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import com.kosku.util.PopupManager;
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
import com.kosku.util.SessionManager;

public class ChatController implements Initializable {

    @FXML
    private NavbarController navbarController;
    @FXML
    private TextField tfCariKontak;
    @FXML
    private VBox vboxKontak;

    public static User targetPemilikChat;

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
        System.out.println("Chat Penyewa berhasil dimuat!");
        chatDAO = new ChatDAO();
        currentUserId = SessionManager.getCurrentUserId();

        if (navbarController != null) {
            navbarController.setHighlight("chat");
        }

        loadContacts();

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

            // Inject targetPemilikChat if it is provided (from Detail Kos)
            if (targetPemilikChat != null) {
                boolean exists = partners.stream().anyMatch(p -> p.getIdUser().equals(targetPemilikChat.getIdUser()));
                if (!exists) {
                    partners.add(0, targetPemilikChat);
                }
            }
            
            // Tampilkan semua pemilik kos agar penyewa bisa memulai chat baru
            UserDAO userDAO = new UserDAO();
            List<User> semuaPemilik = userDAO.getUsersByRole(User.Role.PEMILIK);
            for (User p : semuaPemilik) {
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
                Label lblEmpty = new Label("Belum ada percakapan.");
                lblEmpty.setStyle("-fx-padding: 20; -fx-text-fill: #888;");
                vboxKontak.getChildren().add(lblEmpty);
                return;
            }

            for (User partner : partners) {
                HBox contactItem = createContactItem(partner);
                vboxKontak.getChildren().add(contactItem);
            }

            // Select the target partner if set, otherwise the first
            if (targetPemilikChat != null) {
                selectPartner(targetPemilikChat);
                targetPemilikChat = null; // reset
            } else if (!partners.isEmpty()) {
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
        hbox.setStyle(
                "-fx-padding: 14 20; -fx-background-color: white; -fx-cursor: hand; -fx-border-color: transparent transparent #E8EDF5 transparent; -fx-border-width: 0 0 1 0;");

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

        HBox nameTimeBox = new HBox();
        Label lblName = new Label(
                partner.getUsername() != null ? partner.getUsername() : "User " + partner.getIdUser());
        lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1A2744;");
        HBox.setHgrow(lblName, Priority.ALWAYS);
        nameTimeBox.getChildren().add(lblName);

        detailBox.getChildren().addAll(nameTimeBox);

        hbox.getChildren().addAll(imagePane, detailBox);

        hbox.setOnMouseClicked(e -> {
            // Reset background for all contacts
            for (var child : vboxKontak.getChildren()) {
                child.setStyle(
                        "-fx-padding: 14 20; -fx-background-color: white; -fx-cursor: hand; -fx-border-color: transparent transparent #E8EDF5 transparent; -fx-border-width: 0 0 1 0;");
            }
            hbox.setStyle(
                    "-fx-padding: 14 20; -fx-background-color: #EEF3FF; -fx-cursor: hand; -fx-border-color: transparent transparent #E8EDF5 transparent; -fx-border-width: 0 0 1 0;");
            selectPartner(partner);
        });

        return hbox;
    }

    private void selectPartner(User partner) {
        this.activePartner = partner;
        lblHeaderName.setText(partner.getUsername() != null ? partner.getUsername() : "User " + partner.getIdUser());
        lblHeaderStatus.setText("🟢 Online");
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
                HBox bubble = createMessageBubble(chat, isMine);
                vboxMessages.getChildren().add(bubble);
            }

            // Scroll to bottom
            scrollPaneChat.applyCss();
            scrollPaneChat.layout();
            scrollPaneChat.setVvalue(1.0);

        } catch (Exception e) {
            System.err.println("Error loading messages: " + e.getMessage());
        }
    }

    private HBox createMessageBubble(Chat chat, boolean isMine) {
        HBox hbox = new HBox();
        hbox.setSpacing(12);

        VBox vbox = new VBox();
        vbox.setSpacing(4);

        String timeStr = chat.getWaktuPesan() != null ? chat.getWaktuPesan().format(timeFormatter) : "";
        Label lblTime = new Label(timeStr);
        lblTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #bbb;");

        HBox contentBox = new HBox();
        Label lblMsg = new Label(chat.getIsiPesan());
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(600);

        if (isMine) {
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            vbox.setAlignment(Pos.TOP_RIGHT);

            Label lblSender = new Label("Saya");
            lblSender.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

            lblMsg.setStyle(
                    "-fx-background-color: #2D6BE4; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 16; -fx-background-radius: 16 0 16 16;");

            HBox timeStatusBox = new HBox(4);
            timeStatusBox.setAlignment(Pos.CENTER_RIGHT);
            Label lblStatus = new Label("✓✓");
            lblStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #2D6BE4;");
            timeStatusBox.getChildren().addAll(lblTime, lblStatus);

            contentBox.setAlignment(Pos.CENTER_RIGHT);
            contentBox.getChildren().add(lblMsg);

            vbox.getChildren().addAll(lblSender, contentBox, timeStatusBox);
            hbox.getChildren().add(vbox);
        } else {
            hbox.setAlignment(Pos.BOTTOM_LEFT);
            vbox.setAlignment(Pos.TOP_LEFT);

            ImageView imgProfile = new ImageView();
            try {
                imgProfile.setImage(new Image(getClass().getResourceAsStream("/images/tesProfile.png")));
            } catch (Exception e) {
            }
            imgProfile.setFitWidth(36);
            imgProfile.setFitHeight(36);
            imgProfile.setClip(new Circle(18, 18, 18));

            Label lblSender = new Label(chat.getPengirim().getUsername());
            lblSender.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

            lblMsg.setStyle(
                    "-fx-background-color: white; -fx-text-fill: #1A2744; -fx-font-size: 14px; -fx-padding: 12 16; -fx-background-radius: 0 16 16 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 6, 0, 0, 2);");

            contentBox.getChildren().add(lblMsg);

            vbox.getChildren().addAll(lblSender, contentBox, lblTime);
            hbox.getChildren().addAll(imgProfile, vbox);
        }

        return hbox;
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
            Chat.TipeChat tipe = (activePartner.getRole() == User.Role.ADMIN) ? Chat.TipeChat.PENYEWA_ADMIN : Chat.TipeChat.PENYEWA_PEMILIK;
            
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
            loadMessages(); // Refresh UI

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
