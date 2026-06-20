# Architecture

Wardove is a single-module Android app using **Kotlin + Jetpack Compose + Material 3**.

## Layer overview

```
UI layer  (Compose screens + ViewModels)
    ↕  StateFlow / callback
Repository layer
    ↕
Data layer  (Room DAOs + DataStore + ImageStorage + HttpURLConnection)
```

## Data layer (`data/`)

### Room database

`WardoveDatabase` (version 2) — entities: `ClothingItem`, `WearLog`, `LaundryCycle`, `LaundryCycleItem`. See the [Data Model](./data-model) reference for full schema.

DAOs: `ClothingDao`, `WearLogDao`, `LaundryDao` — all return `Flow` for reactive UI.

### Image storage

`ImageStorage` saves item photos to `context.filesDir/images/`. Served via `FileProvider`. Never write to external storage directly.

### DataStore

`AppSettings` persists: theme mode, dynamic color toggle, app-lock state.

### Repositories

Repositories wrap DAOs and DataStore. `UpdateRepository` calls the GitHub Releases API to check for and download updates (uses `java.net.HttpURLConnection` directly — no Retrofit).

## DI (`di/`)

Hilt with `SingletonComponent`. Three modules:

| Module | Provides |
|--------|----------|
| `DatabaseModule` | Room DB + DAOs |
| `RepositoryModule` | Repositories |
| `SettingsModule` | DataStore + settings repos |

## UI layer (`ui/`)

**MVVM** — each screen has a paired `@HiltViewModel`. Screens receive only state and callbacks; navigation is handled by `WardoveNavHost`.

### Navigation

`WardoveNavHost` wraps everything in a `ModalNavigationDrawer`.

**Top-level screens** (Wardrobe, Laundry, Calendar, Stats, Settings) are accessed via the drawer. Each uses `popUpTo(startDestination) { saveState = true }` + `restoreState = true`, so the back button returns to Wardrobe (not out of the app) and each tab preserves its scroll/filter state. A `BackHandler` closes the drawer first when it's open.

**Detail / sub-screens** (AddItem, ItemDetail, History, Settings sub-pages) push onto the stack with slide transitions.

Snackbar messages pass back via `savedStateHandle[SNACKBAR_KEY]`.

Route string constants live in `WardoveDestinations`.

Predictive back is enabled (`android:enableOnBackInvokedCallback="true"` in the manifest).

### App lock

`MainActivity` extends `FragmentActivity` (required by `BiometricPrompt`). On `onStart`, `LockViewModel.lockIfEnabled()` locks the app if the grace period (1 second since `onStop`) has elapsed. A lock overlay composable renders above `WardoveNavHost`.

## In-app update flow

1. `UpdateRepository.fetchReleases()` — GETs `https://api.github.com/repos/zorenkonte/wardove/releases`.
2. `compareVersions(latestTag, BuildConfig.VERSION_NAME)` determines if an update is available. This is a numeric semver comparison — "latest" is the highest version number, not most recent publish date.
3. `UpdateRepository.enqueueDownload(asset)` — uses `DownloadManager` to download the APK.
4. Progress is polled every 500 ms; on completion a `FileProvider` URI is produced and `ACTION_INSTALL_PACKAGE` is fired.
5. After install, the stale APK file is deleted.

## Design system

- **Fonts**: DM Serif Display (display/headline) + DM Sans (body/labels) — via Google Fonts.
- **Background**: warm off-white `#F7F5F2`; card background white; primary `#1A1A1A`.
- **Status colors**: Clean `#5DCAA5` (teal), Worn `#EF9F27` (amber), In Laundry `#7F77DD` (purple).
- All screens use `Scaffold(containerColor = Color(0xFFF7F5F2))`.

Full pixel-exact specs are in `WARDOVE_DESIGN_SPEC.md` in the source tree.

## Key conventions

- `coreLibraryDesugaringEnabled = true` — `java.time` APIs available (min SDK 24).
- All dependencies declared in `gradle/libs.versions.toml` — no inline version strings.
- Storage is **local-only**: Room, `ImageStorage`, DataStore all live in the app sandbox and are wiped on uninstall. Moving to server-side persistence is a backend project, not a config flag.
