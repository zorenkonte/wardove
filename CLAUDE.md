# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build (APK at app/build/outputs/apk/github/debug/)
./gradlew assembleGithubDebug

# Release build — GitHub-distributed flavor (what CI publishes)
./gradlew assembleGithubRelease

# Release build — Google Play flavor (no self-updater / REQUEST_INSTALL_PACKAGES)
./gradlew assemblePlayRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.app.wardove.ExampleUnitTest"
```

## Architecture

**Single-module Android app** — Kotlin + Jetpack Compose + Material 3 Expressive.

### Distribution flavors

`flavorDimensions = ["distribution"]` with two flavors, same `applicationId`:

- **`github`** (default) — sideloaded APK published on GitHub Releases. `BuildConfig.SELF_UPDATE_ENABLED = true`: the Updates screen downloads and installs APKs, `UpdateCheckWorker` is scheduled, and the manifest requests `REQUEST_INSTALL_PACKAGES`.
- **`play`** — Google Play build. Play policy forbids apps updating themselves outside the store, so `SELF_UPDATE_ENABLED = false`, the worker is cancelled, the Updates screen only shows release notes with a "View on GitHub" link, and `src/play/AndroidManifest.xml` removes `REQUEST_INSTALL_PACKAGES`. Gate any new self-update behaviour on this flag.

### Data layer (`data/`)

- **Room database** (`WardoveDatabase`, version 2) — entities: `ClothingItem`, `WearLog`, `LaundryCycle`, `LaundryCycleItem`. Migrations live in `WardoveDatabase.MIGRATION_X_Y` companion; always add a migration rather than relying on fallback-to-destructive.
- **DAOs**: `ClothingDao`, `WearLogDao`, `LaundryDao` — all return `Flow` for reactive UI.
- **ImageStorage** — saves item photos to `context.filesDir/images/` via FileProvider (`{packageName}.fileprovider`). Always use `ImageStorage` for photo I/O; never write to external storage directly.
- **DataStore** (`AppSettings`) — persists theme mode, dynamic color, app-lock state, and `onboardingCompleted` (first-run tour flag).
- **`ImageStorage.optimize`** — every saved photo (camera, gallery, share-sheet) is downscaled to a 1600px longest edge and re-encoded as JPEG 85 with EXIF rotation baked in. Don't bypass it: full-size camera shots bloat the sandbox and every backup zip.
- Multi-step writes (`ClothingRepository.markWornToday/unwearToday`, `LaundryRepository.startCycle/completeCycle`) run inside `database.withTransaction`; keep new multi-table mutations transactional too.
- **Repositories** wrap DAOs/DataStore. `UpdateRepository` calls the GitHub Releases API to check for app updates. Update detection uses `compareVersions` (numeric semver, top-level in `UpdateViewModel.kt`), not string equality; the "latest" release is chosen by highest version, not most recent publish date.
- **`BackupRepository`** — exports `ClothingItem`/`WearLog`/`LaundryCycle` rows plus item photos into a single zip (via the system file picker), and restores them back in one Room transaction. This is the only way users carry data across a reinstall or device change, since storage is local-only (see below). Screen: `BackupSettingsScreen` + `BackupViewModel`.

> **Storage scope**: all persistence (Room DB, images in `filesDir`, DataStore) is **local to the app sandbox** and is wiped on uninstall. There is no server/cloud backend. See "Data persistence" under Key Conventions before changing this.

### DI (`di/`)

Hilt with `SingletonComponent`. Three modules: `DatabaseModule` (Room + DAOs), `RepositoryModule` (repositories), `SettingsModule` (DataStore + settings repos).

### UI layer (`ui/`)

MVVM — each screen has a paired `ViewModel` (`@HiltViewModel`). Screens receive only callbacks and state; navigation happens in `WardoveNavHost`.

**Navigation**: `WardoveNavHost` wraps everything in a `ModalNavigationDrawer`. Top-level screens (Wardrobe, Laundry, Calendar, Stats, Settings) navigate via the drawer with `popUpTo(graph.findStartDestination().id) { saveState = true }` + `restoreState = true` — so the system back button returns to the start destination (Wardrobe) instead of exiting the app, and each tab keeps its state. A `BackHandler` closes the drawer first when it is open. Detail/sub-screens (AddItem, ItemDetail, History, Settings sub-pages) push onto the stack with slide transitions. Snackbar messages pass back via `savedStateHandle[SNACKBAR_KEY]`. Predictive back is enabled (`android:enableOnBackInvokedCallback="true"` in the manifest).

**Destinations**: All route strings are constants in `WardoveDestinations`.

**Onboarding**: `MainActivity` swaps between `OnboardingScreen` and `WardoveNavHost` based on `AppSettings.onboardingCompleted` (via `AnimatedContent`). The tour is auto-skipped on first launch after upgrading when the wardrobe already has items. The optional notifications step owns the `POST_NOTIFICATIONS` runtime prompt — don't request it at cold start. "Show intro again" in About clears the flag.

**Animations**: Lottie (`lottie-compose`) via `WardoveLottie`, which recolors shapes named `ink`/`paper` to the theme so the JSON in `res/raw/lottie_*.json` works in light, dark and dynamic-color modes. Used by onboarding, the lock screen and empty states.

### App lock

`MainActivity` extends `FragmentActivity` (required by `BiometricPrompt`). On `onStart`, `LockViewModel.lockIfEnabled()` locks the app if the grace period (`LOCK_GRACE_MS = 1s`) since `onStop` has elapsed. The lock overlay (`LockScreen`) is a composable rendered above `WardoveNavHost`; it consumes all pointer events, sends the task to the background on system back, and the content underneath is hidden from accessibility. The unlock prompt allows `BIOMETRIC_WEAK or DEVICE_CREDENTIAL` so users can't be locked out after removing biometrics (enabling the lock still requires a biometric).

## Design System

**Material 3 Expressive**: `WardoveTheme` wraps `MaterialExpressiveTheme` with `MotionScheme.expressive()` and `WardoveShapes`. Prefer the expressive components: `Button(shapes = ButtonDefaults.shapes())` for CTAs, `WardoveLoadingIndicator`/`LoadingBox` (never `CircularProgressIndicator`), `ToggleButton` + `ButtonGroupDefaults.connected*Shapes()` for segmented controls, `LinearWavyProgressIndicator` for stats bars, `MediumExtendedFloatingActionButton` for the wardrobe FAB. Most of these are `@ExperimentalMaterial3ExpressiveApi`.

**Fonts**: DM Serif Display (display/headline) + DM Sans (body/labels) via Google Fonts (`ui-text-google-fonts`). Every `Typography` role is set explicitly in `Type.kt`.

**Colors**: Warm off-white background `#F7F5F2`, white cards, `#1A1A1A` primary. Status colors: clean = `#5DCAA5` (teal), worn = `#EF9F27` (amber), in-laundry = `#7F77DD` (purple).

