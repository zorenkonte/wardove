# Graph Report - .  (2026-06-22)

## Corpus Check
- 110 files · ~54,889 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 718 nodes · 1023 edges · 70 communities (44 shown, 26 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 60 edges (avg confidence: 0.83)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Feature Docs & Changelog|Feature Docs & Changelog]]
- [[_COMMUNITY_Shared Type Primitives|Shared Type Primitives]]
- [[_COMMUNITY_Settings & Theme Logic|Settings & Theme Logic]]
- [[_COMMUNITY_Wardrobe & Laundry State|Wardrobe & Laundry State]]
- [[_COMMUNITY_Add Item Flow|Add Item Flow]]
- [[_COMMUNITY_Cost Per Wear Analytics|Cost Per Wear Analytics]]
- [[_COMMUNITY_Clothing Data Access|Clothing Data Access]]
- [[_COMMUNITY_App Update System|App Update System]]
- [[_COMMUNITY_Laundry Cycle Data|Laundry Cycle Data]]
- [[_COMMUNITY_Add Item UI|Add Item UI]]
- [[_COMMUNITY_Clothing Repository|Clothing Repository]]
- [[_COMMUNITY_Laundry ViewModel|Laundry ViewModel]]
- [[_COMMUNITY_Laundry DAO Layer|Laundry DAO Layer]]
- [[_COMMUNITY_Biometric App Lock|Biometric App Lock]]
- [[_COMMUNITY_Header UI Components|Header UI Components]]
- [[_COMMUNITY_Laundry Repository|Laundry Repository]]
- [[_COMMUNITY_Calendar Screen|Calendar Screen]]
- [[_COMMUNITY_Wear Log UI|Wear Log UI]]
- [[_COMMUNITY_Wear Log DAO|Wear Log DAO]]
- [[_COMMUNITY_Settings UI|Settings UI]]
- [[_COMMUNITY_Diagnostics & Logging|Diagnostics & Logging]]
- [[_COMMUNITY_App Settings Storage|App Settings Storage]]
- [[_COMMUNITY_Update UI|Update UI]]
- [[_COMMUNITY_Item Detail ViewModel|Item Detail ViewModel]]
- [[_COMMUNITY_Room Database|Room Database]]
- [[_COMMUNITY_Database DI Module|Database DI Module]]
- [[_COMMUNITY_Calendar ViewModel|Calendar ViewModel]]
- [[_COMMUNITY_Laundry History Screen|Laundry History Screen]]
- [[_COMMUNITY_Wardrobe ViewModel|Wardrobe ViewModel]]
- [[_COMMUNITY_File Logging|File Logging]]
- [[_COMMUNITY_Image Storage|Image Storage]]
- [[_COMMUNITY_Docs Build System|Docs Build System]]
- [[_COMMUNITY_Settings DI Module|Settings DI Module]]
- [[_COMMUNITY_Navigation Routes|Navigation Routes]]
- [[_COMMUNITY_Calendar Repository|Calendar Repository]]
- [[_COMMUNITY_Design System|Design System]]
- [[_COMMUNITY_App Lock Repository|App Lock Repository]]
- [[_COMMUNITY_Date Formatting|Date Formatting]]
- [[_COMMUNITY_Clothing Item Entity|Clothing Item Entity]]
- [[_COMMUNITY_GitHub Release Models|GitHub Release Models]]
- [[_COMMUNITY_Instrumented Tests|Instrumented Tests]]
- [[_COMMUNITY_Unit Tests|Unit Tests]]
- [[_COMMUNITY_Wear Log Join Model|Wear Log Join Model]]
- [[_COMMUNITY_Repository DI Module|Repository DI Module]]
- [[_COMMUNITY_Docs Deployment|Docs Deployment]]
- [[_COMMUNITY_Splash Screen Assets|Splash Screen Assets]]
- [[_COMMUNITY_Laundry Cycle Entity|Laundry Cycle Entity]]
- [[_COMMUNITY_Laundry Cycle Item Entity|Laundry Cycle Item Entity]]
- [[_COMMUNITY_Wear Log Entity|Wear Log Entity]]
- [[_COMMUNITY_App Settings DataStore|App Settings DataStore]]
- [[_COMMUNITY_Theme Mode Enum|Theme Mode Enum]]
- [[_COMMUNITY_Launcher Icon (nodpi)|Launcher Icon (nodpi)]]
- [[_COMMUNITY_Launcher Icon (hdpi)|Launcher Icon (hdpi)]]
- [[_COMMUNITY_Launcher Round Icon (hdpi)|Launcher Round Icon (hdpi)]]
- [[_COMMUNITY_Launcher Icon (mdpi)|Launcher Icon (mdpi)]]
- [[_COMMUNITY_Launcher Round Icon (mdpi)|Launcher Round Icon (mdpi)]]
- [[_COMMUNITY_Launcher Icon (xhdpi)|Launcher Icon (xhdpi)]]
- [[_COMMUNITY_Launcher Round Icon (xhdpi)|Launcher Round Icon (xhdpi)]]
- [[_COMMUNITY_Launcher Icon (xxhdpi)|Launcher Icon (xxhdpi)]]
- [[_COMMUNITY_Launcher Round Icon (xxhdpi)|Launcher Round Icon (xxhdpi)]]
- [[_COMMUNITY_Launcher Icon (xxxhdpi)|Launcher Icon (xxxhdpi)]]
- [[_COMMUNITY_Launcher Round Icon (xxxhdpi)|Launcher Round Icon (xxxhdpi)]]
- [[_COMMUNITY_App Logo|App Logo]]
- [[_COMMUNITY_java.time Desugaring|java.time Desugaring]]

