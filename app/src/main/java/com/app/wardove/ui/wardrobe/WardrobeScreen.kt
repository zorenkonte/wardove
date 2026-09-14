package com.app.wardove.ui.wardrobe

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.wardove.R
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.settings.WardrobeViewMode
import com.app.wardove.ui.components.ClothingImage
import com.app.wardove.ui.components.Dot
import com.app.wardove.ui.components.LargeTitleHeader
import com.app.wardove.ui.components.SingleSelectSheet
import com.app.wardove.ui.components.WardoveLottie
import com.app.wardove.ui.components.animatedWardrobeCell
import com.app.wardove.ui.components.animatedWardrobeRow
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.style.TextAlign
import com.app.wardove.ui.navigation.clothingSharedImage
import com.app.wardove.ui.theme.StatusClean
import com.app.wardove.ui.theme.StatusLaundry
import com.app.wardove.ui.theme.StatusWorn
import com.app.wardove.ui.theme.textHint
import com.app.wardove.ui.util.ClothingOptions
import com.composables.icons.lucide.ArrowUpDown
import com.composables.icons.lucide.Grid3x3
import com.composables.icons.lucide.Group
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Shirt
import com.composables.icons.lucide.Ungroup
import com.composables.icons.lucide.X

private val itemContentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 112.dp)

private val WardrobeViewMode.icon: ImageVector
    get() = when (this) {
        WardrobeViewMode.CARD -> Lucide.LayoutGrid
        WardrobeViewMode.LIST -> Lucide.List
        WardrobeViewMode.COMPACT -> Lucide.Grid3x3
    }

