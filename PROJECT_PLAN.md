# 🌐 EasyShareNet - USB Tethering Bypass

## 📋 Proje Özeti
Operatör hotspot kısıtlamalarını bypass eden USB tethering uygulaması.
Root gerektirmez, basit ve etkili.

## 🎯 Özellikler
- ✅ USB üzerinden internet paylaşımı
- ✅ Operatör algılama bypass
- ✅ Root gerektirmez
- ✅ Tüm portlar açık (HTTPS, Gaming, IM)
- ✅ Arch Linux driver desteği
- ✅ Basit ve hızlı kurulum

## 🏗️ Mimari

### Android Tarafı
```
[Android App]
    ↓
[SOCKS5 Proxy Server] (Port 1080)
    ↓
[USB ADB Bridge]
    ↓
[Mobile Network]
```

### Linux Tarafı
```
[USB Connection]
    ↓
[ADB Port Forward]
    ↓
[SOCKS5 Client]
    ↓
[Network Interface (tun0)]
    ↓
[Internet]
```

## 🔧 Teknik Detaylar

### Bypass Yöntemi
1. **Paket Yeniden Oluşturma**: Tüm paketler telefon uygulaması tarafından oluşturulur
2. **TTL Korunması**: Paketler telefon seviyesinde TTL değerine sahip
3. **User-Agent Masking**: HTTP başlıkları telefon gibi görünür
4. **Tek Kaynak**: Operatör için tek cihaz trafiği

### Bileşenler

#### Android (Kotlin)
- `ProxyService`: SOCKS5 proxy sunucusu
- `UsbManager`: USB bağlantı yönetimi
- `PacketHandler`: Paket işleme ve yeniden oluşturma
- `MainActivity`: Basit UI (Başlat/Durdur)

#### Linux (Python/Bash)
- `easysharenet-connect`: Bağlantı scripti
- `socks5-client`: SOCKS5 istemcisi
- `tun-manager`: Sanal ağ arayüzü yönetimi
- `PKGBUILD`: Arch Linux paketi

## 📦 Kurulum

### Android
1. APK'yı yükle
2. USB debugging aç
3. Uygulamayı başlat

### Arch Linux
```bash
yay -S easysharenet
easysharenet-connect
```

## 🚀 Kullanım
```bash
# Telefonu USB ile bağla
# Android uygulamasını başlat
# Linux'ta:
sudo easysharenet-connect

# İnternet kullanıma hazır!
```

## ⚠️ Yasal Uyarı
Bu uygulama eğitim amaçlıdır. Operatör sözleşmelerini ihlal edebilir.
Kullanım sorumluluğu kullanıcıya aittir.

## 📝 Notlar
- ADB kurulu olmalı
- USB debugging açık olmalı
- Root gerektirmez
- Tüm Linux dağıtımlarında çalışır (Arch için optimize)
