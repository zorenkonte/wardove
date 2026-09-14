package com.app.wardove.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.wardove.R
import com.app.wardove.data.backup.ImportResult
import com.app.wardove.ui.components.WardoveLoadingIndicator
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Upload
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val isBusy by viewModel.isBusy.collectAsState()
    val result by viewModel.result.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showImportConfirm by remember { mutableStateOf(false) }

    val exportSuccessMsg = stringResource(R.string.backup_message_export_success)
    val importSuccessMsg = stringResource(R.string.backup_message_import_success)

    val failedMsg = stringResource(R.string.backup_message_failed)

    LaunchedEffect(result) {
        val outcome = result ?: return@LaunchedEffect
        when (val r = outcome.result) {
            is ImportResult.Success -> {
                val template = if (outcome.operation == BackupOperation.EXPORT) exportSuccessMsg else importSuccessMsg
                snackbarHostState.showSnackbar(String.format(template, r.itemCount))
            }
            is ImportResult.Error -> {
                snackbarHostState.showSnackbar(String.format(failedMsg, r.message))
            }
        }
        viewModel.onResultShown()
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> uri?.let(viewModel::export) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::import) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_backup_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Lucide.ArrowLeft, contentDescription = stringResource(R.string.action_back))
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            SettingsCard {
                BackupRow(
                    icon = Lucide.Download,
                    label = stringResource(R.string.backup_export_label),
                    subtitle = stringResource(R.string.backup_export_subtitle),
                    enabled = !isBusy,
                    onClick = { exportLauncher.launch(defaultBackupFileName()) }
                )
            }

            SettingsCard {
                BackupRow(
                    icon = Lucide.Upload,
                    label = stringResource(R.string.backup_import_label),
                    subtitle = stringResource(R.string.backup_import_subtitle),
                    enabled = !isBusy,
                    onClick = { showImportConfirm = true }
                )
            }

            Text(
                stringResource(R.string.backup_help_text),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (isBusy) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    WardoveLoadingIndicator(modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }

    if (showImportConfirm) {
        AlertDialog(
            onDismissRequest = { showImportConfirm = false },
            title = { Text(stringResource(R.string.backup_import_dialog_title)) },
            text = { Text(stringResource(R.string.backup_import_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportConfirm = false
                        importLauncher.launch(arrayOf("application/zip"))
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.backup_import_label)) }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun BackupRow(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
            if (subtitle != null) {
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun defaultBackupFileName(): String {
    val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
    return "wardove-backup-$stamp.zip"
}
