package com.app.wardove.ui.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    onAddItem: () -> Unit,
    onOpenItem: (Long) -> Unit,
    onOpenLaundry: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenStats: () -> Unit,
    snackbarMessage: String? = null,
    onSnackbarShown: () -> Unit = {},
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    val items by viewModel.filteredAndSortedItems.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSort by viewModel.sort.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSortSheet by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onSnackbarShown()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F5F2),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItem,
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        bottomBar = {
            WardoveBottomBar(
                currentRoute = WardroveBottomRoute.WARDROBE,
                onSelectWardrobe = {},
                onSelectLaundry = onOpenLaundry,
                onSelectCalendar = onOpenCalendar,
                onSelectStats = onOpenStats
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            WardrobeHeader(
                itemCount = items.size,
                onSortClick = { showSortSheet = true }
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            )

            FilterRow(
                selected = selectedFilter,
                onSelect = viewModel::setFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )

            if (items.isEmpty()) {
                EmptyState(
                    hasQuery = searchQuery.isNotBlank(),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 100.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        ClothingCard(
                            item = item,
                            onClick = { onOpenItem(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        SortSheet(
            selected = selectedSort,
            onSelect = { sort ->
                viewModel.setSort(sort)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

@Composable
private fun WardrobeHeader(itemCount: Int, onSortClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Wardove",
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "$itemCount items",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888)
            )
        }
        IconButton(onClick = onSortClick) {
            Icon(
                Icons.Default.Sort,
                contentDescription = "Sort",
                tint = Color(0xFF1A1A1A)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                "Search clothes...",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF888888)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color(0xFF888888)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEBE8E3),
            unfocusedContainerColor = Color(0xFFEBE8E3),
            disabledContainerColor = Color(0xFFEBE8E3),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF1A1A1A),
            focusedTextColor = Color(0xFF1A1A1A),
            unfocusedTextColor = Color(0xFF1A1A1A)
        )
    )
}

@Composable
private fun FilterRow(
    selected: WardrobeFilter,
    onSelect: (WardrobeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        WardrobeFilter.entries.forEach { f ->
            val isSelected = f == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(f) },
                label = {
                    Text(f.label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1A1A1A),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFEBE8E3),
                    labelColor = Color(0xFF555555)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortSheet(
    selected: WardrobeSort,
    onSelect: (WardrobeSort) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                "Sort by",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            WardrobeSort.entries.forEach { sort ->
                val isSelected = sort == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                scope.launch { sheetState.hide() }
                                    .invokeOnCompletion { onSelect(sort) }
                            }
                        )
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { onSelect(sort) }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF1A1A1A),
                            unselectedColor = Color(0xFF888888)
                        )
                    )
                    Text(
                        sort.label,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF888888),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClothingCard(
    item: ClothingItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            AsyncImage(
                model = File(item.imagePath),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when (item.status) {
                                    ClothingStatus.CLEAN -> Color(0xFF5DCAA5)
                                    ClothingStatus.WORN -> Color(0xFFEF9F27)
                                    ClothingStatus.IN_LAUNDRY -> Color(0xFF7F77DD)
                                    else -> Color(0xFFAAAAAA)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(hasQuery: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Checkroom,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color(0xFFAAAAAA)
            )
            if (hasQuery) {
                Text(
                    "No matches.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Try a different search.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF888888)
                )
            } else {
                Text(
                    "Your wardrobe is empty.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Tap + to add your first item.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

enum class WardroveBottomRoute { WARDROBE, LAUNDRY, CALENDAR, STATS }

@Composable
fun WardoveBottomBar(
    currentRoute: WardroveBottomRoute,
    onSelectWardrobe: () -> Unit,
    onSelectLaundry: () -> Unit,
    onSelectCalendar: () -> Unit,
    onSelectStats: () -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF1A1A1A),
        selectedTextColor = Color(0xFF1A1A1A),
        unselectedIconColor = Color(0xFFAAAAAA),
        unselectedTextColor = Color(0xFFAAAAAA),
        indicatorColor = Color.Transparent
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = currentRoute == WardroveBottomRoute.WARDROBE,
            onClick = onSelectWardrobe,
            icon = { Icon(Icons.Default.Checkroom, contentDescription = "Wardrobe") },
            label = { Text("Wardrobe", fontSize = 10.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == WardroveBottomRoute.LAUNDRY,
            onClick = onSelectLaundry,
            icon = { Icon(Icons.Default.LocalLaundryService, contentDescription = "Laundry") },
            label = { Text("Laundry", fontSize = 10.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == WardroveBottomRoute.CALENDAR,
            onClick = onSelectCalendar,
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
            label = { Text("Calendar", fontSize = 10.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == WardroveBottomRoute.STATS,
            onClick = onSelectStats,
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
            label = { Text("Stats", fontSize = 10.sp) },
            colors = itemColors
        )
    }
}
