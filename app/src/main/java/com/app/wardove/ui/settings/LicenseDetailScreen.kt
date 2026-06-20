package com.app.wardove.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.wardove.R
import com.app.wardove.ui.util.openCustomTab
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseDetailScreen(
    libraryId: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val libraries by produceLibraries {
        context.resources.openRawResource(R.raw.aboutlibraries).bufferedReader().readText()
    }

    val library = libraries?.libraries?.find { it.uniqueId == libraryId }
    val license = library?.licenses?.firstOrNull()
    val licenseText = license?.licenseContent ?: license?.url ?: ""
    val websiteUrl = library?.website?.takeIf { it.isNotBlank() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = library?.name ?: "",
                        maxLines = 2,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Lucide.ArrowLeft,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    if (websiteUrl != null) {
                        OutlinedButton(
                            onClick = { openCustomTab(context, websiteUrl) },
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Text(stringResource(R.string.action_open))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Text(
            text = licenseText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
        )
    }
}