## God Nodes (most connected - your core abstractions)
1. `WardoveNavHost()` - 21 edges
2. `AddItemViewModel` - 20 edges
3. `LaundryViewModel` - 20 edges
4. `ClothingDao` - 14 edges
5. `LaundryDao` - 12 edges
6. `ClothingRepository` - 12 edges
7. `ItemDetailViewModel` - 12 edges
8. `PileTab()` - 12 edges
9. `CalendarViewModel` - 11 edges
10. `UpdateViewModel` - 11 edges

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

## Communities (70 total, 26 thin omitted)

### Community 0 - "Feature Docs & Changelog"
Cohesion: 0.05
Nodes (60): Auto-Update System via GitHub Releases (v1.0.10), Biometric App Lock Feature (v1.0.13), Wardove Changelog, Privacy Policy, Terms of Service, Calendar Screen (monthly wear history view), Wear Today / Un-wear Today Action, Laundry Cycle Lifecycle (startCycle → completeCycle) (+52 more)

### Community 1 - "Shared Type Primitives"
Cohesion: 0.06
Nodes (29): String, Uri, Boolean, GithubAsset, Int, Intent, List, Long (+21 more)

### Community 2 - "Settings & Theme Logic"
Cohesion: 0.07
Nodes (34): String, String, Boolean, SettingsViewModel, String, Boolean, StateFlow, String (+26 more)

### Community 3 - "Wardrobe & Laundry State"
Cohesion: 0.05
Nodes (23): ClothingItem, LaundryCycle, List, Long, StateFlow, Boolean, StateFlow, StateFlow (+15 more)

### Community 4 - "Add Item Flow"
Cohesion: 0.16
Nodes (8): AddItemUiState, AddItemViewModel, Boolean, Double, Long, StateFlow, String, Uri

### Community 5 - "Cost Per Wear Analytics"
Cohesion: 0.19
Nodes (21): ClothingItem, Double, Int, Long, Modifier, String, CostPerWearItem, Map (+13 more)

### Community 6 - "Clothing Data Access"
Cohesion: 0.22
Nodes (7): ClothingItem, Flow, Int, List, Long, String, ClothingDao

### Community 7 - "App Update System"
Cohesion: 0.15
Nodes (13): GithubAsset, GithubRelease, List, Long, String, Uri, DownloadStatus, Complete (+5 more)

### Community 8 - "Laundry Cycle Data"
Cohesion: 0.18
Nodes (20): Boolean, Int, List, Long, Modifier, Set, String, CycleWithItems (+12 more)

### Community 9 - "Add Item UI"
Cohesion: 0.17
Nodes (15): AddItemScreen(), CategoryDropdown(), ColorGrid(), ImagePicker(), AddItemViewModel, String, Color, Int (+7 more)

### Community 10 - "Clothing Repository"
Cohesion: 0.21
Nodes (8): ClothingItem, Flow, List, Long, String, WearLog, Pair, ClothingRepository

### Community 11 - "Laundry ViewModel"
Cohesion: 0.13
Nodes (12): ClothingItem, Int, List, Long, Set, StateFlow, String, CycleWithItems (+4 more)

### Community 12 - "Laundry DAO Layer"
Cohesion: 0.22
Nodes (7): ClothingItem, Flow, LaundryCycle, List, Long, LaundryDao, LaundryCycleItem

### Community 13 - "Biometric App Lock"
Cohesion: 0.12
Nodes (11): Intent, Boolean, ThemeMode, BiometricPrompt, Bundle, FragmentActivity, LockScreen(), LockViewModel (+3 more)

### Community 14 - "Header UI Components"
Cohesion: 0.16
Nodes (16): Modifier, String, Boolean, ClothingItem, Modifier, String, LargeTitleHeader(), ClothingCard() (+8 more)

### Community 15 - "Laundry Repository"
Cohesion: 0.29
Nodes (6): ClothingItem, Flow, LaundryCycle, List, Long, LaundryRepository

### Community 16 - "Calendar Screen"
Cohesion: 0.23
Nodes (15): Boolean, ClothingItem, Int, LocalDate, Set, adjustSelection(), CalendarScreen(), DayCell() (+7 more)

### Community 17 - "Wear Log UI"
Cohesion: 0.21
Nodes (15): Boolean, ClothingItem, Color, List, Long, Modifier, String, WearLog (+7 more)

