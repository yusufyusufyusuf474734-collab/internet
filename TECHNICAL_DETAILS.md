# 🔬 Teknik Detaylar - EasyShareNet

## 🎯 Operatör Bypass Nasıl Çalışır?

### Operatör Algılama Yöntemleri

#### 1. TTL (Time To Live) Kontrolü
```
Normal Telefon Trafiği:
[Telefon] → [İnternet]
TTL: 64 → Operatör görür: 64 ✅

Normal Hotspot:
[Telefon] → [Laptop] → [İnternet]
TTL: 64 → 63 → Operatör görür: 63 ❌ (Tethering algılandı!)

EasyShareNet:
[Telefon App] → [Proxy] → [Mobil Ağ]
TTL: 64 → Operatör görür: 64 ✅ (Normal trafik gibi!)
```

#### 2. User-Agent Kontrolü
```http
Normal Laptop:
User-Agent: Mozilla/5.0 (X11; Linux x86_64) Chrome/120.0 ❌

EasyShareNet:
User-Agent: Dalvik/2.1.0 (Linux; Android 13) ✅
```

#### 3. DPI (Deep Packet Inspection)
```
Normal Hotspot:
- Farklı cihaz imzaları
- Farklı TLS fingerprint'ler
- Farklı DNS sorguları ❌

EasyShareNet:
- Tek kaynak (telefon uygulaması)
- Tutarlı TLS fingerprint
- Tek DNS kaynağı ✅
```

## 🏗️ Mimari Detayları

### Android Tarafı

#### SOCKS5 Proxy Server
```kotlin
// ProxyService.kt içinde

1. TCP Socket Dinle (Port 1080)
   ↓
2. SOCKS5 Handshake
   - Version: 5
   - Auth: No auth (0x00)
   ↓
3. Connection Request
   - CMD: CONNECT (0x01)
   - ATYP: IPv4/Domain
   - DST.ADDR: Hedef adres
   - DST.PORT: Hedef port
   ↓
4. Hedef Sunucuya Bağlan
   - Socket.connect(destAddr, destPort)
   ↓
5. Veri Aktarımı (Relay)
   - Client → Proxy → Destination
   - Destination → Proxy → Client
```

#### Paket Akışı
```
[Linux Client]
    ↓ HTTP Request
[USB ADB Bridge] (Port Forward)
    ↓
[Android SOCKS5 Proxy] (127.0.0.1:1080)
    ↓ Paket Yeniden Oluştur
[Android Network Stack]
    ↓ TTL=64 (Telefon seviyesi)
[Mobile Network]
    ↓
[İnternet]
```

### Linux Tarafı

#### Bağlantı Kurulumu
```bash
# 1. ADB Port Forward
adb forward tcp:1080 tcp:1080
# Telefondaki 1080 portunu bilgisayarın 1080 portuna yönlendir

# 2. Redsocks (Transparent Proxy)
redsocks -c /tmp/redsocks.conf
# Tüm TCP trafiğini SOCKS5'e yönlendir

# 3. iptables (Traffic Redirect)
iptables -t nat -A OUTPUT -p tcp -j REDIRECT --to-ports 12345
# Çıkış trafiğini redsocks'a yönlendir
```

#### Trafik Akışı
```
[Linux App] (curl, firefox, etc.)
    ↓ TCP Request
[iptables NAT]
    ↓ Redirect to 12345
[Redsocks] (127.0.0.1:12345)
    ↓ SOCKS5 Protocol
[Localhost:1080]
    ↓ ADB Forward
[USB Connection]
    ↓
[Android Proxy]
    ↓
[İnternet]
```

## 🔐 Güvenlik Mekanizmaları

### 1. Localhost Binding
```kotlin
// Sadece localhost'tan bağlantı kabul et
serverSocket = aSocket(selectorManager)
    .tcp()
    .bind(InetSocketAddress("127.0.0.1", PROXY_PORT))
```

### 2. Foreground Service
```xml
<!-- Arka planda çalışmaya devam et -->
<service
    android:name=".ProxyService"
    android:foregroundServiceType="specialUse">
```

