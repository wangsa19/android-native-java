# Aplikasi Dasbor Multifungsi (Android Java)

Ini adalah proyek aplikasi Android native yang dibangun menggunakan Java dan XML. Aplikasi ini berfungsi sebagai fondasi atau kerangka dasar yang mendemonstrasikan implementasi berbagai fitur inti Android dalam satu paket yang koheren.

## 📜 Deskripsi

Aplikasi ini dirancang sebagai proyek pembelajaran untuk menampilkan cara mengintegrasikan fitur-fitur penting seperti otentikasi pengguna, layanan berbasis lokasi (GPS), penggunaan sensor perangkat, operasi database (CRUD), dan desain antarmuka modern dengan Material Components. Pengguna masuk ke dalam aplikasi untuk mengakses dasbor utama di mana mereka dapat melihat data sensor, info lokasi, dan mengakses halaman manajemen catatan.

## ✨ Fitur Utama

- **Otentikasi Pengguna:** Halaman login sederhana untuk membatasi akses ke aplikasi.
- **Dasbor Profesional:** Tampilan utama yang modern menggunakan `MaterialCardView` untuk mengelompokkan informasi secara visual.
- **Layanan Lokasi (GPS):** Mendeteksi dan menampilkan koordinat Latitude dan Longitude pengguna saat ini secara real-time.
- **Integrasi Peta:** Tombol untuk membuka Google Maps dan menampilkan lokasi pengguna saat ini sebagai pin di peta.
- **Penggunaan Sensor:** Mendemonstrasikan cara membaca data dari sensor perangkat (contoh: Sensor Cahaya).
- **Manajemen Catatan (CRUD):** Halaman khusus untuk melakukan operasi Create, Read, Update, dan Delete pada catatan yang disimpan di database SQLite lokal.
- **Navigasi & UI/UX:**
    - Menggunakan `ActionBar` dengan tombol "Up" (kembali) untuk navigasi hierarkis.
    - Tombol Logout di *app bar* untuk keluar dari sesi dengan aman.
    - Desain antarmuka yang profesional dan konsisten menggunakan Material Design.

## 🛠️ Teknologi & Prasyarat

- **Bahasa:** Java
- **Antarmuka (UI):** Android XML Layouts
- **Database:** SQLite (menggunakan `SQLiteOpenHelper`)
- **Arsitektur:** Standard Activity-based
- **Library Utama:**
    - AndroidX AppCompat
    - Google Material Components
    - Google Play Services Location

**Untuk menjalankan proyek ini, Anda memerlukan:**
- Android Studio (Disarankan versi Iguana | 2023.2.1 atau yang lebih baru)
- JDK 17 atau yang lebih baru
- Perangkat Android fisik atau Emulator (API 26: Android 8.0 Oreo atau lebih tinggi)

## 🚀 Cara Menjalankan Proyek

Ikuti langkah-langkah ini untuk menjalankan proyek di lingkungan lokal Anda.

1.  **Clone Repositori**
    ```bash
    git clone https://URL_REPOSITORI_ANDA.git
    ```

2.  **Buka di Android Studio**
    - Buka Android Studio.
    - Pilih **"Open an Existing Project"**.
    - Arahkan ke folder tempat Anda meng-clone repositori, lalu klik **OK**.

3.  **Sync Gradle**
    - Android Studio akan secara otomatis memulai proses "Gradle Sync" untuk mengunduh semua *dependency* yang diperlukan. Tunggu hingga proses ini selesai. Pastikan Anda terhubung ke internet.

4.  **Jalankan Aplikasi**
    - Pilih perangkat (emulator atau ponsel fisik) dari daftar perangkat yang tersedia.
    - Klik tombol **Run 'app'** (ikon segitiga hijau) atau gunakan shortcut `Shift + F10`.

## 👨‍💻 Cara Menggunakan Aplikasi

1.  **Login**
    - Gunakan kredensial berikut untuk masuk:
        - **Username:** `admin`
        - **Password:** `12345`

2.  **Dasbor**
    - Setelah login, Anda akan melihat dasbor utama yang menampilkan:
        - Tanggal hari ini.
        - Data dari sensor cahaya.
        - Koordinat GPS Anda.

3.  **Fitur**
    - **Tampilkan di Peta:** Menekan tombol ini akan membuka Google Maps dan menunjukkan lokasi Anda.
    - **Kelola Catatan:** Membawa Anda ke halaman baru untuk menambah, melihat, mengedit, dan menghapus catatan.
    - **Logout:** Tekan ikon logout di pojok kanan atas untuk keluar dari aplikasi dan kembali ke halaman login.

---