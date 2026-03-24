# Kavach SecureComm - Android App

This is the Android client for the Kavach SecureComm ecosystem.

## Architecture
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Security**: 
  - Android Keystore (Curve25519)
  - Signal Protocol for E2E Encrypted Messaging
  - Biometric Prompt integration.
- **Data Layers**: 
  - Room DB with SQLCipher (Encrypted Database)

## Directory Structure
```
app/src/main/java/com/kavachsecurecomm/
├── ui/              # Compose screens (Login, Chat, Dial, Setup)
├── viewmodel/       # UI Logic
├── data/            # Room DAOs, DataStore
├── network/         # WebSocket Client, REST API
├── crypto/          # Signal Protocol integration, Keystore Wrapper
└── security/        # Biometrics, AppLock, Anomaly Handling
```

## Security Requirements
- Minimum SDK: API 26 (Android 8.0)
- Target SDK: API 34 (Android 14)
- ProGuard rules strict mapping.
