# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build (APK at app/build/outputs/apk/debug/)
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.app.wardove.ExampleUnitTest"
```

## Architecture

**Single-module Android app** — Kotlin + Jetpack Compose + Material 3.

### Data layer (`data/`)

- **Room database** (`WardoveDatabase`, version 2) — entities: `ClothingItem`, `WearLog`, `LaundryCycle`, `LaundryCycleItem`. Migrations live in `WardoveDatabase.MIGRATION_X_Y` companion; always add a migration rather than relying on fallback-to-destructive.
- **DAOs**: `ClothingDao`, `WearLogDao`, `LaundryDao` — all return `Flow` for reactive UI.
- **ImageStorage** — saves item photos to `context.filesDir/images/` via FileProvider (`{packageName}.fileprovider`). Always use `ImageStorage` for photo I/O; never write to external storage directly.
- **DataStore** (`AppSettings`) — persists theme mode, dynamic color, app-lock state.
- **Repositories** wrap DAOs/DataStore. `UpdateRepository` calls the GitHub Releases API to check for app updates. Update detection uses `compareVersions` (numeric semver, top-level in `UpdateViewModel.kt`), not string equality; the "latest" release is chosen by highest version, not most recent publish date.

> **Storage scope**: all persistence (Room DB, images in `filesDir`, DataStore) is **local to the app sandbox** and is wiped on uninstall. There is no server/cloud backend. See "Data persistence" under Key Conventions before changing this.

### DI (`di/`)

Hilt with `SingletonComponent`. Three modules: `DatabaseModule` (Room + DAOs), `RepositoryModule` (repositories), `SettingsModule` (DataStore + settings repos).

### UI layer (`ui/`)

MVVM — each screen has a paired `ViewModel` (`@HiltViewModel`). Screens receive only callbacks and state; navigation happens in `WardoveNavHost`.

**Navigation**: `WardoveNavHost` wraps everything in a `ModalNavigationDrawer`. Top-level screens (Wardrobe, Laundry, Calendar, Stats, Settings) navigate via the drawer with `popUpTo(graph.findStartDestination().id) { saveState = true }` + `restoreState = true` — so the system back button returns to the start destination (Wardrobe) instead of exiting the app, and each tab keeps its state. A `BackHandler` closes the drawer first when it is open. Detail/sub-screens (AddItem, ItemDetail, History, Settings sub-pages) push onto the stack with slide transitions. Snackbar messages pass back via `savedStateHandle[SNACKBAR_KEY]`. Predictive back is enabled (`android:enableOnBackInvokedCallback="true"` in the manifest).

**Destinations**: All route strings are constants in `WardoveDestinations`.

### App lock

`MainActivity` extends `FragmentActivity` (required by `BiometricPrompt`). On `onStart`, `LockViewModel.lockIfEnabled()` locks the app if the grace period (`LOCK_GRACE_MS = 1s`) since `onStop` has elapsed. The lock overlay is a composable rendered above `WardoveNavHost`.

## Design System

**Fonts**: DM Serif Display (display/headline) + DM Sans (body/labels) via Google Fonts (`ui-text-google-fonts`).

**Colors**: Warm off-white background `#F7F5F2`, white cards, `#1A1A1A` primary. Status colors: clean = `#5DCAA5` (teal), worn = `#EF9F27` (amber), in-laundry = `#7F77DD` (purple).

All screens set `Scaffold(containerColor = Color(0xFFF7F5F2))`. See `WARDOVE_DESIGN_SPEC.md` (co-located in the source tree) for pixel-exact component specs.

`ClothingItem.status` uses string constants from `ClothingStatus` object (`CLEAN`, `WORN`, `IN_LAUNDRY`).

## Versioning & Release Signing

- **Single source of truth**: `appVersionBase` in `gradle.properties` (e.g. `2.0`) holds the major.minor line. `app/build.gradle.kts` computes `versionName = "$appVersionBase.$buildNumber"` and `versionCode = 100 + buildNumber`, where `buildNumber` is the `-PbuildNumber` Gradle property (the CI run number; defaults to `0` locally). To bump the major/minor, edit `appVersionBase` only — do **not** hardcode versions in the workflow.
- **CI** (`.github/workflows/build.yml`) builds `assembleRelease -PbuildNumber=<run>` and tags the release `v<appVersionBase>.<run>`, so the published tag and the APK's embedded `versionName` always match. The APK is at `app/build/outputs/apk/release/app-release.apk`.
- **Signing**: releases must be signed with one stable keystore so in-app updates can install over a prior release (a changing key causes "App not installed"). `signingConfigs.release` reads `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` from the environment; CI populates these from repo secrets (`KEYSTORE_BASE64` is decoded to a file). When `KEYSTORE_PATH` is blank/unset (local builds, fork PRs), the release build falls back to the **debug** key.

## Key Conventions

- `coreLibraryDesugaringEnabled = true` — `java.time` APIs are available via desugaring (min SDK 24).
- Version catalog is `gradle/libs.versions.toml` — add all new dependencies there, not inline.
- No Retrofit — `UpdateRepository` uses `java.net.HttpURLConnection` directly for the GitHub API call.
- **Data persistence is local-only**: Room, `ImageStorage` (files), and DataStore all live in the app sandbox and are deleted on uninstall. There is no remote/cloud sync. Moving to server-side persistence is a backend project, not a config flag (see the user-facing answer in PR/chat history).
