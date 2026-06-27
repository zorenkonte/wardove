package com.app.wardove.ui.additem

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Images
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.R
import com.app.wardove.ui.util.ClothingOptions
import com.app.wardove.ui.util.parseHexColor
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> viewModel.onCameraResult(success) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::onGalleryUri) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) stringResource(R.string.edit_item_title)
                        else stringResource(R.string.add_item_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Lucide.ArrowLeft, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save(onSaved) },
                        enabled = state.canSave
                    ) {
                        Icon(Lucide.Check, contentDescription = stringResource(R.string.action_save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePicker(
                imagePath = state.imagePath,
                onCamera = {
                    val uri = viewModel.prepareCameraCapture()
                    cameraLauncher.launch(uri)
                },
                onGallery = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text(stringResource(R.string.add_item_field_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            CategoryDropdown(
                selected = state.category,
                onSelect = viewModel::setCategory
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.add_item_field_color), style = MaterialTheme.typography.labelLarge)
                ColorGrid(
                    selected = state.color,
                    onSelect = viewModel::setColor
                )
            }

            OutlinedTextField(
                value = state.price,
                onValueChange = viewModel::setPrice,
                label = {
                    Text(
                        stringResource(R.string.add_item_field_price),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                prefix = { Text("₱ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::setNotes,
                label = { Text(stringResource(R.string.add_item_field_notes)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        if (state.isEditing) stringResource(R.string.add_item_action_update)
                        else stringResource(R.string.add_item_action_save)
                    )
                }
            }

            val nameLabel     = stringResource(R.string.add_item_required_name)
            val categoryLabel = stringResource(R.string.add_item_required_category)
            val colorLabel    = stringResource(R.string.add_item_required_color)
            val missing = buildList {
                if (state.name.isBlank()) add(nameLabel)
                if (state.category.isBlank()) add(categoryLabel)
                if (state.color.isBlank()) add(colorLabel)
            }
            if (missing.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.add_item_required_prefix, missing.joinToString(", ")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ImagePicker(
    imagePath: String?,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                stringResource(R.string.add_item_no_photo),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onCamera,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Lucide.Camera, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.add_item_action_camera))
        }
        OutlinedButton(
            onClick = onGallery,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Lucide.Images, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.add_item_action_gallery))
        }
    }
}

@Composable
internal fun CategoryDropdown(
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.add_item_field_category),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val displaySelected = ClothingOptions.categoryResId(selected)
                        ?.let { stringResource(it) }
                        ?: selected
                    Text(
                        displaySelected.ifBlank { stringResource(R.string.add_item_select_category) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selected.isBlank())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(Lucide.ChevronDown, contentDescription = null)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            ClothingOptions.categories.forEach { cat ->
                val displayName = ClothingOptions.categoryResId(cat)
                    ?.let { stringResource(it) }
                    ?: cat
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onSelect(cat)  // store the DB key (EN string)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
internal fun ColorGrid(
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        items(ClothingOptions.colors) { c ->
            val isSelected = c.hex.equals(selected, ignoreCase = true)
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(parseHexColor(c.hex))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onSelect(c.hex) }
            )
        }
    }
}