All screens set `Scaffold(containerColor = MaterialTheme.colorScheme.background)`. `Theme.kt` maps the palette onto the full set of M3 color roles (containers, secondary = teal, tertiary = purple) for light and dark; `values-night/themes.xml` provides the dark window/splash so there is no white flash. Category labels shown to users go through `ClothingOptions.categoryLabel()` (stored keys are English). See `WARDOVE_DESIGN_SPEC.md` (co-located in the source tree) for pixel-exact component specs.

`ClothingItem.status` uses string constants from `ClothingStatus` object (`CLEAN`, `WORN`, `IN_LAUNDRY`).

## Versioning & Release Signing

- **Single source of truth**: `appVersionBase` in `gradle.properties` (e.g. `2.0`) holds the major.minor line. `app/build.gradle.kts` computes `versionName = "$appVersionBase.$buildNumber"` and `versionCode = 100 + buildNumber`, where `buildNumber` is the `-PbuildNumber` Gradle property (the CI run number; defaults to `0` locally). To bump the major/minor, edit `appVersionBase` only — do **not** hardcode versions in the workflow.
- **CI** (`.github/workflows/build.yml`) builds `assembleGithubRelease -PbuildNumber=<run>` and tags the release `v<appVersionBase>.<run>`, so the published tag and the APK's embedded `versionName` always match. The APK is at `app/build/outputs/apk/github/release/app-github-release.apk`.
- **Signing**: releases must be signed with one stable keystore so in-app updates can install over a prior release (a changing key causes "App not installed"). `signingConfigs.release` reads `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` from the environment; CI populates these from repo secrets (`KEYSTORE_BASE64` is decoded to a file). When `KEYSTORE_PATH` is blank/unset (local builds, fork PRs), the release build falls back to the **debug** key.

## Commit Convention

Release notes are generated by [changelogithub](https://github.com/antfu/changelogithub) (config: `changelogithub.config.json`) from **Conventional Commits** on every push to main. Only `feat:`, `fix:`, `perf:`, and `refactor:` commits appear in the GitHub Release body and `docs/changelog.md`; `docs:`, `chore:`, and untyped messages are dropped from the notes. Append `!` for breaking changes (e.g. `feat!:`). Every user-visible change must use a conventional prefix, or it will be invisible in release notes.

## Key Conventions

- `coreLibraryDesugaringEnabled = true` — `java.time` APIs are available via desugaring (min SDK 24).
- `compileSdk = 37.1` (minor API level 1; required by the Compose 1.13 / material3 1.5 alpha line); `targetSdk` is still 36 — bump it separately after testing Android 17 behaviour changes.
- Version catalog is `gradle/libs.versions.toml` — add all new dependencies there, not inline.
- **Compose BOM is `compose-bom-alpha`** (not `compose-bom`): the M3 Expressive components the UI is built on (`ButtonGroup`/`ToggleButton`, `LoadingIndicator`, `LinearWavyProgressIndicator`, `MediumExtendedFloatingActionButton`, `MotionScheme`, `MaterialExpressiveTheme`) are only published in material3 1.5.0 alphas; the stable BOM's material3 1.4.0 keeps them internal. When material3 1.5.0 goes stable, switch the catalog back to `compose-bom` — no code changes should be needed.
- No Retrofit — `UpdateRepository` uses `java.net.HttpURLConnection` directly for the GitHub API call.
- **Data persistence is local-only**: Room, `ImageStorage` (files), and DataStore all live in the app sandbox and are deleted on uninstall. There is no remote/cloud sync. Moving to server-side persistence is a backend project, not a config flag (see the user-facing answer in PR/chat history). The manual zip export/import via `BackupRepository` is the sanctioned way to preserve data across reinstalls — don't reintroduce cloud sync to solve that problem.
