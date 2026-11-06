# MALLUM'S RAHUSA DATA — Native Android Mobile Application

**Tagline:** Affordable Data, Fast Delivery.  
**Platform:** Android (Kotlin & Jetpack Compose)  
**Target SDK:** 35 (Android 15) | **Min SDK:** 24 (Android 7.0+)  
**Architecture:** MVVM + StateFlow + Kotlin Coroutines + Retrofit 2 + Material 3  

---

## 1. Project Overview & Architecture

This repository contains the complete native Android project for **MALLUM'S RAHUSA DATA**, converted from the web application while faithfully preserving:
- **Branding & Colors:** Blue (`#1D4ED8`), Emerald Green (`#059669`), Crisp White (`#FFFFFF`).
- **Core Features:**
  - MTN, Airtel, Glo & 9mobile data subscriptions (SME, CG, Direct Gifting) with Nigerian phone prefix auto-detection.
  - Airtime top-ups with instant 2% discount calculation.
  - Electricity bill payment with meter verification and 20-digit prepaid token generation.
  - Cable TV (DStv, GOtv, StarTimes) with IUC/Smartcard verification.
  - Dedicated virtual bank accounts (Wema Bank, Moniepoint) and automated instant funding.
  - 4-digit Transaction PIN authorization (`PinDialog.kt`).
  - Digital thermal receipts with direct WhatsApp sharing (`ReceiptSheet.kt`).
  - Owner Administrator Dashboard for `abubakarisahmallum@gmail.com` with user wallet adjustment, KPI analytics, and transaction refund.

---

## 2. Directory Structure

```text
/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml             # Version catalog (Compose, Retrofit, Kotlin 2.0+)
└── app/
    ├── build.gradle.kts               # Module build configuration
    ├── proguard-rules.pro             # ProGuard optimization and model preservation
    └── src/
        └── main/
            ├── AndroidManifest.xml    # Permissions, Application, and Theme setup
            ├── res/
            │   ├── values/
            │   │   ├── strings.xml    # App name, currency (₦), tagline
            │   │   ├── colors.xml     # Brand blue, emerald green, white palette
            │   │   └── themes.xml     # Material 3 NoActionBar theme
            │   └── xml/
            │       ├── network_security_config.xml
            │       ├── data_extraction_rules.xml
            │       └── backup_rules.xml
            └── java/com/mallum/rahusadata/
                ├── MainActivity.kt
                ├── ui/
                │   ├── theme/
                │   │   ├── Color.kt
                │   │   ├── Theme.kt
                │   │   └── Type.kt
                │   ├── navigation/
                │   │   └── RahusaNavGraph.kt   # Jetpack Compose Navigation + Bottom Bar
                │   ├── viewmodel/
                │   │   └── RahusaViewModel.kt  # StateFlow ViewModel
                │   ├── components/
                │   │   ├── PinDialog.kt        # 4-Digit PIN modal numpad
                │   │   └── ReceiptSheet.kt     # Bottom sheet receipt + WhatsApp Intent
                │   └── screens/
                │       ├── AuthScreen.kt       # Sign In, Sign Up, 1-Click Demo
                │       ├── DashboardScreen.kt  # Balance card, Quick VTU services
                │       ├── BuyDataScreen.kt    # Network & bundle selector
                │       ├── BuyAirtimeScreen.kt # 2% discount airtime top-up
                │       ├── ElectricityScreen.kt# Meter verification & token
                │       ├── CableScreen.kt      # Bouquet activation
                │       ├── FundWalletScreen.kt # Dedicated virtual accounts
                │       ├── TransactionsScreen.kt# Searchable transaction history
                │       └── AdminScreen.kt      # Owner control center
                └── data/
                    ├── model/Models.kt         # Domain data classes
                    ├── api/
                    │   ├── ApiService.kt       # Retrofit endpoint definitions
                    │   └── ApiClient.kt        # OkHttp client with auth interceptor
                    └── repository/
                        └── RahusaRepository.kt # Coroutine data layer
```

---

## 3. How to Open and Run in Android Studio

1. Open **Android Studio** (Hedgehog, Iguana, or Koala).
2. Choose **File > Open** and select this directory root.
3. Allow Gradle to sync dependencies via `gradle/libs.versions.toml`.
4. Connect an Android device with USB debugging enabled, or launch an Android Virtual Device (AVD).
5. Click **Run** (`Shift + F10`).

---

## 4. Google Play Publication & Testing Readiness

### Step 1: Keystore Generation
Generate a release signing key using `keytool`:

```bash
keytool -genkey -v -keystore rahusa-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias rahusa_key
```

### Step 2: Configure Release Signing in `app/build.gradle.kts`
Place your keystore credentials in `gradle.properties` or environment variables:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("rahusa-release-key.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "password"
        keyAlias = "rahusa_key"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "password"
    }
}
buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

### Step 3: Build Android App Bundle (AAB)
Run the Gradle release task:

```bash
./gradlew bundleRelease
```

The optimized bundle will be generated at:
`app/build/outputs/bundle/release/app-release.aab`

### Step 4: Upload to Google Play Console
1. Log in to your [Google Play Console](https://play.google.com/console).
2. Create an App:
   - **App name:** `MALLUM'S RAHUSA DATA`
   - **Default language:** English
   - **App or game:** App
   - **Free or paid:** Free
3. Navigate to **Testing > Internal testing**.
4. Create a new release and drag & drop `app-release.aab`.
5. Enter release notes (e.g., *Initial release of MALLUM'S RAHUSA DATA for Android*).
6. Add internal tester email addresses (e.g., `abubakarisahmallum@gmail.com`).
7. Save and review the release, then click **Start rollout to Internal testing**.

---

## 5. Security Architecture
- **Biometric Authentication (`androidx.biometric:biometric:1.2.0-alpha05`):**
  - Instant fingerprint and facial recognition login on `AuthScreen.kt` and authorization in `PinDialog.kt`.
  - Enforces `BIOMETRIC_STRONG` with fallback to PIN/password if hardware is unavailable or not enrolled.
  - Managed cleanly via `BiometricAuthManager.kt` using `androidx.fragment.app.FragmentActivity`.
- **Zero Secrets in Client:** No VTU provider credentials (Clubkonnect, VTPass, Monnify, Paystack) are compiled into the Android APK.
- **Server Authorization:** All debit transactions and wallet mutations require the user's 4-digit PIN verified by the secure Express backend (`/server.ts`).
- **TLS Enforced:** Network security config ensures traffic runs over secure HTTPS channels.
