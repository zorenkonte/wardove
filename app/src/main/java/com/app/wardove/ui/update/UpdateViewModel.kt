package com.app.wardove.ui.update

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.BuildConfig
import com.app.wardove.data.model.GithubAsset
import com.app.wardove.data.model.GithubRelease
import com.app.wardove.data.repository.DownloadStatus
import com.app.wardove.data.repository.UpdateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReleasesState {
    data object Loading : ReleasesState
    data class Success(val releases: List<GithubRelease>) : ReleasesState
    data class Error(val message: String) : ReleasesState
}

sealed interface InstallState {
    data object Idle : InstallState
    data class Downloading(
        val downloadedBytes: Long,
        val totalBytes: Long,
        val bytesPerSec: Long
    ) : InstallState {
        val fraction: Float
            get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
    }
    data object ReadyToInstall : InstallState
    data object Failed : InstallState
}

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val repository: UpdateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _releasesState = MutableStateFlow<ReleasesState>(ReleasesState.Loading)
    val releasesState = _releasesState.asStateFlow()

    private val _installState = MutableStateFlow<InstallState>(InstallState.Idle)
    val installState = _installState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var pollJob: Job? = null
    private var activeDownloadId: Long = -1L

    // Speed tracking between polls (EMA-smoothed to avoid jitter).
    private var lastBytes: Long = 0L
    private var lastTimeMs: Long = 0L
    private var smoothedBytesPerSec: Double = 0.0

    init {
        repository.deleteApkFile()
        load()
    }

    fun load() {
        viewModelScope.launch {
            _releasesState.value = ReleasesState.Loading
            _releasesState.value = try {
                ReleasesState.Success(repository.fetchReleases())
            } catch (e: Exception) {
                ReleasesState.Error(e.message ?: "Failed to fetch releases")
            }
        }
    }

    /**
     * Pull-to-refresh re-fetch. Unlike [load] this keeps the current list visible
     * (no [ReleasesState.Loading]) and drives the swipe indicator via [isRefreshing].
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _releasesState.value = try {
                    ReleasesState.Success(repository.fetchReleases())
                } catch (e: Exception) {
                    ReleasesState.Error(e.message ?: "Failed to fetch releases")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun isUpdateAvailable(latestTag: String): Boolean =
        compareVersions(latestTag, BuildConfig.VERSION_NAME) > 0

    fun startDownload(asset: GithubAsset) {
        if (_installState.value is InstallState.Downloading) return
        // Use the known asset size as the total from the very first frame so the
        // bar shows real progress instead of sitting at 0 until DownloadManager
        // learns the size.
        val knownTotal = asset.size
        resetSpeedTracking()
        _installState.value = InstallState.Downloading(0L, knownTotal, 0L)
        activeDownloadId = repository.enqueueDownload(asset)
        pollJob = viewModelScope.launch {
            while (true) {
                when (val status = repository.queryDownload(activeDownloadId)) {
                    is DownloadStatus.Progress -> {
                        val total = if (status.totalBytes > 0) status.totalBytes else knownTotal
                        val speed = updateSpeed(status.downloadedBytes)
                        _installState.value =
                            InstallState.Downloading(status.downloadedBytes, total, speed)
                    }
                    DownloadStatus.Complete -> {
                        _installState.value = InstallState.ReadyToInstall
                        return@launch
                    }
                    DownloadStatus.Failed -> {
                        _installState.value = InstallState.Failed
                        return@launch
                    }
                    DownloadStatus.Unknown -> {}
                }
                delay(250)
            }
        }
    }

    private fun resetSpeedTracking() {
        lastBytes = 0L
        lastTimeMs = 0L
        smoothedBytesPerSec = 0.0
    }

    /** Updates the EMA-smoothed download speed from the latest byte count. */
    private fun updateSpeed(downloadedBytes: Long): Long {
        val now = System.currentTimeMillis()
        if (lastTimeMs == 0L) {
            lastBytes = downloadedBytes
            lastTimeMs = now
            return 0L
        }
        val elapsedMs = now - lastTimeMs
        if (elapsedMs <= 0L) return smoothedBytesPerSec.toLong()
        val instant = (downloadedBytes - lastBytes).coerceAtLeast(0L) * 1000.0 / elapsedMs
        // EMA so the readout doesn't bounce between polls.
        smoothedBytesPerSec =
            if (smoothedBytesPerSec == 0.0) instant else 0.6 * smoothedBytesPerSec + 0.4 * instant
        lastBytes = downloadedBytes
        lastTimeMs = now
        return smoothedBytesPerSec.toLong()
    }

    fun buildInstallIntent(): Intent {
        val uri = repository.getInstallUri()
        return Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, context.packageName)
        }
    }

    fun deleteApk() {
        repository.deleteApkFile()
    }

    fun resetInstall() {
        pollJob?.cancel()
        resetSpeedTracking()
        _installState.value = InstallState.Idle
    }
}

/**
 * Returns the highest-version non-prerelease release from [releases], or null if none.
 * Uses [compareVersions] for numeric ordering so a higher semver tag always wins
 * over a more-recently published one.
 */
fun latestStableRelease(releases: List<com.app.wardove.data.model.GithubRelease>): com.app.wardove.data.model.GithubRelease? =
    releases
        .filter { !it.prerelease }
        .maxWithOrNull { a, b -> compareVersions(a.tagName, b.tagName) }

/**
 * Compares two dotted version strings (e.g. "2.0.24" vs "v1.0.23") numerically,
 * tolerating a leading 'v' and differing segment counts. Non-numeric segments are
 * treated as 0. Returns >0 if [a] is newer than [b], <0 if older, 0 if equal.
 */
fun compareVersions(a: String, b: String): Int {
    fun parts(v: String) = v.trim().trimStart('v', 'V')
        .split('.')
        .map { segment -> segment.takeWhile { it.isDigit() }.toIntOrNull() ?: 0 }
    val pa = parts(a)
    val pb = parts(b)
    for (i in 0 until maxOf(pa.size, pb.size)) {
        val diff = (pa.getOrElse(i) { 0 }) - (pb.getOrElse(i) { 0 })
        if (diff != 0) return diff
    }
    return 0
}