### Community 18 - "Wear Log DAO"
Cohesion: 0.23
Nodes (7): Flow, Int, List, Long, WearLog, WearLogWithItem, WearLogDao

### Community 19 - "Settings UI"
Cohesion: 0.21
Nodes (12): ImageVector, String, String, Context, String, AboutSettingsScreen(), ChevronRow(), ExternalRow() (+4 more)

### Community 20 - "Diagnostics & Logging"
Cohesion: 0.16
Nodes (9): FileLoggingTree, String, FileLoggingTree, Application, Configuration, DiagnosticsRepository, HiltWorkerFactory, installCrashHandler() (+1 more)

### Community 21 - "App Settings Storage"
Cohesion: 0.16
Nodes (8): AppSettings, Boolean, Flow, Int, String, ThemeMode, Keys, SettingsRepository

### Community 22 - "Update UI"
Cohesion: 0.24
Nodes (13): Boolean, GithubRelease, Long, String, InstallState, CurrentVersionCard(), FeedbackCard(), formatBytes() (+5 more)

### Community 23 - "Item Detail ViewModel"
Cohesion: 0.20
Nodes (7): Boolean, ClothingItem, List, Long, StateFlow, WearLog, ItemDetailViewModel

### Community 24 - "Room Database"
Cohesion: 0.18
Nodes (7): ClothingDao, LaundryDao, WearLogDao, migrate(), WardoveDatabase, RoomDatabase, SupportSQLiteDatabase

### Community 25 - "Database DI Module"
Cohesion: 0.24
Nodes (6): ClothingDao, Context, LaundryDao, WearLogDao, DatabaseModule, WardoveDatabase

### Community 26 - "Calendar ViewModel"
Cohesion: 0.22
Nodes (8): ClothingItem, List, LocalDate, Set, StateFlow, WearLogWithItem, CalendarViewModel, ZoneId

### Community 27 - "Laundry History Screen"
Cohesion: 0.22
Nodes (10): Boolean, ClothingItem, LaundryCycle, List, Long, String, CycleRow(), formatDate() (+2 more)

### Community 28 - "Wardrobe ViewModel"
Cohesion: 0.27
Nodes (7): ClothingItem, List, StateFlow, String, WardrobeFilter, WardrobeSort, WardrobeViewModel

### Community 29 - "File Logging"
Cohesion: 0.29
Nodes (6): File, Int, String, FileLoggingTree, Throwable, Timber

### Community 30 - "Image Storage"
Cohesion: 0.36
Nodes (4): File, String, Uri, ImageStorage

### Community 31 - "Docs Build System"
Cohesion: 0.22
Nodes (8): devDependencies, vitepress, name, private, scripts, docs:build, docs:dev, docs:preview

### Community 32 - "Settings DI Module"
Cohesion: 0.43
Nodes (4): Context, DataStore, SettingsModule, Preferences

### Community 33 - "Navigation Routes"
Cohesion: 0.43
Nodes (3): Long, String, WardoveDestinations

### Community 34 - "Calendar Repository"
Cohesion: 0.33
Nodes (4): Flow, List, WearLogWithItem, CalendarRepository

### Community 35 - "Design System"
Cohesion: 0.53
Nodes (6): Wardove Design System, DM Sans Font, DM Serif Display Font, WardoveLightColors (Material 3 ColorScheme), WardoveTheme (Compose Material 3 Theme wrapper), WardoveTypography (Material 3 Typography)

### Community 36 - "App Lock Repository"
Cohesion: 0.50
Nodes (3): Boolean, Flow, AppLockRepository

### Community 37 - "Date Formatting"
Cohesion: 0.67
Nodes (3): String, formatDateOnly(), formatDateShort()

## Knowledge Gaps
- **184 isolated node(s):** `SettingsRepository`, `LockViewModel`, `Bundle`, `Intent`, `HiltWorkerFactory` (+179 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **26 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `WardoveNavHost()` connect `Settings & Theme Logic` to `Cost Per Wear Analytics`, `Laundry Cycle Data`, `Add Item UI`, `Biometric App Lock`, `Header UI Components`, `Calendar Screen`, `Wear Log UI`, `Settings UI`, `Update UI`, `Laundry History Screen`?**
  _High betweenness centrality (0.196) - this node is a cross-community bridge._
- **Why does `AppLockSettingsScreen()` connect `Settings & Theme Logic` to `Biometric App Lock`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Why does `AppLockSettingsViewModel` connect `Settings & Theme Logic` to `Wardrobe & Laundry State`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Are the 18 inferred relationships involving `WardoveNavHost()` (e.g. with `AddItemScreen()` and `CalendarScreen()`) actually correct?**
  _`WardoveNavHost()` has 18 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SettingsRepository`, `LockViewModel`, `Bundle` to the rest of the system?**
  _187 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Feature Docs & Changelog` be split into smaller, more focused modules?**
  _Cohesion score 0.05254237288135593 - nodes in this community are weakly interconnected._
- **Should `Shared Type Primitives` be split into smaller, more focused modules?**
  _Cohesion score 0.05725490196078432 - nodes in this community are weakly interconnected._