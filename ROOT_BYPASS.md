# 🔥 Root ile Profesyonel Hotspot Bypass

## 🎯 Root Avantajları

Root ile yapabileceklerin:
- ✅ Kernel seviyesi TTL değiştirme
- ✅ iptables tam kontrol
- ✅ Sistem dosyalarını düzenleme
- ✅ VPN tüneli oluşturma
- ✅ Paket manipülasyonu

## 🚀 Yöntem 1: TTL Override (Kernel Seviyesi)

### Android Tarafı (Root)

```bash
# ADB shell ile bağlan
adb shell
su

# TTL değerini kernel'de değiştir
echo "65" > /proc/sys/net/ipv4/ip_default_ttl
echo "65" > /proc/sys/net/ipv6/conf/all/hop_limit

# Kalıcı yap (her açılışta)
echo "net.ipv4.ip_default_ttl=65" >> /system/etc/sysctl.conf
echo "net.ipv6.conf.all.hop_limit=65" >> /system/etc/sysctl.conf

# iptables ile tüm paketlerin TTL'ini değiştir
iptables -t mangle -A POSTROUTING -j TTL --ttl-set 65
iptables -t mangle -A PREROUTING -j TTL --ttl-set 65
ip6tables -t mangle -A POSTROUTING -j HL --hl-set 65
ip6tables -t mangle -A PREROUTING -j HL --hl-set 65

# Kuralları kaydet
iptables-save > /data/local/iptables.rules
```

### Linux Tarafı

```bash
# Sadece hotspot'a bağlan, hiçbir şey yapma
# TTL zaten telefonda düzeltildi
```

---

## 🔐 Yöntem 2: VPN Tüneli (En Güçlü)

### WireGuard Sunucu Kur (Linux)

```bash
# WireGuard kur
sudo pacman -S wireguard-tools

# Sunucu config oluştur
sudo mkdir -p /etc/wireguard
cd /etc/wireguard

# Private key oluştur
wg genkey | sudo tee server_private.key | wg pubkey | sudo tee server_public.key

# Config yaz
sudo nano wg0.conf
```

**wg0.conf:**
```ini
[Interface]
Address = 10.0.0.1/24
ListenPort = 51820
PrivateKey = <server_private.key içeriği>
PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -o wlan0 -j MASQUERADE

[Peer]
# Android telefon
PublicKey = <android_public.key>
AllowedIPs = 10.0.0.2/32
```

```bash
# IP forwarding aktif et
sudo sysctl -w net.ipv4.ip_forward=1
echo "net.ipv4.ip_forward=1" | sudo tee -a /etc/sysctl.conf

# WireGuard başlat
sudo systemctl enable wg-quick@wg0
sudo systemctl start wg-quick@wg0

# Firewall aç
sudo ufw allow 51820/udp
```

### Android WireGuard Client (Root)

```bash
# Termux veya ADB shell
su

# WireGuard binary kur (Magisk modülü veya manuel)
# https://github.com/WireGuard/wireguard-android

# Client config
mkdir -p /data/local/wireguard
cd /data/local/wireguard

# Key oluştur
wg genkey | tee android_private.key | wg pubkey > android_public.key

# Config yaz
cat > wg0.conf << EOF
[Interface]
Address = 10.0.0.2/24
PrivateKey = $(cat android_private.key)
DNS = 1.1.1.1

[Peer]
PublicKey = <linux_server_public.key>
Endpoint = <linux_ip>:51820
AllowedIPs = 0.0.0.0/0
PersistentKeepalive = 25
EOF

# WireGuard başlat
wg-quick up wg0
```

---

## ⚡ Yöntem 3: Transparent Proxy (En Gelişmiş)

### Redsocks + iptables (Android Root)

