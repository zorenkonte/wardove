package com.app.wardove.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.app.wardove.R
import com.app.wardove.data.model.GithubAsset
import com.app.wardove.data.model.GithubRelease
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val RELEASES_URL = "https://api.github.com/repos/zorenkonte/wardove/releases"
private const val APK_FILENAME = "wardove-update.apk"

@Singleton
class UpdateRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun fetchReleases(): List<GithubRelease> = withContext(Dispatchers.IO) {
        val conn = URL(RELEASES_URL).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            parseReleases(conn.inputStream.bufferedReader().readText())
        } finally {
            conn.disconnect()
        }
    }

    fun enqueueDownload(asset: GithubAsset): Long {
        val request = DownloadManager.Request(Uri.parse(asset.browserDownloadUrl))
            .setTitle(context.getString(R.string.download_manager_title))
            .setDescription(context.getString(R.string.download_manager_description, asset.name))
            .setDestinationInExternalFilesDir(context, null, APK_FILENAME)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        return downloadManager().enqueue(request)
    }

    fun queryDownload(downloadId: Long): DownloadStatus {
        val cursor = downloadManager().query(DownloadManager.Query().setFilterById(downloadId))
        cursor.use { c ->
            if (!c.moveToFirst()) return DownloadStatus.Unknown
            val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            val downloaded = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val total = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Complete
                DownloadManager.STATUS_FAILED -> DownloadStatus.Failed
                else -> if (total > 0) DownloadStatus.Progress(downloaded.toFloat() / total) else DownloadStatus.Progress(0f)
            }
        }
    }

    fun getInstallUri(): Uri {
        val file = File(context.getExternalFilesDir(null), APK_FILENAME)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun deleteApkFile() {
        File(context.getExternalFilesDir(null), APK_FILENAME).delete()
    }

    private fun downloadManager() = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun parseReleases(json: String): List<GithubRelease> {
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val assets = obj.getJSONArray("assets")
            GithubRelease(
                tagName = obj.getString("tag_name"),
                name = obj.optString("name", obj.getString("tag_name")),
                body = obj.optString("body", ""),
                publishedAt = obj.getString("published_at"),
                htmlUrl = obj.getString("html_url"),
                prerelease = obj.optBoolean("prerelease", false),
                assets = (0 until assets.length()).mapNotNull { j ->
                    val a = assets.getJSONObject(j)
                    val name = a.getString("name")
                    if (name.endsWith(".apk")) {
                        GithubAsset(
                            name = name,
                            browserDownloadUrl = a.getString("browser_download_url"),
                            size = a.getLong("size")
                        )
                    } else null
                }
            )
        }
    }
}

sealed interface DownloadStatus {
    data object Unknown : DownloadStatus
    data class Progress(val fraction: Float) : DownloadStatus
    data object Complete : DownloadStatus
    data object Failed : DownloadStatus
}
