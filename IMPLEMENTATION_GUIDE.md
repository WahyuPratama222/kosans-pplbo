# 🔧 Panduan Implementasi & Integrasi dengan Java Controller

## 📋 Daftar Isi
1. [Setup FXML dengan Controller](#setup-fxml-dengan-controller)
2. [Contoh Controller Implementation](#contoh-controller-implementation)
3. [Data Binding dengan TableView](#data-binding-dengan-tableview)
4. [Event Handling & Action Buttons](#event-handling--action-buttons)
5. [Navigation Between Scenes](#navigation-between-scenes)
6. [Best Practices](#best-practices)

---

## 🎯 Setup FXML dengan Controller

### 1. Menambahkan Controller Reference di FXML

```xml
<?xml version="1.0" encoding="UTF-8"?>
...
<BorderPane 
    xmlns="http://javafx.com/javafx/26" 
    xmlns:fx="http://javafx.com/fxml/1"
    fx:controller="com.kosku.controller.AdminDashboardController"
    stylesheets="@../../css/style.css">
    ...
</BorderPane>
```

### 2. Struktur Folder Controller

```
src/main/java/com/kosku/
├── controller/
│   ├── admin/
│   │   ├── AdminDashboardController.java
│   │   ├── ManagementPenggunaController.java
│   │   ├── ManagementKosController.java
│   │   ├── LaporanPembayaranAdminController.java
│   │   └── LaporanBookingController.java
│   └── pemilik/
│       ├── PemilikDashboardController.java
│       ├── ManagementKamarController.java
│       ├── LaporanPembayaranPemilikController.java
│       └── BookingPenyewaController.java
```

---

## 💻 Contoh Controller Implementation

### A. Admin Dashboard Controller

```java
package com.kosku.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import com.kosku.model.User;
import com.kosku.dao.UserDAO;
import com.kosku.dao.KosDAO;
import com.kosku.dao.BookingDAO;
import com.kosku.dao.PembayaranDAO;
import java.util.List;

public class AdminDashboardController {
    
    @FXML private BorderPane rootPane;
    @FXML private Label totalUserLabel;
    @FXML private Label totalKosLabel;
    @FXML private Label totalBookingLabel;
    @FXML private Label totalPaymentLabel;
    @FXML private TableView<Booking> recentBookingTable;
    
    private UserDAO userDAO;
    private KosDAO kosDAO;
    private BookingDAO bookingDAO;
    private PembayaranDAO pembayaranDAO;
    
    @FXML
    public void initialize() {
        // Initialize DAOs
        this.userDAO = new UserDAO();
        this.kosDAO = new KosDAO();
        this.bookingDAO = new BookingDAO();
        this.pembayaranDAO = new PembayaranDAO();
        
        // Load data
        loadDashboardData();
        loadRecentBookings();
    }
    
    private void loadDashboardData() {
        try {
            // Get total users
            int totalUsers = userDAO.getTotalCount();
            totalUserLabel.setText(String.valueOf(totalUsers));
            
            // Get total kos
            int totalKos = kosDAO.getTotalCount();
            totalKosLabel.setText(String.valueOf(totalKos));
            
            // Get active bookings
            int activeBookings = bookingDAO.getActiveBookingCount();
            totalBookingLabel.setText(String.valueOf(activeBookings));
            
            // Get total payment this month
            long totalPayment = pembayaranDAO.getTotalPaymentThisMonth();
            totalPaymentLabel.setText(formatCurrency(totalPayment));
            
        } catch (Exception e) {
            showError("Error loading dashboard data", e.getMessage());
        }
    }
    
    private void loadRecentBookings() {
        try {
            List<Booking> recentBookings = bookingDAO.getRecentBookings(5);
            recentBookingTable.getItems().setAll(recentBookings);
        } catch (Exception e) {
            showError("Error loading bookings", e.getMessage());
        }
    }
    
    // Action handlers
    @FXML
    private void handleAddUser() {
        // Open add user dialog
        System.out.println("Membuka dialog tambah pengguna");
    }
    
    @FXML
    private void handleAddKos() {
        // Open add kos dialog
        System.out.println("Membuka dialog tambah kos");
    }
    
    @FXML
    private void handleViewReport() {
        // Navigate to report page
        System.out.println("Navigasi ke halaman laporan");
    }
    
    // Utility methods
    private String formatCurrency(long amount) {
        return String.format("Rp %,d", amount);
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

### B. Management Pengguna Controller

```java
package com.kosku.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import com.kosku.model.User;
import com.kosku.dao.UserDAO;
import java.util.List;

public class ManagementPenggunaController {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    
    private UserDAO userDAO;
    private ObservableList<User> userData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        this.userDAO = new UserDAO();
        setupTable();
        setupComboBoxes();
        loadUsers();
    }
    
    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        userTable.setItems(userData);
    }
    
    private void setupComboBoxes() {
        roleCombo.setItems(FXCollections.observableArrayList(
            "Semua", "Admin", "Pemilik Kos", "Penyewa"
        ));
        statusCombo.setItems(FXCollections.observableArrayList(
            "Semua", "Aktif", "Nonaktif"
        ));
    }
    
    private void loadUsers() {
        try {
            List<User> users = userDAO.getAll();
            userData.setAll(users);
        } catch (Exception e) {
            showError("Error loading users", e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String search = searchField.getText();
        String role = roleCombo.getValue();
        String status = statusCombo.getValue();
        
        try {
            List<User> results = userDAO.search(search, role, status);
            userData.setAll(results);
        } catch (Exception e) {
            showError("Error searching", e.getMessage());
        }
    }
    
    @FXML
    private void handleReset() {
        searchField.clear();
        roleCombo.setValue("Semua");
        statusCombo.setValue("Semua");
        loadUsers();
    }
    
    @FXML
    private void handleAddUser() {
        // Open add user dialog/window
        System.out.println("Membuka form tambah pengguna");
    }
    
    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (confirmDelete("Hapus pengguna " + selected.getName() + "?")) {
                try {
                    userDAO.delete(selected.getId());
                    showSuccess("Pengguna berhasil dihapus");
                    loadUsers();
                } catch (Exception e) {
                    showError("Error deleting user", e.getMessage());
                }
            }
        } else {
            showWarning("Pilih pengguna", "Silakan pilih pengguna yang ingin dihapus");
        }
    }
    
    // Utility methods
    private boolean confirmDelete(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

### C. Management Kamar Controller (Pemilik)

```java
package com.kosku.controller.pemilik;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import com.kosku.model.Kamar;
import com.kosku.dao.KamarDAO;
import com.kosku.dao.KosDAO;
import java.util.List;

public class ManagementKamarController {
    
    @FXML private ComboBox<String> kosCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;
    @FXML private GridPane kamarGrid;
    @FXML private Label totalKamarLabel;
    @FXML private Label kamarTerisiLabel;
    @FXML private Label kamarKosongLabel;
    
    private KamarDAO kamarDAO;
    private KosDAO kosDAO;
    private int currentPemilikId;
    
    @FXML
    public void initialize() {
        this.kamarDAO = new KamarDAO();
        this.kosDAO = new KosDAO();
        // Get current user from session/preference
        this.currentPemilikId = getCurrentPemilikId();
        
        setupComboBoxes();
        loadKamarData();
    }
    
    private void setupComboBoxes() {
        try {
            List<String> kosList = kosDAO.getKosNamesByPemilik(currentPemilikId);
            kosCombo.setItems(FXCollections.observableArrayList(kosList));
        } catch (Exception e) {
            showError("Error loading kos", e.getMessage());
        }
        
        statusCombo.setItems(FXCollections.observableArrayList(
            "Semua", "Tersedia", "Terisi", "Maintenance"
        ));
    }
    
    private void loadKamarData() {
        try {
            List<Kamar> kamarList = kamarDAO.getKamarByPemilik(currentPemilikId);
            
            // Update stats
            updateStats(kamarList);
            
            // Create and display kamar cards
            displayKamarCards(kamarList);
            
        } catch (Exception e) {
            showError("Error loading kamar", e.getMessage());
        }
    }
    
    private void updateStats(List<Kamar> kamarList) {
        totalKamarLabel.setText(String.valueOf(kamarList.size()));
        
        long terisi = kamarList.stream()
            .filter(k -> "Terisi".equals(k.getStatus()))
            .count();
        
        long kosong = kamarList.size() - terisi;
        
        kamarTerisiLabel.setText(String.format("%d (%.0f%%)", terisi, 
            (terisi * 100.0) / kamarList.size()));
        kamarKosongLabel.setText(String.valueOf(kosong));
    }
    
    private void displayKamarCards(List<Kamar> kamarList) {
        kamarGrid.getChildren().clear();
        
        int row = 0, col = 0;
        for (Kamar kamar : kamarList) {
            VBox card = createKamarCard(kamar);
            kamarGrid.add(card, col, row);
            
            col++;
            if (col >= 4) { // 4 columns per row
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createKamarCard(Kamar kamar) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-padding: 10;");
        
        Label noKamar = new Label("🛏️ " + kamar.getNomorKamar());
        noKamar.setStyle("-fx-font-weight: bold;");
        
        Label tipe = new Label("Tipe: " + kamar.getTipe());
        Label harga = new Label("Harga: Rp " + kamar.getHarga());
        Label penghuni = new Label("Penghuni: " + (kamar.getPenyewa() != null ? 
            kamar.getPenyewa().getNama() : "-"));
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> handleEditKamar(kamar));
        
        Button deleteBtn = new Button("Hapus");
        deleteBtn.setOnAction(e -> handleDeleteKamar(kamar));
        
        card.getChildren().addAll(noKamar, tipe, harga, penghuni, editBtn, deleteBtn);
        return card;
    }
    
    @FXML
    private void handleSearch() {
        // Implement search logic
        System.out.println("Searching for: " + searchField.getText());
    }
    
    @FXML
    private void handleAddKamar() {
        // Open add kamar dialog
        System.out.println("Membuka form tambah kamar");
    }
    
    private void handleEditKamar(Kamar kamar) {
        // Open edit kamar dialog with kamar data
        System.out.println("Edit kamar: " + kamar.getNomorKamar());
    }
    
    private void handleDeleteKamar(Kamar kamar) {
        if (confirmDelete("Hapus kamar " + kamar.getNomorKamar() + "?")) {
            try {
                kamarDAO.delete(kamar.getId());
                showSuccess("Kamar berhasil dihapus");
                loadKamarData();
            } catch (Exception e) {
                showError("Error deleting kamar", e.getMessage());
            }
        }
    }
    
    // Utility methods
    private int getCurrentPemilikId() {
        // Get from session or authentication manager
        return 1; // Placeholder
    }
    
    private boolean confirmDelete(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

---

## 📊 Data Binding dengan TableView

### Contoh Implementation

```java
// Setup columns dengan PropertyValueFactory
@FXML
private void setupTableColumns() {
    // User table
    userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    
    // Booking table
    bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    bookingGuestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
    bookingRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
    bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    
    // Add action buttons column
    addActionColumn();
}

// Menambah action buttons column
private void addActionColumn() {
    TableColumn<User, Void> actionColumn = new TableColumn<>("Aksi");
    
    Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = 
        new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Hapus");
                    
                    {
                        editBtn.setOnAction(e -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });
                        deleteBtn.setOnAction(e -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, editBtn, deleteBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
    
    actionColumn.setCellFactory(cellFactory);
    userTable.getColumns().add(actionColumn);
}

// Binding data dengan observable list
private void loadAndBindData() {
    ObservableList<User> observableUsers = FXCollections.observableArrayList();
    
    try {
        List<User> users = userDAO.getAll();
        observableUsers.addAll(users);
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    userTable.setItems(observableUsers);
}
```

---

## 🔘 Event Handling & Action Buttons

### Button Action Examples

```java
// Search/Filter Action
@FXML
private void handleSearch() {
    String searchText = searchField.getText();
    String selectedRole = roleCombo.getValue();
    String selectedStatus = statusCombo.getValue();
    
    try {
        List<User> results = userDAO.search(searchText, selectedRole, selectedStatus);
        userData.setAll(results);
    } catch (Exception e) {
        showError("Search Error", e.getMessage());
    }
}

// Add/Create Action
@FXML
private void handleAddUser() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/view/admin/AddUserDialog.fxml"));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("Tambah Pengguna Baru");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        loadUsers(); // Refresh data after closing
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Edit Action
private void handleEditUser(User user) {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/view/admin/EditUserDialog.fxml"));
        Parent root = loader.load();
        
        EditUserController controller = loader.getController();
        controller.setUser(user);
        
        Stage stage = new Stage();
        stage.setTitle("Edit Pengguna");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        loadUsers(); // Refresh data after closing
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Delete Action
private void handleDeleteUser(User user) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Konfirmasi Hapus");
    alert.setHeaderText("Hapus Pengguna");
    alert.setContentText("Apakah Anda yakin ingin menghapus " + user.getName() + "?");
    
    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        try {
            userDAO.delete(user.getId());
            showSuccess("Pengguna berhasil dihapus");
            loadUsers();
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }
}

// Export/Download Action
@FXML
private void handleExport() {
    try {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as Excel");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            // Export data to Excel (using Apache POI or similar)
            exportToExcel(file, userData);
            showSuccess("Data berhasil diexport ke " + file.getName());
        }
    } catch (Exception e) {
        showError("Export Error", e.getMessage());
    }
}

// Print Action
@FXML
private void handlePrint() {
    try {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(rootPane.getScene().getWindow())) {
            // Print the table or report
            printerJob.printPage(rootPane);
            printerJob.endJob();
        }
    } catch (Exception e) {
        showError("Print Error", e.getMessage());
    }
}
```

---

## 🔄 Navigation Between Scenes

### Main Application Controller

```java
package com.kosku.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    
    @FXML private BorderPane mainContainer;
    private Stage primaryStage;
    private String userRole; // "admin", "pemilik", "penyewa"
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void setUserRole(String role) {
        this.userRole = role;
        setupMenuBasedOnRole();
    }
    
    private void setupMenuBasedOnRole() {
        if ("admin".equals(userRole)) {
            setupAdminMenu();
        } else if ("pemilik".equals(userRole)) {
            setupPemilikMenu();
        } else if ("penyewa".equals(userRole)) {
            setupPenyewaMenu();
        }
    }
    
    private void setupAdminMenu() {
        // Setup admin menu and navigate to admin dashboard
        navigateTo("/view/Admin/DashboardAdmin.fxml");
    }
    
    private void setupPemilikMenu() {
        // Setup pemilik menu and navigate to pemilik dashboard
        navigateTo("/view/Pemilik/DashboardPemilik.fxml");
    }
    
    private void setupPenyewaMenu() {
        // Setup penyewa menu and navigate to penyewa dashboard
        navigateTo("/view/penyewa/MainMenuPenyewa.fxml");
    }
    
    // Navigation methods
    @FXML
    private void handleDashboard() {
        if ("admin".equals(userRole)) {
            navigateTo("/view/Admin/DashboardAdmin.fxml");
        } else if ("pemilik".equals(userRole)) {
            navigateTo("/view/Pemilik/DashboardPemilik.fxml");
        }
    }
    
    @FXML
    private void handleManagementPengguna() {
        navigateTo("/view/Admin/ManagementPengguna.fxml");
    }
    
    @FXML
    private void handleManagementKos() {
        navigateTo("/view/Admin/ManagementKos.fxml");
    }
    
    @FXML
    private void handleManagementKamar() {
        navigateTo("/view/Pemilik/ManagementKamar.fxml");
    }
    
    @FXML
    private void handleLaporanPembayaran() {
        if ("admin".equals(userRole)) {
            navigateTo("/view/Admin/LaporanPembayaran.fxml");
        } else if ("pemilik".equals(userRole)) {
            navigateTo("/view/Pemilik/LaporanPembayaran.fxml");
        }
    }
    
    @FXML
    private void handleLaporanBooking() {
        navigateTo("/view/Admin/LaporanBooking.fxml");
    }
    
    @FXML
    private void handleBookingPenyewa() {
        navigateTo("/view/Pemilik/BookingPenyewa.fxml");
    }
    
    @FXML
    private void handleLogout() {
        // Clear session
        // Navigate to login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load: " + fxmlPath);
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

---

## ✨ Best Practices

### 1. **Separation of Concerns**
```
✅ Model        → Hanya data dan business logic
✅ DAO          → Database operations
✅ Controller   → JavaFX logic dan event handling
✅ FXML         → UI structure dan layout
✅ CSS          → Styling dan appearance
```

### 2. **Error Handling**
```java
try {
    // Perform operation
    List<User> users = userDAO.getAll();
    userData.setAll(users);
} catch (DatabaseException e) {
    showError("Database Error", "Could not fetch users");
} catch (Exception e) {
    showError("Error", "An unexpected error occurred");
}
```

### 3. **Data Loading Pattern**
```java
@FXML
public void initialize() {
    setupUI();      // Setup components
    loadData();     // Load initial data
    attachListeners(); // Attach event listeners
}

private void loadData() {
    // Load data in background thread to avoid UI freezing
    Task<List<User>> task = new Task<>() {
        @Override
        protected List<User> call() throws Exception {
            return userDAO.getAll();
        }
    };
    
    task.setOnSucceeded(e -> userData.setAll(task.getValue()));
    task.setOnFailed(e -> showError("Error", "Could not load data"));
    
    new Thread(task).start();
}
```

### 4. **Responsive UI**
```java
// Use HBox.hgrow for horizontal expansion
HBox.setHgrow(searchField, Priority.ALWAYS);

// Use VBox.vgrow for vertical expansion
VBox.setVgrow(tableView, Priority.ALWAYS);

// Use Region as spacer
Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);
```

### 5. **Resource Management**
```java
@Override
public void initialize() {
    // Load resources
    loadCSS();
    loadImages();
}

private void loadCSS() {
    String css = getClass().getResource("/css/style.css").toExternalForm();
    scene.getStylesheets().add(css);
}

// Cleanup when controller is destroyed
public void cleanup() {
    // Release resources
    if (userDAO != null) {
        userDAO.close();
    }
}
```

---

## 📚 Referensi

- JavaFX Documentation: https://docs.oracle.com/javase/8/javafx/
- CSS Reference: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html
- FXML Guide: https://docs.oracle.com/javase/8/javafx/fxml-reference/
- Scene Builder: https://gluonhq.com/products/scene-builder/

---

**Version**: 1.0  
**Last Updated**: 2026-04-23

✅ Implementasi siap dilakukan!
