# 🔨 Build Rehberi

## Android APK Build

### Gereksinimler
- JDK 17+
- Android SDK
- Gradle 8.2+

### Build Adımları

#### 1. Debug APK (Test için)
```bash
cd EasyShareNet/android
./gradlew assembleDebug

# APK konumu:
# app/build/outputs/apk/debug/app-debug.apk
```

#### 2. Release APK (Yayın için)
```bash
cd EasyShareNet/android
./gradlew assembleRelease

# APK konumu:
# app/build/outputs/apk/release/app-release.apk
```

#### 3. İmzalı APK (Play Store için)

**Keystore oluştur:**
```bash
keytool -genkey -v -keystore easysharenet.keystore \
  -alias easysharenet -keyalg RSA -keysize 2048 -validity 10000
```

**gradle.properties ekle:**
```properties
KEYSTORE_FILE=../easysharenet.keystore
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=easysharenet
KEY_PASSWORD=your_password
```

**app/build.gradle.kts güncelle:**
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(project.property("KEYSTORE_FILE") as String)
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

**Build:**
```bash
./gradlew assembleRelease
```

### Telefona Yükleme

```bash
# USB ile bağlı telefona
adb install app/build/outputs/apk/release/app-release.apk

# Güncelleme için
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Linux Driver Build

### Arch Linux Paketi

```bash
cd EasyShareNet/linux/PKGBUILD

# Paketi oluştur
makepkg -s

# Paketi kur
sudo pacman -U easysharenet-1.0.0-1-any.pkg.tar.zst

# Veya AUR'a yükle
makepkg --printsrcinfo > .SRCINFO
```

### Manuel Kurulum (Tüm Dağıtımlar)

```bash
# Script'i kopyala
sudo cp linux/scripts/easysharenet-connect /usr/local/bin/
sudo chmod +x /usr/local/bin/easysharenet-connect

# Bağımlılıkları kur
# Arch
sudo pacman -S android-tools redsocks iptables openbsd-netcat

# Debian/Ubuntu
sudo apt install android-tools-adb redsocks iptables netcat

# Fedora
sudo dnf install android-tools redsocks iptables nmap-ncat
```

## GitHub Actions CI/CD

### Android Build Workflow

**.github/workflows/android-build.yml:**
```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x android/gradlew
      
    - name: Build Debug APK
      run: |
        cd android
        ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: android/app/build/outputs/apk/debug/app-debug.apk
```

### Release Workflow

**.github/workflows/release.yml:**
```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build Release APK
      run: |
        cd android
        ./gradlew assembleRelease
    
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: |
          android/app/build/outputs/apk/release/app-release.apk
          linux/scripts/easysharenet-connect
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## Gradle Wrapper Oluşturma

```bash
cd EasyShareNet/android

# Gradle wrapper oluştur
gradle wrapper --gradle-version 8.2

# Dosyalar:
# - gradlew (Linux/Mac)
# - gradlew.bat (Windows)
# - gradle/wrapper/gradle-wrapper.jar
# - gradle/wrapper/gradle-wrapper.properties
```

## Optimizasyon

### ProGuard (APK Boyutu Küçültme)

**app/proguard-rules.pro:**
```proguard
# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }

# Kotlinx
-keep class kotlinx.coroutines.** { *; }

# Compose
-keep class androidx.compose.** { *; }
```

### R8 (Kod Optimizasyonu)

**app/build.gradle.kts:**
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

## Test

### Android Unit Test
```bash
cd android
./gradlew test
```

### Linux Script Test
```bash
# Syntax check
bash -n linux/scripts/easysharenet-connect

# Shellcheck
shellcheck linux/scripts/easysharenet-connect
```

## Sorun Giderme

### Gradle Build Hatası
```bash
# Cache temizle
./gradlew clean

# Gradle daemon durdur
./gradlew --stop

# Tekrar dene
./gradlew assembleDebug
```

### ADB Bulunamıyor
```bash
# Android SDK path ekle
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Bağımlılık Hatası
```bash
# Gradle sync
./gradlew --refresh-dependencies
```

## Versiyon Güncelleme

**app/build.gradle.kts:**
```kotlin
defaultConfig {
    versionCode = 2  // Her release'de artır
    versionName = "1.1.0"  // Semantic versioning
}
```

**PKGBUILD:**
```bash
pkgver=1.1.0
pkgrel=1
```

## Dağıtım

### F-Droid
1. Metadata oluştur
2. F-Droid repo'ya PR gönder
3. İnceleme bekle

### AUR (Arch User Repository)
```bash
# Git repo oluştur
git clone ssh://aur@aur.archlinux.org/easysharenet.git
cd easysharenet

# Dosyaları ekle
cp PKGBUILD .
cp .SRCINFO .

# Commit ve push
git add .
git commit -m "Initial commit"
git push
```

---

**Build başarılı! 🎉**
