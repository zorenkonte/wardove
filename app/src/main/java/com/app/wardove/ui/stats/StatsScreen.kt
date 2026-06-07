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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.ui.wardrobe.WardoveBottomBar
import com.app.wardove.ui.wardrobe.WardroveBottomRoute
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun StatsScreen(
    onSelectWardrobe: () -> Unit,
    onSelectLaundry: () -> Unit,
    onSelectCalendar: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val now = remember { System.currentTimeMillis() }

    Scaffold(
        containerColor = Color(0xFFF7F5F2),
        bottomBar = {
            WardoveBottomBar(
                currentRoute = WardroveBottomRoute.STATS,
                onSelectWardrobe = onSelectWardrobe,
                onSelectLaundry = onSelectLaundry,
                onSelectCalendar = onSelectCalendar,
                onSelectStats = {}
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Header() }
            item {
                SummaryGrid(
                    totalItems = state.totalItems,
                    totalWears = state.totalWears,
                    averageWearsPerItem = state.averageWearsPerItem,
                    trackedPrices = state.costPerWearItems.size
                )
            }
            item { SectionHeader("Highlights") }
            item {
                HighlightsBlock(
                    mostWorn = state.mostWornItem,
                    leastWorn = state.leastWornItem,
                    longestUnworn = state.longestUnworn,
                    now = now
                )
            }
            item { SectionHeader("Category breakdown") }
            item { CategoryBreakdown(breakdown = state.categoryBreakdown) }
            item { SectionHeader("Cost per wear") }
            item {
                if (state.costPerWearItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Add prices to your clothes to track cost per wear",
                            color = Color(0xFFAAAAAA),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            items(state.costPerWearItems, key = { it.item.id }) { entry ->
                CostPerWearRow(entry)
                HorizontalDivider(
                    color = Color(0xFFE0DDD8),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            "Stats",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF1A1A1A)
        )
        Text(
            "Your wardrobe at a glance",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF888888)
        )
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
            StatBox("Total items", totalItems.toString(), Modifier.weight(1f))
            StatBox("Total wears", totalWears.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox("Avg wears/item", formatAverage(averageWearsPerItem), Modifier.weight(1f))
            StatBox("Tracked prices", trackedPrices.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFEBE8E3), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF888888))
        Text(
            value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
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
        color = Color(0xFF888888),
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
            label = "Most worn",
            item = mostWorn,
            trailing = mostWorn?.let { "${it.totalWearCount} times" }
        )
        HighlightCard(
            label = "Least worn",
            item = leastWorn,
            trailing = leastWorn?.let { "${it.totalWearCount} times" }
        )
        HighlightCard(
            label = "Longest unworn",
            item = longestUnworn,
            trailing = longestUnworn?.let { item ->
                val last = item.lastWornDate
                if (last == null) "Never worn"
                else "Last worn ${daysSince(last, now)} days ago"
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEBE8E3))
                ) {
                    AsyncImage(
                        model = File(item.imagePath),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        label,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                    Text(
                        item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1
                    )
                }
                trailing?.let {
                    Text(
                        it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A1A)
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontSize = 11.sp, color = Color(0xFF888888))
                    Text(
                        "—",
                        fontSize = 14.sp,
                        color = Color(0xFFAAAAAA)
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
                "No items yet.",
                color = Color(0xFFAAAAAA),
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
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(90.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEBE8E3))
            )
            val fraction = if (max == 0) 0f else count.toFloat() / max
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1A1A1A))
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEBE8E3))
        ) {
            AsyncImage(
                model = File(entry.item.imagePath),
                contentDescription = entry.item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                maxLines = 1
            )
            Text(
                entry.item.category,
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )
        }
        Text(
            "₱${"%.2f".format(entry.costPerWear)} per wear",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
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
