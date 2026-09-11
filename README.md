# 📱 Subscription Tracker (Abonelik Takip)

Modern Android mimarisi ve Jetpack Compose kullanılarak geliştirilmiş, yerel veritabanı destekli kişisel abonelik ve harcama takip uygulaması.

---

## 🚀 Özellikler

* **Tam CRUD Desteği:** Abonelik ekleme, mevcut abonelikleri düzenleme ve onay pencereli silme işlemleri.
* **Dinamik Kategori Filtreleme:** Eklenen kategorilere göre anlık olarak oluşan `FilterChip` bileşenleri ile listeyi ve toplam harcamayı dinamik filtreleme.
* **Canlı Bütçe Özeti:** Filtrelenen kategoriye veya genel toplama göre anlık hesaplanan aylık harcama kartı.
* **Kalıcı Yerel Depolama:** Verilerin cihazda güvenle ve asenkron saklanması için Room Database entegrasyonu.
* **Modern UI:** Material 3 standartlarında, edge-to-edge destekli temiz ve modern kullanıcı arayüzü.

---

## 🛠 Kullanılan Teknolojiler & Kütüphaneler

* **Dil:** Kotlin
* **UI Toolkit:** Jetpack Compose & Material 3
* **Mimari:** MVVM (Model - View - ViewModel)
* **Asenkron Programlama:** Kotlin Coroutines & StateFlow
* **Yerel Veritabanı:** Room Database
* **Annotation Processing:** KSP (Kotlin Symbol Processing)

---

## 📂 Proje Mimarisi

```text
com.example.aboneliktakip
├── data
│   ├── AppDatabase.kt          # Room veritabanı tanımlayıcısı ve Singleton instance
│   ├── Subscription.kt         # Veritabanı Entity (Tablo) modeli
│   └── SubscriptionDao.kt      # Veritabanı sorguları (CRUD) ve Flow akışları
├── ui
│   └── theme                   # Compose Material 3 tema konfigürasyonları
├── viewmodel
│   └── SubscriptionViewModel.kt# UI ile veri katmanı arasındaki StateFlow yönetimi
├── HomeScreen.kt               # Ana ekran, kartlar, çipler ve diyalog bileşenleri
└── MainActivity.kt             # Giriş noktası (Entry point) ve ViewModel enjeksiyonu