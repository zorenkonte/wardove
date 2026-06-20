# Data Model

Wardove uses a **Room** (SQLite) database (schema version 2). All data is local to the app sandbox.

## Entities

### ClothingItem

Table: `clothing_items`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` (PK, auto) | |
| `name` | `String` | Display name |
| `category` | `String` | Top / Bottom / Shoes / Outerwear / Accessory |
| `color` | `String` | One of 12 preset color names |
| `imagePath` | `String` | Absolute path to photo in `filesDir/images/` |
| `status` | `String` | `CLEAN` / `WORN` / `IN_LAUNDRY` (default `CLEAN`) |
| `lastWornDate` | `Long?` | Epoch millis of most recent wear; null if never worn |
| `totalWearCount` | `Int` | Running total of all wear logs (default 0) |
| `createdAt` | `Long` | Epoch millis; defaults to now at insert time |
| `notes` | `String?` | Free-text notes |
| `price` | `Double?` | Purchase price; used for cost-per-wear stat |

**Status constants** (`ClothingStatus` object): `CLEAN`, `WORN`, `IN_LAUNDRY`.

### WearLog

Table: `wear_logs`

One row per "worn today" event. Powers calendar, stats, and item history.

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` (PK, auto) | |
| `clothingItemId` | `Long` | FK → `ClothingItem.id` (`onDelete = CASCADE`, indexed) |
| `wornDate` | `Long` | Epoch millis of the wear date |

### LaundryCycle

Table: `laundry_cycles`

One row per wash batch.

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` (PK, auto) | |
| `startedAt` | `Long` | Epoch millis when cycle started |
| `completedAt` | `Long?` | Null while active; set when cycle is completed |
| `itemCount` | `Int` | Number of items in the cycle |

An active cycle has `completedAt = null`.

### LaundryCycleItem

Table: `laundry_cycle_items`

Join table linking a cycle to its items. Composite PK `(cycleId, clothingItemId)`.

| Column | Type | Notes |
|--------|------|-------|
| `cycleId` | `Long` | FK → `LaundryCycle.id` (`CASCADE`, indexed) |
| `clothingItemId` | `Long` | FK → `ClothingItem.id` (`CASCADE`, indexed) |

## Lifecycle of a laundry cycle

```
Worn item(s) in pile
        ↓
   startCycle()          → LaundryCycle inserted, LaundryCycleItems inserted,
                            items → IN_LAUNDRY
        ↓
  completeCycle()        → items → CLEAN (wearCount reset),
                            LaundryCycle.completedAt stamped
        ↓
  Appears in History
```

## Photos

Item photos are stored by `ImageStorage` at:

```
context.filesDir/images/<filename>
```

Served externally via `FileProvider` (`{packageName}.fileprovider`). Photos are deleted when the associated item is deleted. Never write to external storage directly — use `ImageStorage` only.

## Migrations

Database version: **2**. Migrations live in `WardoveDatabase.MIGRATION_X_Y` companion objects. Always add a migration — do not rely on fallback-to-destructive.
