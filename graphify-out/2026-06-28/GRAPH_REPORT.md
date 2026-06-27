# Graph Report - wardove  (2026-06-28)

## Corpus Check
- 110 files · ~58,152 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1012 nodes · 1364 edges · 90 communities (62 shown, 28 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 86 edges (avg confidence: 0.82)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `cbd85215`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Documentation & Feature Docs|Documentation & Feature Docs]]
- [[_COMMUNITY_Settings UI & Navigation|Settings UI & Navigation]]
- [[_COMMUNITY_ViewModel State Management|ViewModel State Management]]
- [[_COMMUNITY_App Update Screen|App Update Screen]]
- [[_COMMUNITY_Diagnostics & Update Delivery|Diagnostics & Update Delivery]]
- [[_COMMUNITY_App Entry & Theme|App Entry & Theme]]
- [[_COMMUNITY_Add Item ViewModel|Add Item ViewModel]]
- [[_COMMUNITY_Stats & Analytics UI|Stats & Analytics UI]]
- [[_COMMUNITY_Clothing Data Access|Clothing Data Access]]
- [[_COMMUNITY_Update Repository & GitHub API|Update Repository & GitHub API]]
- [[_COMMUNITY_Laundry Screen UI|Laundry Screen UI]]
- [[_COMMUNITY_Add Item Screen|Add Item Screen]]
- [[_COMMUNITY_Clothing Repository|Clothing Repository]]
- [[_COMMUNITY_Laundry ViewModel|Laundry ViewModel]]
- [[_COMMUNITY_Laundry Data Access|Laundry Data Access]]
- [[_COMMUNITY_Wardrobe Screen UI|Wardrobe Screen UI]]
- [[_COMMUNITY_Laundry Repository|Laundry Repository]]
- [[_COMMUNITY_Item Detail Screen|Item Detail Screen]]
- [[_COMMUNITY_Wear Log Data Access|Wear Log Data Access]]
- [[_COMMUNITY_App Initialization & Logging|App Initialization & Logging]]
- [[_COMMUNITY_Settings Repository|Settings Repository]]
- [[_COMMUNITY_Item Detail ViewModel|Item Detail ViewModel]]
- [[_COMMUNITY_Room Database Schema|Room Database Schema]]
- [[_COMMUNITY_Database DI Module|Database DI Module]]
- [[_COMMUNITY_Calendar ViewModel|Calendar ViewModel]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Wardrobe ViewModel|Wardrobe ViewModel]]
- [[_COMMUNITY_File Logging|File Logging]]
- [[_COMMUNITY_Navigation Destinations|Navigation Destinations]]
- [[_COMMUNITY_Image Storage|Image Storage]]
- [[_COMMUNITY_Docs Build Config|Docs Build Config]]
- [[_COMMUNITY_Settings DI Module|Settings DI Module]]
- [[_COMMUNITY_Calendar Repository|Calendar Repository]]
- [[_COMMUNITY_Design System & Theme|Design System & Theme]]
- [[_COMMUNITY_App Lock Repository|App Lock Repository]]
- [[_COMMUNITY_Date Formatting Utils|Date Formatting Utils]]
- [[_COMMUNITY_Clothing Item Entity|Clothing Item Entity]]
- [[_COMMUNITY_GitHub Release Model|GitHub Release Model]]
- [[_COMMUNITY_Instrumented Tests|Instrumented Tests]]
- [[_COMMUNITY_Unit Tests|Unit Tests]]
- [[_COMMUNITY_WearLog Join Query|WearLog Join Query]]
- [[_COMMUNITY_Repository DI Module|Repository DI Module]]
- [[_COMMUNITY_Docs Home & Deployment|Docs Home & Deployment]]
- [[_COMMUNITY_Splash Screen Asset|Splash Screen Asset]]
- [[_COMMUNITY_Laundry Cycle Entity|Laundry Cycle Entity]]
- [[_COMMUNITY_Laundry Cycle Item Entity|Laundry Cycle Item Entity]]
- [[_COMMUNITY_Wear Log Entity|Wear Log Entity]]
- [[_COMMUNITY_App Settings DataStore|App Settings DataStore]]
- [[_COMMUNITY_Theme Mode Enum|Theme Mode Enum]]
- [[_COMMUNITY_App Icon Drawable|App Icon Drawable]]
- [[_COMMUNITY_Launcher Icon hdpi|Launcher Icon hdpi]]
- [[_COMMUNITY_Launcher Round Icon hdpi|Launcher Round Icon hdpi]]
- [[_COMMUNITY_Launcher Icon mdpi|Launcher Icon mdpi]]
- [[_COMMUNITY_Launcher Round Icon mdpi|Launcher Round Icon mdpi]]
- [[_COMMUNITY_Launcher Icon xhdpi|Launcher Icon xhdpi]]
- [[_COMMUNITY_Launcher Round Icon xhdpi|Launcher Round Icon xhdpi]]
- [[_COMMUNITY_Launcher Icon xxhdpi|Launcher Icon xxhdpi]]
- [[_COMMUNITY_Launcher Round Icon xxhdpi|Launcher Round Icon xxhdpi]]
- [[_COMMUNITY_Launcher Icon xxxhdpi|Launcher Icon xxxhdpi]]
- [[_COMMUNITY_Launcher Round Icon xxxhdpi|Launcher Round Icon xxxhdpi]]
- [[_COMMUNITY_Docs Logo Asset|Docs Logo Asset]]
- [[_COMMUNITY_Desugaring Rationale|Desugaring Rationale]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]
- [[_COMMUNITY_Community 79|Community 79]]
- [[_COMMUNITY_Community 80|Community 80]]
- [[_COMMUNITY_Community 81|Community 81]]
- [[_COMMUNITY_Community 82|Community 82]]
- [[_COMMUNITY_Community 83|Community 83]]
- [[_COMMUNITY_Community 84|Community 84]]
- [[_COMMUNITY_Community 85|Community 85]]
- [[_COMMUNITY_Community 86|Community 86]]
- [[_COMMUNITY_Community 87|Community 87]]
- [[_COMMUNITY_Community 88|Community 88]]
- [[_COMMUNITY_Community 90|Community 90]]

