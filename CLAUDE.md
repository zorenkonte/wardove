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
- **Repositories** wrap DAOs/DataStore. `UpdateRepository` calls the GitHub Releases API to check for app updates.

### DI (`di/`)

Hilt with `SingletonComponent`. Three modules: `DatabaseModule` (Room + DAOs), `RepositoryModule` (repositories), `SettingsModule` (DataStore + settings repos).

### UI layer (`ui/`)

MVVM — each screen has a paired `ViewModel` (`@HiltViewModel`). Screens receive only callbacks and state; navigation happens in `WardoveNavHost`.

**Navigation**: `WardoveNavHost` wraps everything in a `ModalNavigationDrawer`. Top-level screens (Wardrobe, Laundry, Calendar, Stats, Settings) navigate via the drawer with `popUpTo(graph.id) { inclusive = true }` to clear the back stack. Detail/sub-screens (AddItem, ItemDetail, History, Settings sub-pages) push onto the stack with slide transitions. Snackbar messages pass back via `savedStateHandle[SNACKBAR_KEY]`.

**Destinations**: All route strings are constants in `WardoveDestinations`.

### App lock

`MainActivity` extends `FragmentActivity` (required by `BiometricPrompt`). On `onStart`, `LockViewModel.lockIfEnabled()` locks the app if the grace period (`LOCK_GRACE_MS = 1s`) since `onStop` has elapsed. The lock overlay is a composable rendered above `WardoveNavHost`.

## Design System

**Fonts**: DM Serif Display (display/headline) + DM Sans (body/labels) via Google Fonts (`ui-text-google-fonts`).

**Colors**: Warm off-white background `#F7F5F2`, white cards, `#1A1A1A` primary. Status colors: clean = `#5DCAA5` (teal), worn = `#EF9F27` (amber), in-laundry = `#7F77DD` (purple).

All screens set `Scaffold(containerColor = Color(0xFFF7F5F2))`. See `WARDOVE_DESIGN_SPEC.md` (co-located in the source tree) for pixel-exact component specs.

`ClothingItem.status` uses string constants from `ClothingStatus` object (`CLEAN`, `WORN`, `IN_LAUNDRY`).

## Key Conventions

- `coreLibraryDesugaringEnabled = true` — `java.time` APIs are available via desugaring (min SDK 24).
- Version catalog is `gradle/libs.versions.toml` — add all new dependencies there, not inline.
- No Retrofit — `UpdateRepository` uses `java.net.HttpURLConnection` directly for the GitHub API call.
