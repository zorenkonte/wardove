package com.app.wardove.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.app.wardove.ui.components.LargeTitleHeader
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.ui.components.ClothingImage
import com.app.wardove.R
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.ui.theme.textHint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun StatsScreen(
    onOpenDrawer: () -> Unit = {},
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val now = remember { System.currentTimeMillis() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                LargeTitleHeader(
                    title = stringResource(R.string.nav_stats),
                    onOpenDrawer = onOpenDrawer,
                    subtitle = stringResource(R.string.stats_subtitle)
                )
            }
            item {
                SummaryGrid(
                    totalItems = state.totalItems,
                    totalWears = state.totalWears,
                    averageWearsPerItem = state.averageWearsPerItem,
                    trackedPrices = state.costPerWearItems.size
                )
            }
            item { SectionHeader(stringResource(R.string.stats_section_highlights)) }
            item {
                HighlightsBlock(
                    mostWorn = state.mostWornItem,
                    leastWorn = state.leastWornItem,
                    longestUnworn = state.longestUnworn,
                    now = now
                )
            }
            item { SectionHeader(stringResource(R.string.stats_section_category_breakdown)) }
            item { CategoryBreakdown(breakdown = state.categoryBreakdown) }
            item { SectionHeader(stringResource(R.string.stats_section_cost_per_wear)) }
            item {
                if (state.costPerWearItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.stats_no_tracked_prices),
                            color = MaterialTheme.colorScheme.textHint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            items(state.costPerWearItems, key = { it.item.id }) { entry ->
                CostPerWearRow(entry)
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}


@Composable
private fun SummaryGrid(
    totalItems: Int,
    totalWears: Int,
    averageWearsPerItem: Double,
    trackedPrices: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(stringResource(R.string.stats_stat_total_items), totalItems.toString(), Modifier.weight(1f))
            StatBox(stringResource(R.string.stats_stat_total_wears), totalWears.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(stringResource(R.string.stats_stat_avg_wears), formatAverage(averageWearsPerItem), Modifier.weight(1f))
            StatBox(stringResource(R.string.stats_stat_tracked_prices), trackedPrices.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(
        label.uppercase(Locale.getDefault()),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
private fun HighlightsBlock(
    mostWorn: ClothingItem?,
    leastWorn: ClothingItem?,
    longestUnworn: ClothingItem?,
    now: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HighlightCard(
            label = stringResource(R.string.stats_highlight_most_worn),
            item = mostWorn,
            trailing = mostWorn?.let {
                pluralStringResource(R.plurals.stats_worn_times, it.totalWearCount, it.totalWearCount)
            }
        )
        HighlightCard(
            label = stringResource(R.string.stats_highlight_least_worn),
            item = leastWorn,
            trailing = leastWorn?.let {
                pluralStringResource(R.plurals.stats_worn_times, it.totalWearCount, it.totalWearCount)
            }
        )
        val neverWorn = stringResource(R.string.stats_never_worn)
        HighlightCard(
            label = stringResource(R.string.stats_highlight_longest_unworn),
            item = longestUnworn,
            trailing = longestUnworn?.let { item ->
                val last = item.lastWornDate
                if (last == null) neverWorn
                else {
                    val days = daysSince(last, now).toInt()
                    pluralStringResource(R.plurals.stats_last_worn_days, days, days)
                }
            }
        )
    }
}

@Composable
private fun HighlightCard(
    label: String,
    item: ClothingItem?,
    trailing: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item != null) {
                ClothingImage(
                    imagePath = item.imagePath,
                    contentDescription = item.name,
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                }
                trailing?.let {
                    Text(
                        it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "—",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.textHint
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdown(breakdown: Map<String, Int>) {
    if (breakdown.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.stats_no_items),
                color = MaterialTheme.colorScheme.textHint,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }
    val max = breakdown.values.max()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        breakdown.entries.sortedByDescending { it.value }.forEach { (cat, count) ->
            CategoryBar(cat, count, max)
        }
    }
}

@Composable
private fun CategoryBar(category: String, count: Int, max: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            category,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(90.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            val fraction = if (max == 0) 0f else count.toFloat() / max
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun CostPerWearRow(entry: CostPerWearItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClothingImage(
            imagePath = entry.item.imagePath,
            contentDescription = entry.item.name,
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
            Text(
                entry.item.category,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            stringResource(R.string.stats_cost_per_wear, entry.costPerWear),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun formatAverage(v: Double): String =
    if (v % 1.0 == 0.0) v.toLong().toString() else "%.1f".format(v)

private fun daysSince(then: Long, now: Long): Long =
    TimeUnit.MILLISECONDS.toDays((now - then).coerceAtLeast(0L))

@Suppress("unused")
private fun fmtDate(epochMillis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(epochMillis))
