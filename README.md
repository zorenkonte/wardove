# Wardove
A personal wardrobe tracker for Android.

## Features
- 👕 Track your clothes with photos and tags
- 📅 Log what you wear each day
- 🧺 Manage your laundry cycle
- 📊 Stats — most worn items, cost per wear
- 🔍 Search, sort, and filter your wardrobe

- 🔒 Private by design — local-only storage, optional biometric lock, zip backup/restore

## Tech Stack
- Kotlin + Jetpack Compose
- Material 3 Expressive
- Room, Hilt, Navigation Compose, CameraX, Coil, Lottie
- Min SDK 24 (Android 7.0)

## Build
Clone the repo and open in Android Studio, or:
```bash
./gradlew assembleGithubDebug
```
APK output: `app/build/outputs/apk/github/debug/`

Two distribution flavors share one `applicationId`:

| Flavor | Use | Self-updater |
|---|---|---|
| `github` (default) | Sideloaded APK from GitHub Releases | Yes — downloads and installs new releases in-app |
| `play` | Google Play listing | No — Play policy forbids self-updating; the Updates screen only shows release notes |

```bash
./gradlew assemblePlayRelease   # Play Store build
```

## Automated Builds
Every push to main triggers a GitHub Actions build.
Download the latest APK from the Actions tab → most recent run → Artifacts.
