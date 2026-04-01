# 🌐 EasyShareNet

[![Android Build](https://github.com/yusufyusufyusuf474734-collab/internet/actions/workflows/android-build.yml/badge.svg)](https://github.com/yusufyusufyusuf474734-collab/internet/actions/workflows/android-build.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-green.svg)](https://www.android.com)
[![Arch Linux](https://img.shields.io/badge/Arch-Linux-1793D1?logo=arch-linux&logoColor=fff)](https://archlinux.org)

**Operatör hotspot kısıtlamalarını bypass eden USB tethering uygulaması**

Root gerektirmez, basit ve etkili. EasyTether'ın açık kaynak alternatifi.

## ✨ Özellikler

- ✅ **Root Gerektirmez** - ADB ile çalışır
- ✅ **Tüm Portlar Açık** - HTTPS, Gaming, IM destekli
- ✅ **Operatör Bypass** - TTL ve paket manipülasyonu
- ✅ **Arch Linux Desteği** - Optimize edilmiş driver
- ✅ **Basit Kullanım** - Tek komut ile bağlan
- ✅ **Açık Kaynak** - Şeffaf ve güvenli

## 🎯 Nasıl Çalışır?

```
[Android App] → [SOCKS5 Proxy] → [USB ADB] → [Linux Driver] → [İnternet]
```

### Bypass Yöntemi
1. **Paket Yeniden Oluşturma**: Tüm paketler telefon uygulaması tarafından oluşturulur
2. **TTL Korunması**: Paketler telefon seviyesinde TTL değerine sahip
3. **Tek Kaynak**: Operatör için tek cihaz trafiği gibi görünür

## 📦 Kurulum

### Android Tarafı

1. **APK'yı İndirin**
```bash
# GitHub Releases'dan indir (yakında)
# Veya kendin build et:
git clone https://github.com/yusufyusufyusuf474734-collab/internet.git
cd internet/android
gradle assembleRelease

# Telefona yükle
adb install app/build/outputs/apk/release/app-release-unsigned.apk
```

2. **USB Debugging Açın**
```
Ayarlar → Telefon Hakkında → Yapı Numarası (7 kez tıkla)
Ayarlar → Geliştirici Seçenekleri → USB Debugging (Aç)
```

### Linux Tarafı (Arch)

```bash
# Manuel kurulum (Önerilen)
git clone https://github.com/yusufyusufyusuf474734-collab/internet.git
cd internet/linux/PKGBUILD
makepkg -si

# Veya direkt script kurulumu
sudo curl -o /usr/local/bin/easysharenet-connect \
  https://raw.githubusercontent.com/yusufyusufyusuf474734-collab/internet/main/linux/scripts/easysharenet-connect
sudo chmod +x /usr/local/bin/easysharenet-connect
```

### Diğer Linux Dağıtımları

```bash
# Bağımlılıkları kur
sudo apt install android-tools-adb redsocks iptables netcat  # Debian/Ubuntu
sudo dnf install android-tools redsocks iptables nmap-ncat   # Fedora
sudo pacman -S android-tools redsocks iptables openbsd-netcat # Arch

# Script'i kur
sudo curl -o /usr/local/bin/easysharenet-connect \
  https://raw.githubusercontent.com/yusufyusufyusuf474734-collab/internet/main/linux/scripts/easysharenet-connect
sudo chmod +x /usr/local/bin/easysharenet-connect
```

## 🚀 Kullanım

### 1. Android Uygulamasını Başlat
- Uygulamayı aç
- "BAŞLAT" butonuna bas
- Bildirimde "Çalışıyor" yazısını gör

### 2. Telefonu Bağla
```bash
# USB kablosu ile bağla
# İlk seferde telefonda yetkilendirme onayı çıkacak
```

### 3. Linux'ta Bağlan
```bash
sudo easysharenet-connect
```

### 4. İnternet Kullan!
```bash
# Test et
curl https://ifconfig.me
ping google.com
```

## 📊 Ekran Görüntüleri

### Android Uygulaması
```
┌─────────────────────────┐
│      🌐                 │
│   EasyShareNet          │
│                         │
│  ┌───────────────────┐  │
│  │ Durum             │  │
│  │ 🟢 Çalışıyor      │  │
│  │                   │  │
│  │ Port: 1080        │  │
│  │ Protokol: SOCKS5  │  │
│  │ Adres: 127.0.0.1  │  │
│  └───────────────────┘  │
│                         │
│    [  DURDUR  ]         │
└─────────────────────────┘
```

### Linux Terminal
```
╔════════════════════════════════════╗
║      EasyShareNet Connector        ║
╚════════════════════════════════════╝

📱 Telefon bağlantısı kontrol ediliyor...
✓ Telefon bulundu: 4P8HWKCQQKEIUCEU
✓ Uygulama yüklü
✓ Port forwarding aktif: localhost:1080
✓ Proxy sunucusu çalışıyor
✓ Firewall kuralları aktif

╔════════════════════════════════════╗
║   ✓ Bağlantı Başarılı!             ║
╚════════════════════════════════════╝

📊 Bağlantı Bilgileri:
  Proxy: SOCKS5://127.0.0.1:1080
  DNS: 8.8.8.8
  Durum: Aktif

⚠️  Bağlantıyı kesmek için: Ctrl+C
```

## 🔧 Sorun Giderme

### Telefon Bulunamıyor
```bash
# ADB cihazları listele
adb devices

# ADB sunucusunu yeniden başlat
adb kill-server
adb start-server
```

### Proxy Bağlantısı Yok
- Android uygulamasının çalıştığından emin olun
- Bildirimlerde "Çalışıyor" yazısını kontrol edin
- Uygulamayı kapatıp tekrar açın

### İnternet Yok
```bash
# DNS kontrol
cat /etc/resolv.conf

# Bağlantı test
nc -zv 127.0.0.1 1080

# iptables kontrol
sudo iptables -t nat -L REDSOCKS
```

### Port Çakışması
```bash
# 1080 portunu kullanan process'i bul
sudo lsof -i :1080

# Process'i durdur
sudo kill -9 <PID>
```

## ⚙️ Gelişmiş Ayarlar

### Farklı Port Kullanma

**Android (ProxyService.kt):**
```kotlin
private val PROXY_PORT = 8080  // Değiştir
```

**Linux:**
```bash
PROXY_PORT=8080 easysharenet-connect
```

### DNS Değiştirme
```bash
# Script içinde DNS_SERVER değişkenini düzenle
DNS_SERVER="1.1.1.1"  # Cloudflare
```

### Otomatik Başlatma
```bash
# Systemd service oluştur
sudo nano /etc/systemd/system/easysharenet.service
```

```ini
[Unit]
Description=EasyShareNet Auto Connect
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/easysharenet-connect
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable easysharenet
sudo systemctl start easysharenet
```

## 🛡️ Güvenlik

- ✅ Tüm trafik localhost üzerinden geçer
- ✅ Root gerektirmez (güvenlik riski yok)
- ✅ Açık kaynak (kod incelenebilir)
- ✅ Şifreleme desteği (HTTPS trafiği korunur)

## ⚠️ Yasal Uyarı

Bu yazılım **eğitim amaçlıdır**. Operatör sözleşmelerini ihlal edebilir ve bazı ülkelerde yasal sorunlara yol açabilir. Kullanım sorumluluğu tamamen kullanıcıya aittir.

**Tavsiye edilen kullanım:**
- Acil durumlarda internet erişimi
- Seyahat sırasında geçici çözüm
- Test ve geliştirme amaçlı

## 📝 Lisans

GPL-3.0 License

## 🤝 Katkıda Bulunma

Pull request'ler memnuniyetle karşılanır!

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing`)
3. Commit edin (`git commit -m 'Add amazing feature'`)
4. Push edin (`git push origin feature/amazing`)
5. Pull Request açın

## 📧 İletişim

- GitHub Issues: Sorun bildirimi
- Discussions: Genel sorular

## 🙏 Teşekkürler

- EasyTether'a ilham için
- Arch Linux topluluğuna
- Açık kaynak katkıcılarına

---

**Made with ❤️ for freedom of internet**
