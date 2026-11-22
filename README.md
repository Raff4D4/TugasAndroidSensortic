# ZenSpace

**ZenSpace** adalah aplikasi Android berbasis **Kotlin & Jetpack Compose** dengan tema *Minimalist Dark* untuk membantu menjaga fokus belajar dan bekerja. Aplikasi ini memantau pencahayaan ruangan serta kestabilan perangkat secara *real-time*, lalu menyimpan riwayat sesi fokus ke database lokal **SQLite**.

---

## Fitur Utama

* **Live Environment & Stability Monitor**: Memantau intensitas cahaya ruangan (`lx`) beserta saran ergonomi mata dan kestabilan posisi perangkat (`m/s²`).
* **Motion Distraction Alert**: Mendeteksi guncangan atau pengangkatan ponsel saat timer berjalan dan mencatat jumlah distraksi.
* **Focus Timer**: Pilihan kategori (*Belajar*, *Ngoding*, *Membaca*, *Deep Work*) dan target durasi (`15m`, `25m`, `45m`, `60m`).
* **Riwayat & Statistik SQLite (CRUD)**: Menyimpan sesi, menampilkan ringkasan statistik, filter kategori, edit judul sesi, serta hapus satuan atau semua data.

## Teknologi & Arsitektur

* **Bahasa & UI**: Kotlin, Jetpack Compose, Material Design 3
* **Database**: SQLite murni (`SQLiteOpenHelper`, database `zenspace.db`, tabel `focus_sessions`)
* **Arsitektur**: **MVVM (Model - View - ViewModel)**

```text
com.zenspace.app/
├── model/FocusSession.kt       # Model data sesi, statistik, & telemetri sensor
├── data/DatabaseHelper.kt      # Operasi CRUD SQLite (insert, query, update, delete)
├── sensor/SensorHelper.kt      # Listener sensor via StateFlow
├── viewmodel/ZenViewModel.kt   # AndroidViewModel (timer, logika sensor, & SQLite)
├── ui/
│   ├── theme/                  # Tema Minimalist Dark Material 3
│   ├── FocusScreen.kt          # Tab Fokus (sensor real-time & timer)
│   └── HistoryScreen.kt        # Tab Riwayat (statistik & CRUD SQLite)
└── MainActivity.kt             # Navigasi 2 tab & lifecycle sensor (ON_RESUME/ON_PAUSE)
```

## Sensor yang Digunakan

| Kategori | Sensor Android | Fungsi |
| :--- | :--- | :--- |
| **Environment** | `Sensor.TYPE_LIGHT` | Mengukur cahaya sekitar (Lux) untuk menilai kelayakan ruang belajar dan menghitung rata-rata Lux per sesi. |
| **Motion** | `Sensor.TYPE_ACCELEROMETER` | Menghitung perubahan percepatan linear untuk mendeteksi distraksi saat ponsel digerakkan dan menghitung *Stability Score*. |

## Screenshot Aplikasi
<img width="752" height="1600" alt="image" src="https://github.com/user-attachments/assets/4a60132c-bd9b-41dd-b3ed-84da9e09fc9a" />


## Cara Menjalankan Aplikasi

1. Clone repository ini:
   ```bash
   git clone https://github.com/Raff4D4/TugasAndroidSensortic.git
   ```
2. Buka folder proyek di **Android Studio** dan tunggu hingga **Gradle Sync** selesai (`minSdk 24`, `compileSdk 34`).
3. Jalankan aplikasi (`Shift + F10`) di **HP Android** atau **Emulator** (gunakan *Extended Controls > Virtual Sensors* pada emulator untuk menyimulasikan sensor cahaya dan gerak).
