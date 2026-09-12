# Camera Reticle Overlay

App Android sederhana yang menampilkan garis bantu (reticle) di atas layar — berguna untuk framing foto/video, alignment desain, atau sekadar guide visual. Overlay ini **berdiri sendiri**: tidak membaca, menyuntik, atau berkomunikasi dengan aplikasi lain yang sedang berjalan. Ia hanya menggambar di window terpisah di atas layar (memakai izin Android resmi `SYSTEM_ALERT_WINDOW`).

## Fitur
- 4 gaya reticle: Crosshair, Rule of Thirds, Center Dot, Circle Guide
- Slider opacity
- Overlay bisa digeser (drag) langsung di layar
- Tombol close kecil di overlay buat mematikan cepat
- Berjalan sebagai foreground service (ada notifikasi permanen selama aktif — ini standar Android untuk overlay app, supaya user selalu sadar overlay sedang jalan)

## Cara build
1. Buka folder ini di Android Studio (File → Open).
2. Biarkan Gradle sync selesai.
3. Run ke device/emulator (Run ▶).

## Cara pakai
1. Buka app, pilih gaya reticle & opacity.
2. Tekan "Tampilkan Overlay".
3. Kalau diminta izin "Display over other apps", aktifkan lalu kembali ke app dan tekan tombol lagi.
4. Overlay akan muncul di atas layar — buka app kamera atau app lain, garis tetap terlihat.
5. Geser reticle dengan menyentuh & drag di layar.
6. Ketuk ikon X kecil di pojok kanan atas overlay, atau tombol "Sembunyikan Overlay" di app, untuk mematikan.

## Batasan yang disengaja
Overlay ini didesain generik (framing kamera / alignment / guide visual) dan tidak menyasar aplikasi tertentu. Ia tidak membaca piksel layar, tidak melakukan image recognition, dan tidak terhubung ke proses app lain — jadi cocok untuk kebutuhan legit seperti fotografi atau desain, bukan untuk mem-bypass fair-play di game kompetitif manapun.
