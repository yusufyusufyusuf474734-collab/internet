# 🚀 EasyShareNet - Hızlı Kullanım Rehberi

## ✅ Kurulum Tamamlandı!

Script bilgisayarınıza kuruldu: `/usr/local/bin/easysharenet`

## 📱 Adım 1: Android APK'yı Yükle

GitHub Actions'dan APK'yı indir:
https://github.com/yusufyusufyusuf474734-collab/internet/actions

Veya bekle, build tamamlanınca hazır olacak.

## 🔌 Adım 2: Telefonu Bağla

```bash
# USB kablosu ile telefonu bağla
# USB debugging açık olmalı
adb devices
```

## 📲 Adım 3: APK'yı Yükle

```bash
adb install app-debug.apk
```

## ▶️ Adım 4: Android Uygulamasını Başlat

1. Telefonda "EasyShareNet" uygulamasını aç
2. "BAŞLAT" butonuna bas
3. Bildirimde "🟢 Çalışıyor" yazısını gör

## 💻 Adım 5: Bilgisayarda Bağlan

```bash
easysharenet
```

## 🌐 Adım 6: İnternet Kullan!

### Firefox
1. Ayarlar → Network Settings → Manual proxy
2. SOCKS Host: `127.0.0.1`
3. Port: `1080`
4. SOCKS v5 seç

### Chrome/Chromium
```bash
chromium --proxy-server="socks5://127.0.0.1:1080"
```

### Terminal (curl, wget, git, etc)
```bash
export ALL_PROXY=socks5://127.0.0.1:1080
curl https://ifconfig.me
```

## 🧪 Test Et

```bash
# Proxy üzerinden IP kontrolü
curl --socks5 127.0.0.1:1080 https://ifconfig.me

# Hız testi
speedtest-cli --secure
```

## 🛑 Durdurma

Script çalışırken: `Ctrl+C`

## 🔧 Sorun Giderme

### Telefon Bulunamıyor
```bash
adb kill-server
adb start-server
adb devices
```

### Proxy Bağlantısı Yok
- Android uygulamasının çalıştığını kontrol et
- Uygulamayı kapat ve tekrar aç

### Port Çakışması
```bash
# 1080 portunu kullanan process'i bul
sudo lsof -i :1080
# Process'i durdur
sudo kill -9 <PID>
```

## 📝 Notlar

- Root gerektirmez
- Tüm portlar açık (HTTPS, Gaming, IM)
- Operatör bypass aktif
- Basit ve hızlı

## ⚠️ Yasal Uyarı

Bu yazılım eğitim amaçlıdır. Operatör sözleşmelerini ihlal edebilir.
Kullanım sorumluluğu kullanıcıya aittir.

---

**Hazır! İnterneti kullanmaya başla! 🎉**
