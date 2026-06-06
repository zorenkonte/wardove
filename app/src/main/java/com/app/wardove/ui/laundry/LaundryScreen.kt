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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.data.local.entity.ClothingItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryScreen(
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: LaundryViewModel = hiltViewModel()
) {
    val tab by viewModel.selectedTab.collectAsState()
    val pile by viewModel.pile.collectAsState()
    val selected by viewModel.selectedPileIds.collectAsState()
    val cycles by viewModel.activeCycles.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingCompleteCycle by remember { mutableStateOf<CycleWithItems?>(null) }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laundry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = tab.ordinal) {
                LaundryTab.entries.forEach { entry ->
                    Tab(
                        selected = entry == tab,
                        onClick = { viewModel.setTab(entry) },
                        text = { Text(entry.label) }
                    )
                }
            }

            when (tab) {
                LaundryTab.PILE -> PileTab(
                    items = pile,
                    selectedIds = selected,
                    onToggle = viewModel::togglePileSelection,
                    onClearSelection = viewModel::clearPileSelection,
                    onSendToLaundry = viewModel::sendToLaundry,
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
private fun PileTab(
    items: List<ClothingItem>?,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
    onClearSelection: () -> Unit,
    onSendToLaundry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when {
            items == null -> LoadingBox(modifier = Modifier.fillMaxSize())
            items.isEmpty() -> EmptyMessage("No dirty clothes. Nice!", modifier = Modifier.fillMaxSize())
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        PileRow(
                            item = item,
                            selected = item.id in selectedIds,
                            onToggle = { onToggle(item.id) }
                        )
                    }
                }

                if (selectedIds.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClearSelection,
                            modifier = Modifier.weight(1f)
                        ) { Text("Clear") }
                        Button(
                            onClick = onSendToLaundry,
                            modifier = Modifier.weight(2f)
                        ) {
                            Text("Send to Laundry (${selectedIds.size})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PileRow(
    item: ClothingItem,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = selected, onCheckedChange = { onToggle() })
            Spacer(Modifier.size(8.dp))
            ThumbImage(path = item.imagePath, contentDescription = item.name)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Last worn: " + (item.lastWornDate?.let { formatDate(it) } ?: "Never"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
        cycles.isEmpty() -> EmptyMessage("Nothing in the wash right now.", modifier = modifier)
        else -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
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
    Card(modifier = Modifier.fillMaxWidth()) {
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
                        "Started ${formatDate(cwi.cycle.startedAt)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${cwi.items.size} item${if (cwi.items.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            cwi.items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ThumbImage(path = item.imagePath, contentDescription = item.name)
                    Spacer(Modifier.size(12.dp))
                    Text(
                        item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mark as Clean")
            }
        }
    }
}

@Composable
private fun ThumbImage(path: String, contentDescription: String?) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = File(path),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EmptyMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(epochMillis))
