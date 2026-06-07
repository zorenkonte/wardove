# Wardove Design Spec — Pixel Perfect Implementation

## 1. Fonts

Add dependency in build.gradle.kts:
```kotlin
implementation("androidx.compose.ui:ui-text-google-fonts:1.7.0")
```

In `ui/theme/Type.kt`:
```kotlin
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val DmSerifDisplay = FontFamily(Font(GoogleFont("DM Serif Display"), provider))
val DmSans = FontFamily(Font(GoogleFont("DM Sans"), provider))

val WardoveTypography = Typography(
    displayLarge  = TextStyle(fontFamily = DmSerifDisplay, fontSize = 32.sp),
    headlineLarge = TextStyle(fontFamily = DmSerifDisplay, fontSize = 24.sp),
    headlineMedium= TextStyle(fontFamily = DmSerifDisplay, fontSize = 20.sp),
    titleLarge    = TextStyle(fontFamily = DmSans, fontSize = 18.sp, fontWeight = FontWeight.Medium),
    titleMedium   = TextStyle(fontFamily = DmSans, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge     = TextStyle(fontFamily = DmSans, fontSize = 15.sp),
    bodyMedium    = TextStyle(fontFamily = DmSans, fontSize = 13.sp),
    labelSmall    = TextStyle(fontFamily = DmSans, fontSize = 11.sp),
)
```

---

## 2. Color Scheme

In `ui/theme/Color.kt`:
```kotlin
val Background   = Color(0xFFF7F5F2)  // warm off-white — page bg
val Surface      = Color(0xFFFFFFFF)  // pure white — cards
val SurfaceVariant = Color(0xFFEBE8E3) // warm light gray — chips, stat boxes
val Primary      = Color(0xFF1A1A1A)  // near black — FAB, active chip, buttons
val OnPrimary    = Color(0xFFFFFFFF)
val OnBackground = Color(0xFF1A1A1A)
val TextSecondary= Color(0xFF888888)
val TextHint     = Color(0xFFAAAAAA)

// Status colors
val StatusClean   = Color(0xFF5DCAA5)  // teal green
val StatusWorn    = Color(0xFFEF9F27)  // amber
val StatusLaundry = Color(0xFF7F77DD)  // purple

// Laundry CTA
val LaundryPurple = Color(0xFF7F77DD)
```

In `ui/theme/Theme.kt`:
```kotlin
val WardoveLightColors = lightColorScheme(
    primary           = Color(0xFF1A1A1A),
    onPrimary         = Color(0xFFFFFFFF),
    background        = Color(0xFFF7F5F2),
    onBackground      = Color(0xFF1A1A1A),
    surface           = Color(0xFFFFFFFF),
    onSurface         = Color(0xFF1A1A1A),
    surfaceVariant    = Color(0xFFEBE8E3),
    onSurfaceVariant  = Color(0xFF555555),
    outline           = Color(0xFFE0DDD8),
)

@Composable
fun WardoveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WardoveLightColors,
        typography  = WardoveTypography,
        content     = content
    )
}
```

---

## 3. Wardrobe Screen

### Top Bar
```kotlin
Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
    Text(
        text = "Wardove",
        style = MaterialTheme.typography.displayLarge, // DM Serif Display 32sp
        color = Color(0xFF1A1A1A)
    )
    Text(
        text = "${items.size} items",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF888888)
    )
}
```

### Filter Chips
```kotlin
FilterChip(
    selected = selectedFilter == filter,
    onClick = { selectedFilter = filter },
    label = { Text(filter.label, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
    shape = CircleShape,
    colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = Color(0xFF1A1A1A),
        selectedLabelColor     = Color.White,
        containerColor         = Color(0xFFEBE8E3),
        labelColor             = Color(0xFF555555),
    ),
    border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selectedFilter == filter,
        borderColor = Color.Transparent,
        selectedBorderColor = Color.Transparent,
    )
)
```

