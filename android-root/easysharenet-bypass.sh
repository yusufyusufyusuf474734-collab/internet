#!/system/bin/sh
# EasyShareNet Root Bypass Script
# Telefonda root ile çalıştır

echo "🔥 EasyShareNet Root Bypass"
echo "=========================="

# Root kontrolü
if [ "$(id -u)" != "0" ]; then
   echo "❌ Root gerekli!"
   exit 1
fi

echo "✓ Root erişimi var"

# TTL değiştir (kernel seviyesi)
echo "🔧 TTL ayarlanıyor..."
echo "65" > /proc/sys/net/ipv4/ip_default_ttl
echo "65" > /proc/sys/net/ipv6/conf/all/hop_limit

# Kalıcı yap
if [ -w /system/etc/sysctl.conf ]; then
    grep -q "ip_default_ttl" /system/etc/sysctl.conf || echo "net.ipv4.ip_default_ttl=65" >> /system/etc/sysctl.conf
    grep -q "hop_limit" /system/etc/sysctl.conf || echo "net.ipv6.conf.all.hop_limit=65" >> /system/etc/sysctl.conf
fi

echo "✓ TTL = 65"

# iptables kuralları
echo "🔥 iptables kuralları uygulanıyor..."

# Eski kuralları temizle
iptables -t mangle -F 2>/dev/null
ip6tables -t mangle -F 2>/dev/null

# TTL manipülasyonu
iptables -t mangle -A POSTROUTING -j TTL --ttl-set 65
iptables -t mangle -A PREROUTING -j TTL --ttl-set 65
ip6tables -t mangle -A POSTROUTING -j HL --hl-set 65
ip6tables -t mangle -A PREROUTING -j HL --hl-set 65

echo "✓ iptables kuralları aktif"

# Kuralları kaydet
if [ -d /data/local ]; then
    iptables-save > /data/local/iptables.rules 2>/dev/null
    echo "✓ Kurallar kaydedildi"
fi

echo ""
echo "✅ Bypass aktif!"
echo ""
echo "📊 Durum:"
echo "  TTL: $(cat /proc/sys/net/ipv4/ip_default_ttl)"
echo "  iptables: Aktif"
echo ""
echo "🎯 Artık hotspot kullanabilirsin!"
echo "   Operatör algılayamayacak."
echo ""

# Log
echo "$(date): Bypass aktif" >> /data/local/easysharenet.log
