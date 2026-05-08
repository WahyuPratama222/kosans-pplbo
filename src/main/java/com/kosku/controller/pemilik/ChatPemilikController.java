package com.kosku.controller.pemilik;

import com.kosku.dao.ChatDAO;
import com.kosku.dao.UserDAO;
import com.kosku.model.Chat;
import com.kosku.model.User;
import com.kosku.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatPemilikController {

    @FXML private NavbarPemilikController navbarController;
    @FXML private VBox contactContainer;
    @FXML private VBox messageContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField messageField;
    @FXML private Label activeContactName;
    @FXML private ImageView activeContactImage;
    @FXML private Label activeContactStatus;

    private final ChatDAO chatDAO = new ChatDAO();
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;
    private User selectedContact;

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setActivePage("chat");
        }
        
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            currentUser = userDAO.getById(User.class, 3);
        }

        loadContacts();
        
        messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });
    }

    private void loadContacts() {
        contactContainer.getChildren().clear();
        List<User> contacts = chatDAO.getChatContacts(currentUser);
        
        if (contacts.isEmpty()) {
            Label noContact = new Label("Belum ada pesan masuk.");
            noContact.setStyle("-fx-text-fill: -color-dark-gray; -fx-padding: 20;");
            contactContainer.getChildren().add(noContact);
            return;
        }

        for (User contact : contacts) {
            contactContainer.getChildren().add(createContactItem(contact));
        }
    }

    private HBox createContactItem(User contact) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setStyle("-fx-background-radius: 8; -fx-cursor: hand;");
        item.getStyleClass().add("contact-item");

        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/images/tesProfile.png")));
        img.setFitHeight(45);
        img.setFitWidth(45);
        Circle clip = new Circle(22.5, 22.5, 22.5);
        img.setClip(clip);

        VBox info = new VBox(2);
        Label name = new Label(contact.getUsername());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Chat lastMsg = chatDAO.getLastMessage(currentUser, contact);
        String previewText = (lastMsg != null) ? lastMsg.getIsiPesan() : "Mulai percakapan...";
        if (previewText.length() > 30) previewText = previewText.substring(0, 27) + "...";
        
        Label preview = new Label(previewText);
        preview.setStyle("-fx-text-fill: -color-dark-gray; -fx-font-size: 12px;");

        info.getChildren().addAll(name, preview);
        item.getChildren().addAll(img, info);

        item.setOnMouseClicked(e -> selectContact(contact, item));

        return item;
    }

    private void selectContact(User contact, HBox item) {
        selectedContact = contact;
        
        for (javafx.scene.Node node : contactContainer.getChildren()) {
            node.getStyleClass().remove("contact-item-active");
        }
        item.getStyleClass().add("contact-item-active");

        activeContactName.setText(contact.getUsername());
        activeContactStatus.setVisible(true);
        loadMessages();
    }

    private void loadMessages() {
        if (selectedContact == null) return;
        
        messageContainer.getChildren().clear();
        List<Chat> history = chatDAO.getChatHistory(currentUser, selectedContact);
        
        for (Chat chat : history) {
            boolean isMine = chat.getPengirim().getIdUser().equals(currentUser.getIdUser());
            messageContainer.getChildren().add(createMessageBubble(chat.getIsiPesan(), chat.getWaktuPesan().format(DateTimeFormatter.ofPattern("HH:mm")), isMine));
        }
    }

    private HBox createMessageBubble(String text, String time, boolean isMine) {
        HBox container = new HBox();
        container.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        VBox bubble = new VBox(5);
        bubble.setMaxWidth(450);
        bubble.setPadding(new Insets(12, 18, 12, 18));
        bubble.getStyleClass().add(isMine ? "chat-bubble-mine" : "chat-bubble-other");

        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setStyle("-fx-text-fill: " + (isMine ? "white" : "#1E293B") + "; -fx-font-size: 14px; -fx-line-spacing: 2px;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: " + (isMine ? "rgba(255,255,255,0.7)" : "#94A3B8") + "; -fx-font-size: 10px;");
        
        bubble.getChildren().addAll(msgLabel, timeLabel);
        container.getChildren().add(bubble);
        
        return container;
    }

    @FXML
    private void handleSendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty() || selectedContact == null) return;

        Chat newChat = Chat.builder()
                .pengirim(currentUser)
                .penerima(selectedContact)
                .isiPesan(text)
                .sudahDibaca(false)
                .tipeChat(Chat.TipeChat.PENYEWA_PEMILIK)
                .build();

        chatDAO.saveOrUpdate(newChat);
        messageField.clear();
        loadMessages();
        loadContacts();
    }
}