## God Nodes (most connected - your core abstractions)
1. `AddItemViewModel` - 21 edges
2. `WardoveNavHost()` - 21 edges
3. `LaundryViewModel` - 20 edges
4. `Changelog` - 18 edges
5. `MainActivity` - 17 edges
6. `ClothingDao` - 15 edges
7. `ClothingImage()` - 15 edges
8. `ClothingRepository` - 14 edges
9. `ShareItemViewModel` - 14 edges
10. `WardrobeViewModel` - 13 edges

## Surprising Connections (you probably didn't know these)
- `Auto-Update System via GitHub Releases (v1.0.10)` --semantically_similar_to--> `UpdateRepository (GitHub Releases API client)`  [INFERRED] [semantically similar]
  docs/changelog.md → CLAUDE.md
- `In-App Update Flow (GitHub Releases sideload)` --semantically_similar_to--> `UpdateRepository (GitHub Releases API client)`  [INFERRED] [semantically similar]
  docs/guide/getting-started.md → CLAUDE.md
- `Biometric App Lock Feature (v1.0.13)` --semantically_similar_to--> `LockViewModel`  [INFERRED] [semantically similar]
  docs/changelog.md → CLAUDE.md
- `Calendar Screen (monthly wear history view)` --semantically_similar_to--> `Calendar Screen (top-level drawer nav screen)`  [INFERRED] [semantically similar]
  docs/features/calendar.md → CLAUDE.md
- `Stats Screen (wear analytics)` --semantically_similar_to--> `Stats Screen (top-level drawer nav screen)`  [INFERRED] [semantically similar]
  docs/features/stats.md → CLAUDE.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Room Data Layer (Database + DAOs + Entities)** — wardove_wardrobe_database, wardove_clothing_dao, wardove_wear_log_dao, wardove_laundry_dao, wardove_clothing_item_entity, wardove_wear_log_entity, wardove_laundry_cycle_entity, wardove_laundry_cycle_item_entity [EXTRACTED 1.00]
