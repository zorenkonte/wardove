# Wardove
A personal wardrobe tracker for Android.

## Features
- 👕 Track your clothes with photos and tags
- 📅 Log what you wear each day
- 🧺 Manage your laundry cycle
- 📊 Stats — most worn items, cost per wear
- 🔍 Search, sort, and filter your wardrobe

## Tech Stack
- Kotlin + Jetpack Compose
- Material 3
- Room, Hilt, Navigation Compose, CameraX, Coil
- Min SDK 24 (Android 7.0)

## Build
Clone the repo and open in Android Studio, or:
```bash
./gradlew assembleDebug
```
APK output: `app/build/outputs/apk/debug/`

## Automated Builds
Every push to main triggers a GitHub Actions build.
Download the latest APK from the Actions tab → most recent run → Artifacts.