### Clothing Card
```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    shape    = RoundedCornerShape(14.dp),
    colors   = CardDefaults.cardColors(containerColor = Color.White),
    elevation= CardDefaults.cardElevation(defaultElevation = 0.dp)
) {
    Column {
        // Photo — full width, 130dp tall
        AsyncImage(
            model = item.imagePath,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(130.dp)
        )
        // Info row
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.category, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF888888))
                // Status dot
                Box(
                    modifier = Modifier.size(8.dp).background(
                        color = when (item.status) {
                            ClothingStatus.CLEAN    -> Color(0xFF5DCAA5)
                            ClothingStatus.WORN     -> Color(0xFFEF9F27)
                            ClothingStatus.IN_LAUNDRY -> Color(0xFF7F77DD)
                        },
                        shape = CircleShape
                    )
                )
            }
        }
    }
}
```

### Grid
```kotlin
LazyVerticalGrid(
    columns = Fixed(2),
    contentPadding = PaddingValues(horizontal = 20.dp, bottom = 100.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement   = Arrangement.spacedBy(10.dp),
)
```

### FAB
```kotlin
FloatingActionButton(
    onClick = { navController.navigate("addItem") },
    containerColor = Color(0xFF1A1A1A),
    contentColor   = Color.White,
    shape          = CircleShape,
) {
    Icon(Icons.Default.Add, contentDescription = "Add item")
}
```

### Bottom Navigation
```kotlin
NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
    NavigationBarItem(
        selected = currentRoute == "wardrobe",
        onClick  = { navController.navigate("wardrobe") },
        icon     = { Icon(Icons.Default.Checkroom, contentDescription = "Wardrobe") },
        label    = { Text("Wardrobe", fontSize = 10.sp) },
        colors   = NavigationBarItemDefaults.colors(
            selectedIconColor   = Color(0xFF1A1A1A),
            selectedTextColor   = Color(0xFF1A1A1A),
            unselectedIconColor = Color(0xFFAAAAAA),
            unselectedTextColor = Color(0xFFAAAAAA),
            indicatorColor      = Color.Transparent
        )
    )
    // Badge on Laundry tab: wrap icon in BadgedBox
    NavigationBarItem(
        selected = currentRoute == "laundry",
        onClick  = { navController.navigate("laundry") },
        icon     = {
            BadgedBox(badge = {
                if (laundryCount > 0) Badge(containerColor = Color(0xFF7F77DD)) { Text("$laundryCount") }
            }) {
                Icon(Icons.Default.LocalLaundryService, contentDescription = "Laundry")
            }
        },
        label    = { Text("Laundry", fontSize = 10.sp) },
        colors   = NavigationBarItemDefaults.colors(
            selectedIconColor   = Color(0xFF1A1A1A),
            selectedTextColor   = Color(0xFF1A1A1A),
            unselectedIconColor = Color(0xFFAAAAAA),
            unselectedTextColor = Color(0xFFAAAAAA),
            indicatorColor      = Color.Transparent
        )
    )
}
```

---

## 4. Item Detail Screen

### Item Name
```kotlin
Text(
    text  = item.name,
    style = MaterialTheme.typography.headlineLarge, // DM Serif Display 24sp
    color = Color(0xFF1A1A1A),
    modifier = Modifier.padding(top = 16.dp)
)
```

### Tags Row (category, color, status)
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    // Category tag
    Box(
        modifier = Modifier
            .background(Color(0xFFEBE8E3), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) { Text(item.category, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF555555)) }

    // Color tag — black pill
    Box(
        modifier = Modifier
            .background(Color(0xFF1A1A1A), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) { Text(item.color, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White) }
}
```

### Stat Boxes
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    StatBox(label = "Times worn",  value = "${item.totalWearCount}", modifier = Modifier.weight(1f))
    StatBox(label = "Last worn",   value = item.lastWornDate?.formatDate() ?: "Never", modifier = Modifier.weight(1f))
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFEBE8E3), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF888888))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A), modifier = Modifier.padding(top = 2.dp))
    }
}
```