- **Hilt DI Module Triad (Database + Repository + Settings)** — wardove_hilt_di, wardove_database_module, wardove_repository_module, wardove_settings_module [EXTRACTED 1.00]
- **Top-Level Navigation Drawer Screens** — wardove_wardrobe_screen, wardove_laundry_screen, wardove_calendar_screen, wardove_stats_screen, wardove_settings_screen, wardove_wardove_nav_host [EXTRACTED 1.00]

## Communities (90 total, 28 thin omitted)

### Community 0 - "Documentation & Feature Docs"
Cohesion: 0.06
Nodes (52): Auto-Update System via GitHub Releases (v1.0.10), Biometric App Lock Feature (v1.0.13), Wardove Changelog, Calendar Screen (monthly wear history view), Wear Today / Un-wear Today Action, Laundry Cycle Lifecycle (startCycle → completeCycle), Wear Threshold (laundry reminder, range 1-30), Appearance Settings (theme mode, Material You dynamic color) (+44 more)

### Community 1 - "Settings UI & Navigation"
Cohesion: 0.07
Nodes (44): Boolean, ClothingItem, Modifier, String, Modifier, String, Color, Modifier (+36 more)

### Community 2 - "ViewModel State Management"
Cohesion: 0.39
Nodes (3): String, Uri, DiagnosticsRepository

### Community 3 - "App Update Screen"
Cohesion: 0.08
Nodes (24): Boolean, GithubRelease, Long, String, Boolean, Int, List, String (+16 more)

### Community 4 - "Diagnostics & Update Delivery"
Cohesion: 0.10
Nodes (18): File, String, Uri, GithubAsset, Intent, Long, ImageStorage, Job (+10 more)

### Community 5 - "App Entry & Theme"
Cohesion: 0.07
Nodes (23): Bundle, Intent, Sensor, SettingsRepository, String, Bundle, SettingsRepository, Boolean (+15 more)

### Community 6 - "Add Item ViewModel"
Cohesion: 0.15
Nodes (8): AddItemUiState, AddItemViewModel, Boolean, Double, Long, StateFlow, String, Uri

### Community 7 - "Stats & Analytics UI"
Cohesion: 0.19
Nodes (21): ClothingItem, Double, Int, Long, Modifier, String, CostPerWearItem, Map (+13 more)

### Community 8 - "Clothing Data Access"
Cohesion: 0.21
Nodes (7): ClothingItem, Flow, Int, List, Long, String, ClothingDao

### Community 9 - "Update Repository & GitHub API"
Cohesion: 0.15
Nodes (13): GithubAsset, GithubRelease, List, Long, String, Uri, DownloadStatus, Complete (+5 more)

### Community 10 - "Laundry Screen UI"
Cohesion: 0.18
Nodes (20): Boolean, Int, List, Long, Modifier, Set, String, CycleWithItems (+12 more)

### Community 11 - "Add Item Screen"
Cohesion: 0.14
Nodes (18): AddItemScreen(), CategoryDropdown(), ColorGrid(), ImagePicker(), ShareItemSheet(), AddItemViewModel, String, Uri (+10 more)

### Community 12 - "Clothing Repository"
Cohesion: 0.19
Nodes (9): ClothingItem, Flow, Int, List, Long, String, WearLog, Pair (+1 more)

### Community 13 - "Laundry ViewModel"
Cohesion: 0.13
Nodes (12): ClothingItem, Int, List, Long, Set, StateFlow, String, CycleWithItems (+4 more)

### Community 14 - "Laundry Data Access"
Cohesion: 0.22
Nodes (7): ClothingItem, Flow, LaundryCycle, List, Long, LaundryDao, LaundryCycleItem

### Community 15 - "Wardrobe Screen UI"
Cohesion: 0.06
Nodes (44): String, String, ImageVector, String, Boolean, SettingsViewModel, String, String (+36 more)

### Community 16 - "Laundry Repository"
Cohesion: 0.29
Nodes (6): ClothingItem, Flow, LaundryCycle, List, Long, LaundryRepository

### Community 18 - "Item Detail Screen"
Cohesion: 0.21
Nodes (15): Boolean, ClothingItem, Color, List, Modifier, String, ItemDetailBody(), ItemDetailScreen() (+7 more)

### Community 19 - "Wear Log Data Access"
Cohesion: 0.23
Nodes (7): Flow, Int, List, Long, WearLog, WearLogWithItem, WearLogDao

