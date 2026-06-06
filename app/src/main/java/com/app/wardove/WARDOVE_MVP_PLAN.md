# Wardove — MVP Plan

A personal wardrobe tracker for Android. Snap a photo of a clothing item, log when you wear it, and manage your laundry pile.

---

## 1. Core Features (MVP Scope)

### Feature 1: Wardrobe Management
- Add a clothing item by taking a photo (or picking from gallery)
- Manually tag: name, category (dropdown), color (picker)
- Optional notes field
- View all items in a grid
- Edit / delete items

### Feature 2: Wear Tracking
- Tap an item → mark as "worn today"
- Each item shows last worn date + total wear count
- Filter wardrobe by: clean / worn / in laundry

### Feature 3: Laundry
- **Laundry pile**: list of all worn (dirty) clothes
- **Mark as "in laundry"**: move from "worn" → "washing"
- **Mark as "clean"**: returns item to wardrobe, resets wear count
- **Laundry history log**: chronological record of every wash cycle

---

## 2. Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Min SDK | 24 |
| Architecture | MVVM + Repository pattern |
| Local DB | Room |
| Image storage | App's internal storage (file paths in Room) |
| Image capture | CameraX or ActivityResultContracts.TakePicture |
| Image loading | Coil |
| DI | Hilt |
| Navigation | Navigation Compose |
| Async | Coroutines + Flow |

> **Note on tagging**: User takes a photo, then fills in details manually via a simple form (name, category dropdown, color picker). Keeps the MVP lean — no ML overhead, faster to ship, and the user has full control over their data.

---

## 3. Data Model (Room Entities)

### `ClothingItem`
```
id: Long (PK, auto-generated)
name: String
category: String        // "Top", "Bottom", "Shoes", "Outerwear", "Accessory"
color: String           // hex string e.g. "#3B5998"
imagePath: String       // local file path
status: String          // "CLEAN" | "WORN" | "IN_LAUNDRY"
lastWornDate: Long?     // epoch millis, nullable
totalWearCount: Int
createdAt: Long
```

### `WearLog`
```
id: Long (PK)
clothingItemId: Long (FK)
wornDate: Long          // epoch millis
```

### `LaundryCycle`
```
id: Long (PK)
startedAt: Long         // when moved to "in laundry"
completedAt: Long?      // when marked clean (null = in progress)
itemCount: Int
```

### `LaundryCycleItem` (join table)
```
cycleId: Long (FK)
clothingItemId: Long (FK)
```

---

## 4. Screen List

| # | Screen | Purpose |
|---|---|---|
| 1 | **Wardrobe (Home)** | Grid of all clothes, filter chips (All / Clean / Worn / In Laundry) |
| 2 | **Item Detail** | Photo, info, "Wear Today" button, wear history, edit/delete |
| 3 | **Add Item** | Camera/gallery → preview → form (name, category, color, notes) → save |
| 4 | **Laundry** | Two tabs: "Pile" (worn items) and "Washing" (in laundry). Action buttons to move items between states. |
| 5 | **Laundry History** | List of past wash cycles with dates + item count |

Bottom navigation: **Wardrobe** · **Laundry** (with optional History accessible from Laundry screen)

---

## 5. Project Structure

```
com.example.wardove/
├── data/
│   ├── local/
│   │   ├── dao/              (ClothingDao, WearLogDao, LaundryDao)
│   │   ├── entity/           (Room entities)
│   │   └── WardoveDatabase.kt
│   ├── repository/
│   │   ├── ClothingRepository.kt
│   │   └── LaundryRepository.kt
│   └── image/
│       └── ImageStorage.kt   (saves images to internal storage)
├── domain/
│   └── model/                (UI-facing models if needed)
├── ui/
│   ├── wardrobe/             (WardrobeScreen + ViewModel)
│   ├── itemdetail/
│   ├── additem/
│   ├── laundry/
│   ├── history/
│   ├── components/           (shared composables)
│   └── theme/                (Material 3 theme)
├── di/                       (Hilt modules)
└── MainActivity.kt
```

---

## 6. Build Order (suggested phases for Claude Code)

### Phase 1 — Foundation (Day 1)
1. Add dependencies: Room, Hilt, Navigation Compose, CameraX, Coil (image loading)
2. Set up Hilt
3. Define Room entities, DAOs, database
4. Set up Navigation graph with placeholder screens

### Phase 2 — Wardrobe & Add Item (Day 2–3)
5. ImageStorage utility (save photos to internal dir)
6. Add Item flow: camera/gallery → preview → form (name, category dropdown, color picker, notes) → save
7. Wardrobe grid screen with filter chips
8. Item detail screen + edit/delete

### Phase 3 — Wear Tracking (Day 4)
9. "Wear Today" action → creates WearLog, updates item status to WORN
10. Display last worn date + wear count on item detail

### Phase 4 — Laundry (Day 5–6)
11. Laundry screen with Pile / Washing tabs
12. "Move to laundry" action (batch select worn items)
13. "Mark as clean" action → completes cycle, resets items to CLEAN
14. Laundry history screen

### Phase 5 — Polish (Day 7)
15. Empty states (no clothes yet, empty laundry pile)
16. Loading indicators
17. Confirmation dialogs (delete, mark clean)
18. App icon + splash screen

---

## 7. First Prompt to Give Claude Code

Once you open the project, start with something like:

> "Set up the foundation for Wardove following `WARDOVE_MVP_PLAN.md`. Add Room, Hilt, Navigation Compose, Coil, and CameraX dependencies. Create the Room entities (`ClothingItem`, `WearLog`, `LaundryCycle`, `LaundryCycleItem`), their DAOs, and the database class. Set up Hilt with the database/repository modules. Create a basic navigation graph with empty Composable screens for Wardrobe, ItemDetail, AddItem, Laundry, and History."

Then work through phases 2–5 one at a time, reviewing each before moving on.

---

## 8. Out of Scope (for MVP — save for v2)

- **Auto-detect category/color from photo** (ML Kit + Palette API)
- Outfit creation (combining items)
- Calendar view of what you wore each day
- Weather-based outfit suggestions
- Cloud sync / multi-device
- Sharing outfits
- Statistics dashboard (most-worn item, cost-per-wear)
- iOS version (KMP migration later)

---

## 9. Nice-to-Have Polish (if time allows in MVP)

- Haptic feedback on "Wear Today" tap
- Animated transitions between item states
- Search bar on wardrobe screen
- Sort options (recently worn, alphabetical, most worn)
