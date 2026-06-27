package com.app.wardove.ui.additem

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareItemSheet(
    sharedImageUri: Uri,
    onSaved: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: ShareItemViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sharedImageUri) {
        viewModel.loadSharedImage(sharedImageUri)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.share_item_title),
                style = MaterialTheme.typography.titleLarge
            )

            // Photo preview — shared image, no picker buttons needed
            val imagePath = state.imagePath
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
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
                    CircularProgressIndicator()
                }
            }

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
                Text(
                    stringResource(R.string.add_item_field_color),
                    style = MaterialTheme.typography.labelLarge
                )
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
                    Text(stringResource(R.string.add_item_action_save))
                }
            }

            // Missing fields hint
            val photoLabel    = stringResource(R.string.add_item_required_photo)
            val nameLabel     = stringResource(R.string.add_item_required_name)
            val categoryLabel = stringResource(R.string.add_item_required_category)
            val colorLabel    = stringResource(R.string.add_item_required_color)
            val missing = buildList {
                if (state.imagePath.isNullOrBlank()) add(photoLabel)
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