### Community 20 - "App Initialization & Logging"
Cohesion: 0.16
Nodes (9): FileLoggingTree, String, FileLoggingTree, Application, Configuration, DiagnosticsRepository, HiltWorkerFactory, installCrashHandler() (+1 more)

### Community 21 - "Settings Repository"
Cohesion: 0.14
Nodes (9): Boolean, String, WardrobeViewMode, AppSettings, Flow, Int, Keys, SettingsRepository (+1 more)

### Community 22 - "Item Detail ViewModel"
Cohesion: 0.20
Nodes (7): Boolean, ClothingItem, List, Long, StateFlow, WearLog, ItemDetailViewModel

### Community 23 - "Room Database Schema"
Cohesion: 0.18
Nodes (7): ClothingDao, LaundryDao, WearLogDao, migrate(), WardoveDatabase, RoomDatabase, SupportSQLiteDatabase

### Community 24 - "Database DI Module"
Cohesion: 0.24
Nodes (6): ClothingDao, Context, LaundryDao, WearLogDao, DatabaseModule, WardoveDatabase

### Community 25 - "Calendar ViewModel"
Cohesion: 0.22
Nodes (8): ClothingItem, List, LocalDate, Set, StateFlow, WearLogWithItem, CalendarViewModel, ZoneId

### Community 26 - "Community 26"
Cohesion: 0.07
Nodes (31): Changes to this policy, Children's privacy, Contact, Data storage, Network activity, Permissions, Privacy Policy, Third-party libraries (+23 more)

### Community 27 - "Wardrobe ViewModel"
Cohesion: 0.23
Nodes (8): ClothingItem, List, String, StateFlow, WardrobeFilter, WardrobeSort, WardrobeViewModel, WardrobeViewMode

### Community 28 - "File Logging"
Cohesion: 0.29
Nodes (6): File, Int, String, FileLoggingTree, Throwable, Timber

### Community 29 - "Navigation Destinations"
Cohesion: 0.33
Nodes (4): Long, String, ShortcutActions, WardoveDestinations

### Community 30 - "Image Storage"
Cohesion: 0.24
Nodes (5): AppSettings, Boolean, StateFlow, ThemeMode, SettingsViewModel

### Community 31 - "Docs Build Config"
Cohesion: 0.22
Nodes (8): devDependencies, vitepress, name, private, scripts, docs:build, docs:dev, docs:preview

### Community 32 - "Settings DI Module"
Cohesion: 0.43
Nodes (4): Context, DataStore, SettingsModule, Preferences

### Community 33 - "Calendar Repository"
Cohesion: 0.33
Nodes (4): Flow, List, WearLogWithItem, CalendarRepository

### Community 34 - "Design System & Theme"
Cohesion: 0.07
Nodes (27): [1.0.10] — 2026-06-14, [1.0.13] — 2026-06-15, [1.0.14] — 2026-06-15, [1.0.15] — 2026-06-18, [1.0.16] — 2026-06-18, [1.0.17] — 2026-06-18, [1.0.18] — 2026-06-18, [1.0.19] — 2026-06-18 (+19 more)

### Community 35 - "App Lock Repository"
Cohesion: 0.50
Nodes (3): Boolean, Flow, AppLockRepository

### Community 36 - "Date Formatting Utils"
Cohesion: 0.67
Nodes (3): String, formatDateOnly(), formatDateShort()

### Community 69 - "Community 69"
Cohesion: 0.14
Nodes (8): ClothingRepository, String, CoroutineScope, LaundryTileService, QuickAddTileService, StatsTileService, launchMainActivity(), TileService

### Community 70 - "Community 70"
Cohesion: 0.18
Nodes (12): Color, Context, Int, String, GlanceAppWidget, GlanceAppWidgetReceiver, GlanceId, GlanceModifier (+4 more)

### Community 71 - "Community 71"
Cohesion: 0.16
Nodes (6): ShareItemViewModel, AddItemUiState, Double, StateFlow, String, Uri

### Community 72 - "Community 72"
Cohesion: 0.08
Nodes (22): App lock, Architecture, Data layer (`data/`), DataStore, Design system, DI (`di/`), Image storage, Key conventions (+14 more)