### Wear Today Button
```kotlin
Button(
    onClick  = onWearToday,
    enabled  = !alreadyWornToday,
    modifier = Modifier.fillMaxWidth().height(52.dp),
    shape    = RoundedCornerShape(14.dp),
    colors   = ButtonDefaults.buttonColors(
        containerColor         = Color(0xFF1A1A1A),
        contentColor           = Color.White,
        disabledContainerColor = Color(0xFFEBE8E3),
        disabledContentColor   = Color(0xFFAAAAAA),
    )
) {
    Text(if (alreadyWornToday) "Already worn today" else "Wear Today", fontSize = 15.sp, fontWeight = FontWeight.Medium)
}
```

### Wear History List
```kotlin
// Show date only, no time
Text("Wear history", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF888888))
wearLogs.forEach { log ->
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).background(Color(0xFF5DCAA5), CircleShape))
        Spacer(Modifier.width(10.dp))
        Text(
            text  = log.wornDate.formatDateOnly(), // e.g. "June 5, 2026" — NO time
            fontSize = 13.sp,
            color    = Color(0xFF555555)
        )
    }
    Divider(color = Color(0xFFE0DDD8), thickness = 0.5.dp)
}
```

---

## 5. Laundry Screen

### Tab Row
```kotlin
// Custom tab — NOT Material TabRow which has an underline indicator
Box(
    modifier = Modifier
        .padding(horizontal = 20.dp)
        .background(Color(0xFFEBE8E3), RoundedCornerShape(10.dp))
        .padding(3.dp)
) {
    Row {
        listOf("Pile", "Washing").forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedTab == index) Color.White else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedTab = index }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontSize  = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedTab == index) Color(0xFF1A1A1A) else Color(0xFF888888)
                )
            }
        }
    }
}
```

### Laundry Item Row
```kotlin
Card(
    shape  = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(0.dp)
) {
    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = item.imagePath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Worn ${item.lastWornDate?.formatDateShort()}", fontSize = 12.sp, color = Color(0xFF888888), modifier = Modifier.padding(top = 2.dp))
        }
        // Checkbox
        Checkbox(
            checked = item.id in selectedIds,
            onCheckedChange = { toggleSelection(item.id) },
            colors = CheckboxDefaults.colors(
                checkedColor  = Color(0xFF5DCAA5),
                checkmarkColor = Color.White,
                uncheckedColor = Color(0xFFDDDDDD)
            )
        )
    }
}
```

### Send to Laundry Button
```kotlin
Button(
    onClick  = onSendToLaundry,
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp),
    shape    = RoundedCornerShape(14.dp),
    colors   = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF7F77DD),
        contentColor   = Color.White,
    )
) {
    Text("Send to Laundry", fontSize = 15.sp, fontWeight = FontWeight.Medium)
}
```

---

## 6. Date Formatting Helpers

```kotlin
fun Long.formatDateOnly(): String {
    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDateShort(): String {
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(this))
}
```

---

## 7. Scaffold Background

Make sure every screen's Scaffold uses the warm background:
```kotlin
Scaffold(
    containerColor = Color(0xFFF7F5F2),
    ...
)
```

---

## Prompt for Claude Code

Paste this verbatim:

```
Read WARDOVE_DESIGN_SPEC.md in the project root and apply it fully to the app.
Do NOT change any logic, navigation, ViewModels, Room, or repositories.
Only change visual/UI files: Theme.kt, Color.kt, Type.kt, and all Screen composables.

Apply every spec exactly:
1. Install DM Serif Display + DM Sans via Google Fonts. Apply to WardoveTypography.
2. Apply WardoveLightColors to MaterialTheme. Set Scaffold containerColor = Color(0xFFF7F5F2) on every screen.
3. WardrobeScreen: update FilterChip colors, clothing Card style, FAB color, BottomNav colors per spec.
4. ItemDetailScreen: use headlineLarge (DM Serif Display) for item name, StatBox composable for stats, updated Wear Today button style, date-only wear history rows.
5. LaundryScreen: custom pill TabRow (no underline), white laundry item cards with thumbnail, purple Send to Laundry button.
6. Add formatDateOnly() and formatDateShort() extension functions on Long.

After applying, do a full build to verify no compilation errors.
```
