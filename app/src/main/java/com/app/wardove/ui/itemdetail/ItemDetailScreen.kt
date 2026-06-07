package com.app.wardove.ui.itemdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.WearLog
import com.app.wardove.ui.util.ClothingOptions
import com.app.wardove.ui.util.formatDateOnly
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState()
    val logs by viewModel.wearLogs.collectAsState()
    val wornToday by viewModel.wornToday.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF7F5F2),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(itemId) }, enabled = item != null) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = item != null
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F5F2)
                )
            )
        }
    ) { padding ->
        val current = item
        when {
            current == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            else -> ItemDetailBody(
                item = current,
                wearLogs = logs,
                wornToday = wornToday,
                onWearToday = viewModel::wearToday,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this item?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete(onDeleted)
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ItemDetailBody(
    item: ClothingItem,
    wearLogs: List<WearLog>,
    wornToday: Boolean,
    onWearToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEBE8E3))
        ) {
            AsyncImage(
                model = File(item.imagePath),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = item.name,
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(top = 8.dp)
        )

        TagsRow(item = item)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(
                label = "Times worn",
                value = "${item.totalWearCount}",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Last worn",
                value = item.lastWornDate?.formatDateOnly() ?: "Never",
                modifier = Modifier.weight(1f)
            )
        }

        if (!item.notes.isNullOrBlank()) {
            Column {
                Text(
                    "Notes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF888888)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    item.notes,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A)
                )
            }
        }

        val haptic = LocalHapticFeedback.current
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onWearToday()
            },
            enabled = !wornToday,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFEBE8E3),
                disabledContentColor = Color(0xFFAAAAAA)
            )
        ) {
            Text(
                if (wornToday) "Already worn today" else "Wear Today",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        WearHistorySection(logs = wearLogs)

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TagsRow(item: ClothingItem) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TagPill(
            label = item.category,
            backgroundColor = Color(0xFFEBE8E3),
            textColor = Color(0xFF555555)
        )
        TagPill(
            label = ClothingOptions.colorNameFor(item.color),
            backgroundColor = Color(0xFF1A1A1A),
            textColor = Color.White
        )
    }
}

@Composable
private fun TagPill(label: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFEBE8E3), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            color = Color(0xFF888888)
        )
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun WearHistorySection(logs: List<WearLog>) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            "Wear history",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF888888),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (logs.isEmpty()) {
            Text(
                "No wears recorded yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888)
            )
        } else {
            logs.forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF5DCAA5), CircleShape)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = log.wornDate.formatDateOnly(),
                        fontSize = 13.sp,
                        color = Color(0xFF555555)
                    )
                }
                HorizontalDivider(color = Color(0xFFE0DDD8), thickness = 0.5.dp)
            }
        }
    }
}
