# 🔥 Root Bypass - Hızlı Kullanım

## 📱 Telefonda (Root)

### 1. Script'i Yükle
```bash
adb push easysharenet-bypass.sh /data/local/
adb shell
su
chmod +x /data/local/easysharenet-bypass.sh
```

### 2. Çalıştır
```bash
sh /data/local/easysharenet-bypass.sh
```

### 3. Hotspot Aç
```
Ayarlar → Mobil Hotspot → AÇ
```

## 💻 Bilgisayarda

### Sadece Bağlan!
```
WiFi → Telefon hotspot'una bağlan
```

Hiçbir ayar gerekmez! TTL telefonda düzeltildi.

## ✅ Test

```bash
# Telefonda TTL kontrol
cat /proc/sys/net/ipv4/ip_default_ttl
# Çıktı: 65

# iptables kontrol
iptables -t mangle -L -n -v | grep TTL
```

## 🔄 Otomatik Başlatma

### Magisk Modülü (Önerilen)

1. `easysharenet-module.zip` oluştur
2. Magisk Manager → Modüller → Zip'ten yükle
3. Reboot
4. ✅ Her açılışta otomatik çalışır

### Manuel (Termux)

```bash
# Termux'ta
pkg install termux-boot
mkdir -p ~/.termux/boot
cp easysharenet-bypass.sh ~/.termux/boot/
```

## 🎯 Sonuç

- ✅ TTL = 65 (Bypass)
- ✅ Kernel seviyesi
- ✅ Kalıcı
- ✅ Otomatik
- ✅ %100 etkili

**Artık sınırsız hotspot! 🚀**
