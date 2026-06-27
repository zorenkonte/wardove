package com.app.wardove.ui.update

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.wardove.BuildConfig
import com.app.wardove.R
import com.app.wardove.data.model.GithubRelease
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    onBack: () -> Unit,
    viewModel: UpdateViewModel = hiltViewModel()
) {
    val releasesState by viewModel.releasesState.collectAsState()
    val installState by viewModel.installState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val installLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.deleteApk()
        viewModel.resetInstall()
        if (result.resultCode == Activity.RESULT_OK) viewModel.load()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.update_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Lucide.ArrowLeft, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::load) {
                        Icon(Lucide.RefreshCw, contentDescription = stringResource(R.string.update_action_refresh))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
        when (val state = releasesState) {
            ReleasesState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ReleasesState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.update_failed_to_load),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = viewModel::load) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
            }

            is ReleasesState.Success -> {
                val releases = state.releases
                // Pick the highest-version stable release rather than the most
                // recently published one, so an older-dated-but-higher tag wins.
                val latest = latestStableRelease(releases)
                val history = releases.filter { it != latest }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    // Current version
                    item {
                        CurrentVersionCard()
                        Spacer(Modifier.height(12.dp))
                    }

                    // Latest release / up-to-date
                    if (latest != null) {
                        item {
                            LatestReleaseCard(
                                release = latest,
                                isUpdateAvailable = viewModel.isUpdateAvailable(latest.tagName),
                                installState = installState,
                                onDownload = {
                                    val apk = latest.assets.firstOrNull()
                                    if (apk != null) viewModel.startDownload(apk)
                                },
                                onInstall = { installLauncher.launch(viewModel.buildInstallIntent()) },
                                onResetInstall = viewModel::resetInstall
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // Release history
                    if (history.isNotEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.update_release_history),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                        items(history) { release ->
                            ReleaseHistoryItem(release = release)
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun CurrentVersionCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.update_installed_version),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LatestReleaseCard(
    release: GithubRelease,
    isUpdateAvailable: Boolean,
    installState: InstallState,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onResetInstall: () -> Unit
) {
    val apkSize = release.assets.firstOrNull()?.size ?: 0L
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUpdateAvailable)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isUpdateAvailable) stringResource(R.string.update_available)
                        else stringResource(R.string.update_up_to_date),
                        fontSize = 12.sp,
                        color = if (isUpdateAvailable)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        release.tagName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        formatDate(release.publishedAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!isUpdateAvailable) {
                    Icon(
                        Lucide.CircleCheck,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (release.body.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    release.body.take(300) + if (release.body.length > 300) "…" else "",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isUpdateAvailable && release.assets.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                when (installState) {
                    InstallState.Idle -> {
                        if (apkSize > 0L) {
                            Text(
                                stringResource(R.string.update_apk_size, formatBytes(apkSize)),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Lucide.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.update_download_install))
                        }
                    }
                    is InstallState.Downloading -> {
                        val fraction = installState.fraction
                        val downloadLabel = if (installState.totalBytes > 0L) {
                            stringResource(
                                R.string.update_downloading_progress,
                                formatBytes(installState.downloadedBytes),
                                formatBytes(installState.totalBytes),
                                formatSpeed(installState.bytesPerSec),
                                (fraction * 100).toInt()
                            )
                        } else {
                            stringResource(
                                R.string.update_downloading_percent,
                                (fraction * 100).toInt()
                            )
                        }
                        // Ease the bar between the 250ms polls so it fills
                        // smoothly instead of stepping.
                        val animatedProgress by animateFloatAsState(
                            targetValue = fraction,
                            animationSpec = tween(durationMillis = 300),
                            label = "downloadProgress"
                        )
                        Column {
                            Text(
                                downloadLabel,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    InstallState.ReadyToInstall -> {
                        Button(
                            onClick = onInstall,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.update_install_now))
                        }
                    }
                    InstallState.Failed -> {
                        Column {
                            Text(
                                stringResource(R.string.update_download_failed),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(4.dp))
                            OutlinedButton(onClick = onResetInstall, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseHistoryItem(release: GithubRelease) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                release.tagName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                formatDate(release.publishedAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (release.body.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                release.body.lines().firstOrNull()?.take(80) ?: "",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1.0) "%.1f MB".format(mb) else "%d KB".format(bytes / 1024)
}

private fun formatSpeed(bytesPerSec: Long): String =
    if (bytesPerSec <= 0L) "—" else "${formatBytes(bytesPerSec)}/s"

private fun formatDate(iso: String): String = try {
    val instant = Instant.parse(iso)
    DateTimeFormatter.ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault())
        .format(instant)
} catch (_: Exception) {
    iso.take(10)
}