### Community 73 - "Community 73"
Cohesion: 0.22
Nodes (3): Boolean, StateFlow, LockViewModel

### Community 74 - "Community 74"
Cohesion: 0.20
Nodes (4): StateFlow, String, Uri, DiagnosticsViewModel

### Community 75 - "Community 75"
Cohesion: 0.29
Nodes (6): ClothingItem, LaundryCycle, List, Long, StateFlow, HistoryViewModel

### Community 76 - "Community 76"
Cohesion: 0.40
Nodes (5): StateFlow, CostPerWearItem, StatsUiState, StatsViewModel, ViewModel

### Community 77 - "Community 77"
Cohesion: 0.08
Nodes (23): 1. Fonts, 2. Color Scheme, 3. Wardrobe Screen, 4. Item Detail Screen, 5. Laundry Screen, 6. Date Formatting Helpers, 7. Scaffold Background, Bottom Navigation (+15 more)

### Community 78 - "Community 78"
Cohesion: 0.09
Nodes (22): 1. Core Features (MVP Scope), 2. Tech Stack, 3. Data Model (Room Entities), 4. Screen List, 5. Project Structure, 6. Build Order (suggested phases for Claude Code), 7. First Prompt to Give Claude Code, 8. Out of Scope (for MVP — save for v2) (+14 more)

### Community 79 - "Community 79"
Cohesion: 0.11
Nodes (15): Calendar, Usage, Wear logs, Adding an item, Deleting an item, Editing an item, Item Detail, Items (+7 more)

### Community 80 - "Community 80"
Cohesion: 0.18
Nodes (9): App lock, Architecture, Build Commands, Data layer (`data/`), Design System, DI (`di/`), Key Conventions, UI layer (`ui/`) (+1 more)

### Community 81 - "Community 81"
Cohesion: 0.25
Nodes (5): Int, Sensor, SensorEvent, SensorEventListener, ShakeDetector

### Community 82 - "Community 82"
Cohesion: 0.25
Nodes (7): Build from Source, CI / automated builds, Clone and build, Prerequisites, Signing, Useful tasks, Versioning

### Community 83 - "Community 83"
Cohesion: 0.29
Nodes (6): About, App Lock, Appearance, Dynamic color (Material You), Settings, Theme

### Community 84 - "Community 84"
Cohesion: 0.33
Nodes (5): Data & privacy, First launch, Getting Started, In-app updates, Install

### Community 85 - "Community 85"
Cohesion: 0.33
Nodes (5): Automated Builds, Build, Features, Tech Stack, Wardove

### Community 86 - "Community 86"
Cohesion: 0.40
Nodes (4): History, Laundry, Pile tab, Washing tab

### Community 90 - "Community 90"
Cohesion: 0.22
Nodes (10): Boolean, ClothingItem, LaundryCycle, List, Long, String, CycleRow(), formatDate() (+2 more)

## Knowledge Gaps
- **340 isolated node(s):** `Color`, `WardrobeViewModel`, `WardrobeFilter`, `Boolean`, `CalendarViewModel` (+335 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **28 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `WardoveNavHost()` connect `Wardrobe Screen UI` to `Settings UI & Navigation`, `App Update Screen`, `App Entry & Theme`, `Stats & Analytics UI`, `Laundry Screen UI`, `Add Item Screen`, `Item Detail Screen`, `Community 90`?**
  _High betweenness centrality (0.126) - this node is a cross-community bridge._
- **Why does `AppLockSettingsScreen()` connect `App Entry & Theme` to `Wardrobe Screen UI`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Why does `AppLockSettingsViewModel` connect `App Entry & Theme` to `Community 76`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Are the 18 inferred relationships involving `WardoveNavHost()` (e.g. with `AddItemScreen()` and `CalendarScreen()`) actually correct?**
  _`WardoveNavHost()` has 18 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Color`, `WardrobeViewModel`, `WardrobeFilter` to the rest of the system?**
  _343 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Documentation & Feature Docs` be split into smaller, more focused modules?**
  _Cohesion score 0.05878084179970972 - nodes in this community are weakly interconnected._
- **Should `Settings UI & Navigation` be split into smaller, more focused modules?**
  _Cohesion score 0.06693877551020408 - nodes in this community are weakly interconnected._