@Composable
private fun statusDotColor(status: String): Color = when (status) {
    ClothingStatus.CLEAN -> StatusClean
    ClothingStatus.WORN -> StatusWorn
    ClothingStatus.IN_LAUNDRY -> StatusLaundry
    else -> MaterialTheme.colorScheme.textHint
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WardrobeScreen(
    onAddItem: () -> Unit,
    onOpenItem: (Long) -> Unit,
    onOpenDrawer: () -> Unit = {},
    snackbarMessage: String? = null,
    onSnackbarShown: () -> Unit = {},
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    val items by viewModel.filteredAndSortedItems.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSort by viewModel.sort.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val groupByCategory by viewModel.groupByCategory.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSortSheet by remember { mutableStateOf(false) }
    var showViewSheet by remember { mutableStateOf(false) }

    // One scroll state per layout so switching view modes keeps each one's position,
    // and so the FAB can collapse to an icon once the user scrolls into the list.
    val cardGridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val compactGridState = rememberLazyGridState()
    val fabExpanded by remember(viewMode) {
        derivedStateOf {
            when (viewMode) {
                WardrobeViewMode.CARD ->
                    cardGridState.firstVisibleItemIndex == 0 && cardGridState.firstVisibleItemScrollOffset < 24
                WardrobeViewMode.LIST ->
                    listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 24
                WardrobeViewMode.COMPACT ->
                    compactGridState.firstVisibleItemIndex == 0 && compactGridState.firstVisibleItemScrollOffset < 24
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onSnackbarShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // M3 Expressive medium extended FAB: label shows at rest, collapses on scroll.
            MediumExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.action_add_item)) },
                icon = { Icon(Lucide.Plus, contentDescription = null) },
                onClick = onAddItem,
                expanded = fabExpanded,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LargeTitleHeader(
                title = stringResource(R.string.nav_wardrobe),
                onOpenDrawer = onOpenDrawer,
                subtitle = pluralStringResource(R.plurals.wardrobe_item_count, items.size, items.size),
                actions = {
                    IconButton(onClick = { viewModel.setGroupByCategory(!groupByCategory) }) {
                        Icon(
                            if (groupByCategory) Lucide.Ungroup else Lucide.Group,
                            contentDescription = stringResource(R.string.action_group_by_category),
                            tint = if (groupByCategory) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    }
                    IconButton(onClick = { showViewSheet = true }) {
                        Icon(
                            viewMode.icon,
                            contentDescription = stringResource(R.string.action_view_mode),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(
                            Lucide.ArrowUpDown,
                            contentDescription = stringResource(R.string.action_sort),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
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
                    filter = selectedFilter,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val grouped = remember(items, groupByCategory) {
                    if (!groupByCategory) return@remember emptyList()
                    items.groupBy { it.category }
                        .entries.sortedBy { entry ->
                            ClothingOptions.categories.indexOf(entry.key)
                                .let { i -> if (i < 0) Int.MAX_VALUE else i }
                        }
                }
                when (viewMode) {
                    WardrobeViewMode.CARD -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = cardGridState,
                        contentPadding = itemContentPadding,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (groupByCategory) {
                            grouped.forEach { (category, groupItems) ->
                                item(span = { GridItemSpan(maxLineSpan) }, key = "header_$category") {
                                    CategorySectionHeader(
                                        category = category,
                                        count = groupItems.size,
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                itemsIndexed(groupItems, key = { _, it -> it.id }) { index, item ->
                                    ClothingCard(
                                        item = item,
                                        onClick = { onOpenItem(item.id) },
                                        modifier = animatedWardrobeCell(index, item.id)
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(items, key = { _, it -> it.id }) { index, item ->
                                ClothingCard(
                                    item = item,
                                    onClick = { onOpenItem(item.id) },
                                    modifier = animatedWardrobeCell(index, item.id)
                                )
                            }
                        }
                    }
                    WardrobeViewMode.LIST -> LazyColumn(
                        state = listState,
                        contentPadding = itemContentPadding,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (groupByCategory) {
                            grouped.forEach { (category, groupItems) ->
                                item(key = "header_$category") {
                                    CategorySectionHeader(
                                        category = category,
                                        count = groupItems.size,
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                itemsIndexed(groupItems, key = { _, it -> it.id }) { index, item ->
                                    ClothingListRow(
                                        item = item,
                                        onClick = { onOpenItem(item.id) },
                                        modifier = animatedWardrobeRow(index, item.id)
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(items, key = { _, it -> it.id }) { index, item ->
                                ClothingListRow(
                                    item = item,
                                    onClick = { onOpenItem(item.id) },
                                    modifier = animatedWardrobeRow(index, item.id)
                                )
                            }
                        }
                    }
                    WardrobeViewMode.COMPACT -> LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = compactGridState,
                        contentPadding = itemContentPadding,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (groupByCategory) {
                            grouped.forEach { (category, groupItems) ->
                                item(span = { GridItemSpan(maxLineSpan) }, key = "header_$category") {
                                    CategorySectionHeader(
                                        category = category,
                                        count = groupItems.size,
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                itemsIndexed(groupItems, key = { _, it -> it.id }) { index, item ->
                                    CompactCard(
                                        item = item,
                                        onClick = { onOpenItem(item.id) },
                                        modifier = animatedWardrobeCell(index, item.id)
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(items, key = { _, it -> it.id }) { index, item ->
                                CompactCard(
                                    item = item,
                                    onClick = { onOpenItem(item.id) },
                                    modifier = animatedWardrobeCell(index, item.id)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        SingleSelectSheet(
            title = stringResource(R.string.wardrobe_sort_title),
            options = WardrobeSort.entries,
            selected = selectedSort,
            labelOf = { it.labelResId },
            onSelect = { sort ->
                viewModel.setSort(sort)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }

    if (showViewSheet) {
        SingleSelectSheet(
            title = stringResource(R.string.wardrobe_view_title),
            options = WardrobeViewMode.entries,
            selected = viewMode,
            labelOf = { it.labelResId },
            onSelect = { mode ->
                viewModel.setViewMode(mode)
                showViewSheet = false
            },
            onDismiss = { showViewSheet = false }
        )
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
                stringResource(R.string.wardrobe_search_hint),
                color = MaterialTheme.colorScheme.textHint,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Lucide.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Lucide.X,
                        contentDescription = stringResource(R.string.wardrobe_search_clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
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
                    Text(stringResource(f.labelResId), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun CategorySectionHeader(category: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            ClothingOptions.categoryResId(category)?.let { stringResource(it) } ?: category,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ClothingCard(
    item: ClothingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            ClothingImage(
                imagePath = item.imagePath,
                contentDescription = item.name,
                category = item.category,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clothingSharedImage(item.id)
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
                        ClothingOptions.categoryResId(item.category)
                            ?.let { stringResource(it) }
                            ?: item.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Dot(color = statusDotColor(item.status))
                }
            }
        }
    }
}

@Composable
private fun ClothingListRow(
    item: ClothingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ClothingImage(
                imagePath = item.imagePath,
                contentDescription = item.name,
                category = item.category,
                modifier = Modifier
                    .size(64.dp)
                    .clothingSharedImage(item.id)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    ClothingOptions.categoryResId(item.category)
                        ?.let { stringResource(it) }
                        ?: item.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Dot(color = statusDotColor(item.status))
        }
    }
}

@Composable
private fun CompactCard(
    item: ClothingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            ClothingImage(
                imagePath = item.imagePath,
                contentDescription = item.name,
                category = item.category,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clothingSharedImage(item.id)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Dot(color = statusDotColor(item.status), size = 6.dp, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

/**
 * Empty state that explains *why* nothing is showing: a search with no hits, a
 * status chip that filtered everything out, or a genuinely empty wardrobe (which
 * gets the animated hanger illustration and the add-item nudge).
 */
@Composable
private fun EmptyState(
    hasQuery: Boolean,
    filter: WardrobeFilter,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            val (title, body) = when {
                hasQuery -> stringResource(R.string.wardrobe_no_matches_title) to
                    stringResource(R.string.wardrobe_no_matches_body)
                filter == WardrobeFilter.CLEAN -> stringResource(R.string.wardrobe_empty_filter_title) to
                    stringResource(R.string.wardrobe_empty_filter_clean)
                filter == WardrobeFilter.WORN -> stringResource(R.string.wardrobe_empty_filter_title) to
                    stringResource(R.string.wardrobe_empty_filter_worn)
                filter == WardrobeFilter.IN_LAUNDRY -> stringResource(R.string.wardrobe_empty_filter_title) to
                    stringResource(R.string.wardrobe_empty_filter_laundry)
                else -> stringResource(R.string.wardrobe_empty_title) to
                    stringResource(R.string.wardrobe_empty_body)
            }
            if (hasQuery || filter != WardrobeFilter.ALL) {
                Icon(
                    Lucide.Shirt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.textHint
                )
            } else {
                WardoveLottie(
                    animation = R.raw.lottie_wardrobe,
                    modifier = Modifier.size(180.dp)
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
