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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.app.wardove.ui.util.formatDateShort
import com.app.wardove.ui.wardrobe.WardoveBottomBar
import com.app.wardove.ui.wardrobe.WardroveBottomRoute
import java.io.File

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
        containerColor = Color(0xFFF7F5F2),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Laundry",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color(0xFF1A1A1A)
                    )
                },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F5F2)
                )
            )
        },
        bottomBar = {
            WardoveBottomBar(
                currentRoute = WardroveBottomRoute.LAUNDRY,
                onSelectWardrobe = onBack,
                onSelectLaundry = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PillTabRow(
                selected = tab,
                onSelect = viewModel::setTab
            )

            Spacer(Modifier.height(12.dp))

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
private fun PillTabRow(
    selected: LaundryTab,
    onSelect: (LaundryTab) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(Color(0xFFEBE8E3), RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            LaundryTab.entries.forEach { entry ->
                val isSelected = entry == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onSelect(entry) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        entry.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF888888)
                    )
                }
            }
        }
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
            items.isEmpty() -> EmptyMessage(
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
                    items(items, key = { it.id }) { item ->
                        LaundryItemRow(
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
                                containerColor = Color(0xFF7F77DD),
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
private fun LaundryItemRow(
    item: ClothingItem,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Worn ${item.lastWornDate?.formatDateShort() ?: "—"}",
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Checkbox(
                checked = selected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF5DCAA5),
                    checkmarkColor = Color.White,
                    uncheckedColor = Color(0xFFDDDDDD)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "${cwi.items.size} item${if (cwi.items.size == 1) "" else "s"}",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
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
                        color = Color(0xFF1A1A1A),
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
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
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
        Text(text, color = Color(0xFF888888), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
