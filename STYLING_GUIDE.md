# 🎨 Dokumentasi Styling & Fitur UI Kos Kurniawan

## 📋 Daftar Isi
1. [Color Palette](#color-palette)
2. [Struktur CSS](#struktur-css)
3. [Fitur Baru Admin](#fitur-baru-admin)
4. [Fitur Baru Pemilik](#fitur-baru-pemilik)
5. [Cara Menggunakan Stylesheet](#cara-menggunakan-stylesheet)
6. [Component Reference](#component-reference)

---

## 🎭 Color Palette

Sistem warna yang konsisten dan professional untuk semua halaman:

```
┌─────────────────┬──────────┬─────────────────────────┐
│ Nama            │ Hex Code │ Penggunaan              │
├─────────────────┼──────────┼─────────────────────────┤
│ Oxford Blue     │ #192338  │ Background gelap, footer│
│ Space Cadet     │ #1E2E4F  │ Background sidebar      │
│ YlnMn Blue      │ #31487A  │ Button primary, header  │
│ Jordy Blue      │ #8FB3E2  │ Button secondary, accent│
│ Lavender Web    │ #D9E1F1  │ Background ringan       │
│ Success         │ #10B981  │ Status berhasil         │
│ Warning         │ #F59E0B  │ Status peringatan       │
│ Danger          │ #EF4444  │ Status bahaya/error     │
│ Info            │ #3B82F6  │ Status informasi        │
└─────────────────┴──────────┴─────────────────────────┘
```

---

## 🎨 Struktur CSS

### File Lokasi
```
src/main/resources/css/
└── style.css (Master Stylesheet)
```

### Struktur CSS yang Disediakan

#### 1. Root Variables
```css
.root {
    -color-oxford-blue: #192338;
    -color-space-cadet: #1E2E4F;
    -color-ylnmn-blue: #31487A;
    -color-jordy-blue: #8FB3E2;
    -color-lavender: #D9E1F1;
    /* Status colors, neutral colors, dan font */
}
```

#### 2. Navbar Styles
```css
.navbar-admin     /* Gradient biru untuk admin */
.navbar-pemilik   /* Gradient biru lebih gelap untuk pemilik */
.navbar-penyewa   /* Gradient lavender untuk penyewa */
.navbar-title     /* Judul navbar */
.navbar-menu-item /* Item menu navbar */
```

#### 3. Button Styles
```css
.btn-primary      /* Tombol utama (Blue) */
.btn-secondary    /* Tombol sekunder (Light Blue) */
.btn-success      /* Tombol berhasil (Green) */
.btn-danger       /* Tombol berbahaya (Red) */
.btn-warning      /* Tombol peringatan (Yellow) */
.btn-outline      /* Tombol outline transparan */
```

#### 4. Card Styles
```css
.card             /* Kartu standar dengan shadow */
.card-header      /* Header kartu */
.card-body        /* Body kartu */
.card-footer      /* Footer kartu */
.stat-card        /* Kartu statistik khusus */
```

#### 5. Form Elements
```css
.text-field       /* Input text */
.text-area        /* Text area */
.combo-box        /* Dropdown */
.form-section     /* Section form */
.form-group       /* Group elemen form */
```

#### 6. Table Styles
```css
.table-view                    /* Tabel utama */
.table-view .column-header     /* Header tabel */
.table-row-cell:hover          /* Hover row */
.table-row-cell:selected       /* Selected row */
```

#### 7. Badge & Status
```css
.badge-primary    /* Badge biru */
.badge-success    /* Badge hijau */
.badge-warning    /* Badge kuning */
.badge-danger     /* Badge merah */
```

#### 8. Alert Box
```css
.alert-success    /* Alert berhasil */
.alert-danger     /* Alert gagal */
.alert-warning    /* Alert peringatan */
.alert-info       /* Alert informasi */
```

#### 9. Utility Classes
```css
.text-center      /* Teks center */
.text-left        /* Teks kiri */
.text-right       /* Teks kanan */
.text-bold        /* Teks bold */
.text-muted       /* Teks redup */
.text-success     /* Teks hijau */
.text-danger      /* Teks merah */
.padding-small    /* Padding kecil */
.padding-medium   /* Padding medium */
.padding-large    /* Padding besar */
.margin-top-*     /* Margin top berbagai ukuran */
.bg-light         /* Background terang */
.bg-dark          /* Background gelap */
.border-*         /* Border di berbagai sisi */
```

---

## ✨ Fitur Baru Admin

### 1. Dashboard Admin (`DashboardAdmin.fxml`)
**Lokasi**: `src/main/resources/view/Admin/`

**Fitur**:
- 📊 Statistik keseluruhan sistem
- 👥 Total pengguna dengan trend
- 🏠 Total kos dengan trend
- 📋 Booking aktif dengan trend
- 💰 Total pembayaran dengan trend
- 📌 Aksi cepat (tambah pengguna, tambah kos, lihat laporan)
- 🔔 Aktivitas terbaru
- 📊 Tabel booking terbaru

### 2. Management Pengguna (`ManagementPengguna.fxml`)
**Fitur**:
- 🔍 Search & filter pengguna
- 📊 Statistik pengguna per role
- 👥 Tabel lengkap semua pengguna
- ✏️ Edit pengguna
- ❌ Hapus pengguna
- 📄 Pagination dengan pilihan jumlah item per halaman

### 3. Management Kos (`ManagementKos.fxml`)
**Fitur**:
- 🏠 Lihat semua kos di sistem
- 📊 Statistik kos (total, kamar, rating, pendapatan)
- 🔍 Search & filter kos
- 📍 Info detail setiap kos
- ✏️ Edit kos
- ❌ Hapus kos
- 👁️ Lihat detail kos

### 4. Laporan Pembayaran (`LaporanPembayaran.fxml`)
**Fitur**:
- 💰 Total pendapatan sistem
- 📊 Statistik transaksi
- 🎨 Grafik metode pembayaran
- 📈 Top 5 kos berdasarkan pendapatan
- 💳 Breakdown metode pembayaran
- 📋 Tabel detail pembayaran
- 📥 Export dan cetak laporan

### 5. Laporan Booking (`LaporanBooking.fxml`)
**Fitur**:
- 📋 Lihat semua booking di sistem
- 📊 Statistik booking per status
- 📈 Distribusi status booking
- 📊 Analisis durasi booking
- 🔍 Search & filter booking
- 📥 Export ke Excel/PDF
- 📋 Tabel detail booking lengkap

---

## 🏠 Fitur Baru Pemilik Kos

### 1. Dashboard Pemilik (`DashboardPemilik.fxml`)
**Lokasi**: `src/main/resources/view/Pemilik/`

**Fitur**:
- 📊 Statistik kos yang dikelola
- 🏠 Total kos yang dimiliki
- 🛏️ Total kamar dengan status
- 📋 Booking pending
- 💰 Pendapatan bulan ini
- 🎯 Aksi cepat
- 🏢 Overview detail setiap kos
- 📊 Booking & pembayaran terbaru

### 2. Management Kamar (`ManagementKamar.fxml`)
**Fitur**:
- 🛏️ Grid view kamar dengan kartu
- 📊 Statistik kamar per status
- 🔍 Filter berdasarkan status (tersedia, terisi, maintenance)
- 💰 Harga kamar
- 👤 Info penghuni kamar
- ✏️ Edit kamar
- ❌ Hapus kamar
- 🔄 Ubah status kamar
- 📍 Info lokasi (lantai, area)

### 3. Laporan Pembayaran Pemilik (`LaporanPembayaran.fxml`)
**Fitur**:
- 💰 Total pembayaran pemilik
- 📊 Statistik pembayaran (lunas, pending, tertunda)
- 🎨 Breakdown metode pembayaran
- 📊 Grafik placeholder untuk chart
- 💳 Metode pembayaran breakdown
- 📋 Tabel detail pembayaran
- 📥 Export & cetak laporan

### 4. Booking Penyewa (`BookingPenyewa.fxml`)
**Fitur**:
- 📋 Lihat semua booking untuk kos pemilik
- 📊 Statistik booking per status
- ⚠️ Alert untuk pending bookings
- ✅ Konfirmasi booking
- ❌ Tolak booking
- 📝 Edit booking
- 🔄 Perpanjang booking
- ⏹️ Hentikan booking
- 💳 Info pembayaran per booking
- 📋 Tabel booking dengan filter

---

## 📝 Cara Menggunakan Stylesheet

### 1. Menambahkan Stylesheet ke FXML

```xml
<BorderPane ... stylesheets="@../../css/style.css">
```

### 2. Menggunakan Class Styles

**Tombol Primary**:
```xml
<Button text="Simpan" className="btn-primary" />
```

**Kartu Statistik**:
```xml
<VBox className="stat-card" spacing="10">
    <Label text="Total Pengguna" className="stat-label" />
    <Label text="2,547" className="stat-value" />
</VBox>
```

**Table**:
```xml
<TableView className="table-view">
    <columns>
        <TableColumn text="Nama" />
    </columns>
</TableView>
```

### 3. Kombinasi Style

```xml
<!-- Menggunakan class + inline style -->
<Label text="Judul" 
       className="label-title"
       style="-fx-padding: 10;" />

<!-- Menggunakan utiliti class -->
<VBox className="spacing-large padding-medium">
    ...
</VBox>
```

---

## 🎯 Component Reference

### Navbar Components

**Admin Navbar**:
```xml
<BorderPane style="-fx-background-color: linear-gradient(to right, #31487A, #8FB3E2);"
            className="navbar-admin">
    <Label text="🏢 Admin Dashboard" className="navbar-title" />
</BorderPane>
```

**Pemilik Navbar**:
```xml
<BorderPane style="-fx-background-color: linear-gradient(to right, #1E2E4F, #31487A);"
            className="navbar-pemilik">
    <Label text="🏠 Pemilik Kos Dashboard" className="navbar-title" />
</BorderPane>
```

### Sidebar Menu

```xml
<VBox className="side-menu" style="-fx-background-color: #1E2E4F;">
    <Button text="📊 Dashboard" className="btn-outline" />
    <Button text="👥 Manajemen Pengguna" className="btn-outline" />
    <!-- More menu items -->
</VBox>
```

### Stat Cards

```xml
<VBox className="stat-card" spacing="10">
    <Label text="Total Pengguna" className="stat-label" />
    <Label text="2,547" className="stat-value" />
    <Label text="↑ 12% dari minggu lalu" style="-fx-text-fill: #10B981;" />
</VBox>
```

### Buttons

```xml
<!-- Primary Button -->
<Button text="Simpan" className="btn-primary" />

<!-- Secondary Button -->
<Button text="Edit" className="btn-secondary" />

<!-- Success Button -->
<Button text="Konfirmasi" className="btn-success" />

<!-- Danger Button -->
<Button text="Hapus" className="btn-danger" />

<!-- Outline Button -->
<Button text="Batal" className="btn-outline" />
```

### Alert Boxes

```xml
<!-- Success Alert -->
<HBox className="alert-box alert-success">
    <Label text="Operasi berhasil!" />
</HBox>

<!-- Warning Alert -->
<HBox className="alert-box alert-warning">
    <Label text="Ada 5 booking pending!" />
</HBox>

<!-- Error Alert -->
<HBox className="alert-box alert-danger">
    <Label text="Terjadi kesalahan!" />
</HBox>
```

### Status Badge

```xml
<!-- Pending Badge -->
<Label text="PENDING" className="badge badge-warning" />

<!-- Active Badge -->
<Label text="ACTIVE" className="badge badge-success" />

<!-- Error Badge -->
<Label text="ERROR" className="badge badge-danger" />
```

---

## 📱 Responsive Layout Tips

1. **Gunakan HBox.hgrow="ALWAYS"** untuk elemen yang ingin mengembang horizontal
2. **Gunakan VBox.vgrow="ALWAYS"** untuk elemen yang ingin mengembang vertikal
3. **Gunakan Region sebagai spacer** untuk mendorong elemen ke sisi tertentu
4. **Gunakan ScrollPane** untuk konten yang panjang

---

## 🔧 Customization

### Mengubah Warna Palette

Edit di `style.css` bagian `.root`:
```css
.root {
    -color-oxford-blue: #192338;  /* Ganti dengan warna pilihan */
    -color-jordy-blue: #8FB3E2;   /* Ganti dengan warna pilihan */
    /* ... */
}
```

### Menambah Style Baru

```css
/* Tambah di akhir style.css */
.my-custom-style {
    -fx-background-color: #custom-color;
    -fx-text-fill: white;
    -fx-padding: 10px;
}
```

Gunakan di FXML:
```xml
<Button text="Custom" className="my-custom-style" />
```

---

## 📋 File-File FXML yang Telah Dibuat

### Admin
- ✅ `DashboardAdmin.fxml` - Dashboard admin
- ✅ `ManagementPengguna.fxml` - Manajemen pengguna
- ✅ `ManagementKos.fxml` - Manajemen kos
- ✅ `LaporanPembayaran.fxml` - Laporan pembayaran admin
- ✅ `LaporanBooking.fxml` - Laporan booking

### Pemilik Kos
- ✅ `DashboardPemilik.fxml` - Dashboard pemilik
- ✅ `ManagementKamar.fxml` - Manajemen kamar
- ✅ `LaporanPembayaran.fxml` - Laporan pembayaran pemilik
- ✅ `BookingPenyewa.fxml` - Booking penyewa

---

## 🚀 Best Practices

1. **Selalu gunakan className** untuk konsistensi styling
2. **Gunakan spacing dan padding utility classes** untuk layout
3. **Gunakan variable colors** dari `.root` untuk consistency
4. **Test di berbagai resolusi** untuk responsiveness
5. **Dokumentasikan custom styles** jika membuat yang baru
6. **Gunakan icons emoji** untuk visual appeal
7. **Maintain hierarchy** dalam typography

---

## 📧 Support

Untuk pertanyaan atau masalah dengan styling, silakan modifikasi `style.css` sesuai kebutuhan atau tambahkan custom styles di halaman FXML.

---

**Version**: 1.0  
**Last Updated**: 2026-04-23  
**Author**: Development Team