### 3. Wake Lock
```kotlin
// Uyku modunda bile çalış
val wakeLock = powerManager.newWakeLock(
    PowerManager.PARTIAL_WAKE_LOCK,
    "EasyShareNet::ProxyService"
)
wakeLock.acquire()
```

## ⚡ Performans Optimizasyonları

### 1. Buffer Boyutu
```kotlin
val buffer = ByteBuffer.allocate(8192)  // 8KB optimal
```

### 2. Coroutine Dispatcher
```kotlin
// IO işlemleri için optimize
val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
```

### 3. Connection Pooling
```kotlin
// Her client için ayrı coroutine
serviceScope.launch {
    handleClient(clientSocket)
}
```

## 🧪 Test Senaryoları

### 1. Temel Bağlantı Testi
```bash
# Proxy test
curl -x socks5://127.0.0.1:1080 https://ifconfig.me

# DNS test
nslookup google.com

# Hız testi
speedtest-cli --secure
```

### 2. Port Testi
```bash
# HTTPS (443)
curl -x socks5://127.0.0.1:1080 https://google.com

# SSH (22)
ssh -o ProxyCommand="nc -X 5 -x 127.0.0.1:1080 %h %p" user@server

# Gaming (çeşitli portlar)
nc -X 5 -x 127.0.0.1:1080 game-server.com 27015
```

### 3. Operatör Algılama Testi
```bash
# TTL kontrolü
traceroute -m 1 google.com

# User-Agent kontrolü
curl -x socks5://127.0.0.1:1080 -A "" https://httpbin.org/user-agent
```

## 📊 Performans Metrikleri

### Beklenen Değerler
- **Latency**: +5-15ms (proxy overhead)
- **Throughput**: %90-95 (native hız)
- **CPU Usage**: %2-5 (Android)
- **Battery**: ~3-5% saat başına

### Benchmark
```bash
# Hız testi
speedtest-cli

# Latency testi
ping -c 100 8.8.8.8

# Throughput testi
wget --output-document=/dev/null http://speedtest.tele2.net/100MB.zip
```

## 🐛 Debug Modu

### Android Logcat
```bash
# Tüm loglar
adb logcat | grep EasyShareNet

# Sadece hatalar
adb logcat *:E | grep EasyShareNet

# Proxy trafiği
adb logcat | grep -E "(SOCKS5|Proxy|Connection)"
```

### Linux Debug
```bash
# Redsocks verbose
redsocks -c /tmp/redsocks.conf -p /tmp/redsocks.pid

# iptables trace
iptables -t nat -L REDSOCKS -v -n

# Network monitoring
tcpdump -i any port 1080
```

## 🔄 Alternatif Implementasyonlar

### 1. VPN Tabanlı (Root Gerektirmez)
```kotlin
// VpnService kullan
class VpnProxyService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = Builder()
            .addAddress("10.0.0.2", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
        
        val vpnInterface = builder.establish()
        // Paketleri yakala ve proxy'e yönlendir
    }
}
```

### 2. Root Tabanlı (Daha Etkili)
```bash
# TTL değerini değiştir
echo "65" > /proc/sys/net/ipv4/ip_default_ttl

# iptables ile mangle
iptables -t mangle -A POSTROUTING -j TTL --ttl-set 65
```

### 3. Bluetooth Desteği
```kotlin
// BluetoothServerSocket ile dinle
val serverSocket = bluetoothAdapter
    .listenUsingRfcommWithServiceRecord("EasyShareNet", uuid)
```

## 📈 Gelecek Geliştirmeler

- [ ] GUI için Electron/Tauri desktop app
- [ ] Windows driver desteği
- [ ] macOS driver desteği
- [ ] Bluetooth tethering
- [ ] Otomatik bağlantı
- [ ] Trafik istatistikleri
- [ ] Bandwidth limitleme
- [ ] Multi-device desteği

## 🔗 Kaynaklar

- [SOCKS5 RFC](https://www.rfc-editor.org/rfc/rfc1928)
- [Android VpnService](https://developer.android.com/reference/android/net/VpnService)
- [Redsocks GitHub](https://github.com/darkk/redsocks)
- [iptables Tutorial](https://www.netfilter.org/documentation/)

---

**Teknik sorularınız için GitHub Issues kullanın!**
