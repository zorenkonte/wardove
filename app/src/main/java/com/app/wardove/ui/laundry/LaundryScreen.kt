package com.app.wardove.ui.laundry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.app.wardove.ui.theme.LaundryPurple
import com.app.wardove.ui.theme.StatusClean
import com.app.wardove.ui.util.formatDateShort
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryScreen(
    onOpenHistory: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    viewModel: LaundryViewModel = hiltViewModel()
) {
    val tab by viewModel.selectedTab.collectAsState()
    val pileEntries by viewModel.pileEntries.collectAsState()
    val selected by viewModel.selectedPileIds.collectAsState()
    val cycles by viewModel.activeCycles.collectAsState()
    val threshold by viewModel.threshold.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingCompleteCycle by remember { mutableStateOf<CycleWithItems?>(null) }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LargeTitleHeader(
                title = "Laundry",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            Lucide.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )

            PillTabRow(
                selected = tab,
                onSelect = viewModel::setTab
            )

            Spacer(Modifier.height(12.dp))

            when (tab) {
                LaundryTab.PILE -> PileTab(
                    entries = pileEntries,
                    selectedIds = selected,
                    threshold = threshold,
                    onToggle = viewModel::togglePileSelection,
                    onClearSelection = viewModel::clearPileSelection,
                    onSendToLaundry = viewModel::sendToLaundry,
                    onIncrementThreshold = viewModel::incrementThreshold,
                    onDecrementThreshold = viewModel::decrementThreshold,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                LaundryTab.WASHING -> WashingTab(
                    cycles = cycles,
                    onRequestComplete = { pendingCompleteCycle = it },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }

    pendingCompleteCycle?.let { cwi ->
        val count = cwi.items.size
        AlertDialog(
            onDismissRequest = { pendingCompleteCycle = null },
            title = { Text("Mark as clean?") },
            text = { Text("Mark $count item${if (count == 1) "" else "s"} as clean?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.completeCycle(cwi.cycle.id)
                        pendingCompleteCycle = null
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { pendingCompleteCycle = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PillTabRow(
    selected: LaundryTab,
    onSelect: (LaundryTab) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            LaundryTab.entries.forEach { entry ->
                val isSelected = entry == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onSelect(entry) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        entry.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PileTab(
    entries: List<PileEntry>?,
    selectedIds: Set<Long>,
    threshold: Int,
    onToggle: (Long) -> Unit,
    onClearSelection: () -> Unit,
    onSendToLaundry: () -> Unit,
    onIncrementThreshold: () -> Unit,
    onDecrementThreshold: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ThresholdControl(
            threshold = threshold,
            onIncrement = onIncrementThreshold,
            onDecrement = onDecrementThreshold
        )
        when {
            entries == null -> LoadingBox(modifier = Modifier.fillMaxSize())
            entries.isEmpty() -> EmptyMessage(
                "No dirty clothes. Nice!",
                modifier = Modifier.fillMaxSize()
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries, key = { it.item.id }) { entry ->
                        LaundryItemRow(
                            entry = entry,
                            selected = entry.item.id in selectedIds,
                            onToggle = { onToggle(entry.item.id) }
                        )
                    }
                }

                if (selectedIds.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClearSelection,
                            modifier = Modifier.height(52.dp)
                        ) { Text("Clear") }
                        Button(
                            onClick = onSendToLaundry,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LaundryPurple,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Send to Laundry",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThresholdControl(
    threshold: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Wash after",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
            Text("−", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Text(
            "$threshold wear${if (threshold == 1) "" else "s"}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
            Text("+", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
private fun LaundryItemRow(
    entry: PileEntry,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val item = entry.item
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = File(item.imagePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Worn ${item.lastWornDate?.formatDateShort() ?: "—"} · ${item.totalWearCount}× since last wash",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (entry.readyToWash) {
                    Text(
                        "Ready to wash",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = LaundryPurple,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Checkbox(
                checked = selected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = StatusClean,
                    checkmarkColor = Color.White,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun WashingTab(
    cycles: List<CycleWithItems>?,
    onRequestComplete: (CycleWithItems) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        cycles == null -> LoadingBox(modifier = modifier)
        cycles.isEmpty() -> EmptyMessage(
            "Nothing in the wash right now.",
            modifier = modifier
        )
        else -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cycles, key = { it.cycle.id }) { cwi ->
                CycleCard(
                    cwi = cwi,
                    onComplete = { onRequestComplete(cwi) }
                )
            }
        }
    }
}

@Composable
private fun CycleCard(
    cwi: CycleWithItems,
    onComplete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Started ${cwi.cycle.startedAt.formatDateShort()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${cwi.items.size} item${if (cwi.items.size == 1) "" else "s"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            cwi.items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = File(item.imagePath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        item.name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "Mark as Clean",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
