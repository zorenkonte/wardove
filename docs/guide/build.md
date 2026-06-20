# Build from Source

## Prerequisites

- Android Studio (Hedgehog or newer) **or** a JDK 17+ install with the Android SDK.
- Gradle wrapper is included — no separate Gradle install needed.

## Clone and build

```bash
git clone https://github.com/zorenkonte/wardove.git
cd wardove
./gradlew assembleDebug
```

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Useful tasks

```bash
# Release build (unsigned / debug-key locally if KEYSTORE_PATH is unset)
./gradlew assembleRelease

# Unit tests
./gradlew test

# Instrumented tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Single test class
./gradlew test --tests "com.app.wardove.ExampleUnitTest"
```

## Versioning

Version is composed from two sources:

| Source | What it controls |
|--------|-----------------|
| `appVersionBase` in `gradle.properties` | Major.minor line (e.g. `2.0`) |
| `-PbuildNumber` Gradle property | Patch — the CI run number; defaults to `0` locally |

`versionName = "$appVersionBase.$buildNumber"` (e.g. `2.0.25`)  
`versionCode = 100 + buildNumber`

To bump the major or minor version, edit `appVersionBase` in `gradle.properties` only. Never hardcode version strings elsewhere.

## CI / automated builds

Every push to `main` triggers `.github/workflows/build.yml`:

1. Builds `assembleRelease -PbuildNumber=<run_number>`.
2. Signs with the stable keystore stored in repo secrets (`KEYSTORE_BASE64`).
3. Creates a GitHub Release tagged `v<appVersionBase>.<run>` with the signed APK attached.

The in-app update checker reads these releases.

## Signing

Release builds use a stable keystore so in-app updates can install over prior releases (mismatched keys cause "App not installed").

`signingConfigs.release` reads four env vars:

| Variable | Source |
|----------|--------|
| `KEYSTORE_PATH` | Path to the `.jks` file (CI decodes `KEYSTORE_BASE64` to a temp file) |
| `KEYSTORE_PASSWORD` | Repo secret |
| `KEY_ALIAS` | Repo secret |
| `KEY_PASSWORD` | Repo secret |

When `KEYSTORE_PATH` is blank or unset (local builds, fork PRs), the build falls back to the **debug** key automatically.
