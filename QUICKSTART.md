# ⚡ Hızlı Başlangıç - 5 Dakikada Çalıştır!

## 📱 1. Android Kurulum (2 dakika)

### APK İndir ve Yükle
```bash
# Telefonu USB ile bağla
adb install easysharenet.apk
```

### USB Debugging Aç
```
Ayarlar → Telefon Hakkında → Yapı Numarası (7 kez tıkla)
Ayarlar → Geliştirici Seçenekleri → USB Debugging ✅
```

## 💻 2. Linux Kurulum (2 dakika)

### Arch Linux
```bash
yay -S easysharenet
```

### Diğer Dağıtımlar
```bash
# Bağımlılıkları kur
sudo pacman -S android-tools redsocks iptables openbsd-netcat  # Arch
sudo apt install android-tools-adb redsocks iptables netcat    # Debian/Ubuntu
sudo dnf install android-tools redsocks iptables nmap-ncat     # Fedora

# Script'i kur
sudo curl -o /usr/local/bin/easysharenet-connect \
  https://raw.githubusercontent.com/easysharenet/easysharenet/main/linux/scripts/easysharenet-connect
sudo chmod +x /usr/local/bin/easysharenet-connect
```

## 🚀 3. Kullan! (1 dakika)

### Adım 1: Android Uygulamasını Başlat
```
📱 Uygulamayı aç → BAŞLAT butonuna bas
```

### Adım 2: Linux'ta Bağlan
```bash
sudo easysharenet-connect
```

### Adım 3: İnternet Kullan!
```bash
# Test et
curl https://ifconfig.me
ping google.com
firefox &
```

## ✅ Başarılı!

```
╔════════════════════════════════════╗
║   ✓ Bağlantı Başarılı!             ║
╚════════════════════════════════════╝

📊 Bağlantı Bilgileri:
  Proxy: SOCKS5://127.0.0.1:1080
  DNS: 8.8.8.8
  Durum: Aktif
```

## 🆘 Sorun mu Var?

### Telefon Bulunamıyor?
```bash
adb devices
# Telefonda "Bu bilgisayara güven" onayını ver
```

### Proxy Çalışmıyor?
```bash
# Android uygulamasının çalıştığını kontrol et
# Bildirimlerde "🟢 Çalışıyor" yazmalı
```

### İnternet Yok?
```bash
# Bağlantıyı test et
nc -zv 127.0.0.1 1080
```

## 📚 Daha Fazla Bilgi

- [README.md](README.md) - Detaylı dokümantasyon
- [TECHNICAL_DETAILS.md](TECHNICAL_DETAILS.md) - Teknik detaylar
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - Kaynak koddan build

---

**5 dakikada hazır! 🎉**
