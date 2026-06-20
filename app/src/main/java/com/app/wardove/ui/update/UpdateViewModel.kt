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
    data class Downloading(val progress: Float) : InstallState
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

    private var pollJob: Job? = null
    private var activeDownloadId: Long = -1L

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

    fun isUpdateAvailable(latestTag: String): Boolean =
        compareVersions(latestTag, BuildConfig.VERSION_NAME) > 0

    fun startDownload(asset: GithubAsset) {
        if (_installState.value is InstallState.Downloading) return
        _installState.value = InstallState.Downloading(0f)
        activeDownloadId = repository.enqueueDownload(asset)
        pollJob = viewModelScope.launch {
            while (true) {
                when (val status = repository.queryDownload(activeDownloadId)) {
                    is DownloadStatus.Progress -> _installState.value = InstallState.Downloading(status.fraction)
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
                delay(500)
            }
        }
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
        _installState.value = InstallState.Idle
    }

    fun openFeedback() {
        val intent = Intent(Intent.ACTION_VIEW, "https://github.com/zorenkonte/wardove/issues".toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
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