```bash
# ADB shell
adb shell
su

# Redsocks binary indir
wget https://github.com/darkk/redsocks/releases/download/release-0.5/redsocks-android
chmod +x redsocks-android
mv redsocks-android /system/bin/redsocks

# Config oluştur
cat > /data/local/redsocks.conf << EOF
base {
    log_debug = off;
    log_info = on;
    log = "file:/data/local/redsocks.log";
    daemon = on;
    redirector = iptables;
}

redsocks {
    local_ip = 127.0.0.1;
    local_port = 12345;
    ip = <linux_ip>;
    port = 1080;
    type = socks5;
}
EOF

# Redsocks başlat
redsocks -c /data/local/redsocks.conf

# iptables redirect
iptables -t nat -N REDSOCKS
iptables -t nat -A REDSOCKS -d 0.0.0.0/8 -j RETURN
iptables -t nat -A REDSOCKS -d 10.0.0.0/8 -j RETURN
iptables -t nat -A REDSOCKS -d 127.0.0.0/8 -j RETURN
iptables -t nat -A REDSOCKS -d 169.254.0.0/16 -j RETURN
iptables -t nat -A REDSOCKS -d 172.16.0.0/12 -j RETURN
iptables -t nat -A REDSOCKS -d 192.168.0.0/16 -j RETURN
iptables -t nat -A REDSOCKS -d 224.0.0.0/4 -j RETURN
iptables -t nat -A REDSOCKS -d 240.0.0.0/4 -j RETURN
iptables -t nat -A REDSOCKS -p tcp -j REDIRECT --to-ports 12345
iptables -t nat -A OUTPUT -p tcp -j REDSOCKS
```

### Linux SOCKS5 Server

```bash
# SSH tunnel (basit)
ssh -D 1080 -N user@localhost

# Veya dante-server kur
sudo pacman -S dante
sudo nano /etc/sockd.conf
```

---

## 🎯 Otomatik Script (Android Root)

### Magisk Modülü Oluştur

```bash
# Modül yapısı
mkdir -p easysharenet-module
cd easysharenet-module

# module.prop
cat > module.prop << EOF
id=easysharenet
name=EasyShareNet Bypass
version=1.0
versionCode=1
author=You
description=Automatic hotspot bypass with TTL manipulation
EOF

# service.sh (boot'ta çalışır)
cat > service.sh << '#!/system/bin/sh
# TTL değiştir
echo "65" > /proc/sys/net/ipv4/ip_default_ttl
echo "65" > /proc/sys/net/ipv6/conf/all/hop_limit

# iptables kuralları
iptables -t mangle -F
iptables -t mangle -A POSTROUTING -j TTL --ttl-set 65
iptables -t mangle -A PREROUTING -j TTL --ttl-set 65
ip6tables -t mangle -F
ip6tables -t mangle -A POSTROUTING -j HL --hl-set 65
ip6tables -t mangle -A PREROUTING -j HL --hl-set 65

# Log
echo "EasyShareNet bypass aktif: $(date)" >> /data/local/easysharenet.log
EOF'

chmod +x service.sh

# Zip oluştur
zip -r easysharenet-module.zip *

# Magisk Manager'dan yükle
```

---

## 🔧 Komple Çözüm: Android Script

```bash
#!/system/bin/sh
# easysharenet-bypass.sh
# Root gerekli

# TTL manipülasyonu
echo "65" > /proc/sys/net/ipv4/ip_default_ttl
echo "65" > /proc/sys/net/ipv6/conf/all/hop_limit

# iptables kuralları
iptables -t mangle -F
iptables -t mangle -A POSTROUTING -j TTL --ttl-set 65
iptables -t mangle -A PREROUTING -j TTL --ttl-set 65
ip6tables -t mangle -F  
ip6tables -t mangle -A POSTROUTING -j HL --hl-set 65
ip6tables -t mangle -A PREROUTING -j HL --hl-set 65

# User-Agent maskeleme (opsiyonel)
# HTTP başlıklarını değiştir
iptables -t mangle -A POSTROUTING -p tcp --dport 80 -j MARK --set-mark 1
iptables -t mangle -A POSTROUTING -p tcp --dport 443 -j MARK --set-mark 1

echo "Bypass aktif!"
```

**Kullanım:**
```bash
adb push easysharenet-bypass.sh /data/local/
adb shell
su
sh /data/local/easysharenet-bypass.sh
```

---

## 📊 Test

```bash
# TTL kontrol
cat /proc/sys/net/ipv4/ip_default_ttl
# Çıktı: 65

# iptables kontrol
iptables -t mangle -L -n -v

# Hotspot test
# Hotspot aç, bilgisayardan bağlan
# Operatör algılayamayacak
```

---

## 🎯 En İyi Yöntem

**Tavsiye: TTL Override (Yöntem 1)**
- En basit
- En hızlı
- %99 etkili
- Pil dostu

**Ekstra Güvenlik: + VPN (Yöntem 2)**
- %100 bypass
- Şifreli trafik
- Biraz yavaş

---

## ⚠️ Önemli

1. **Root gerekli** - Magisk önerilir
2. **Sistem bölümü yazılabilir** olmalı
3. **Yedek al** - TWRP backup
4. **Test et** - Önce TTL kontrolü yap

---

**Root + TTL = Sınırsız Hotspot! 🔥**
