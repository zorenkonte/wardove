# Wardrobe

The Wardrobe screen is the home screen — your full catalog of clothing items.

## Overview

Each item card shows the item's photo, name, category, color, and current status. Tap a card to open the [Item Detail](./item-detail) screen.

Tap the **+** FAB to [add a new item](./item-detail#adding-an-item).

## Search

The search bar filters items by **name** or **category** in real time.

## Status filter

Filter the list to show only items in a given status:

| Filter | Shows |
|--------|-------|
| All | Every item |
| Clean | Items marked clean |
| Worn | Items currently in the worn pile |
| In Laundry | Items in an active wash cycle |

## Sort

| Sort option | Order |
|-------------|-------|
| Recently worn | Most recently worn date, descending |
| Alphabetical | Name A → Z |
| Most worn | Highest total wear count first |
| Recently added | Newest items first |

## Item statuses

Items cycle through three statuses:

- **Clean** — ready to wear (default after creation or completing a laundry cycle).
- **Worn** — logged as worn today (or at some point); sitting in the dirty pile.
- **In Laundry** — currently in an active wash cycle.

Status transitions:
- Log a wear on an item → **Worn**
- Send worn item to laundry → **In Laundry**
- Complete the laundry cycle → **Clean**
