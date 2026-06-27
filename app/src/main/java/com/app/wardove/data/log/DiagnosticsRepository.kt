package com.app.wardove.data.log

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.app.wardove.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tree: FileLoggingTree
) {
    /** One-line header with app + device info, included at the top of every export. */
    fun header(): String =
        "Wardove ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) | " +
        "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT}) | " +
        "${Build.MANUFACTURER} ${Build.MODEL}"

    /**
     * Returns header + full log content (backup then current, oldest-first).
     * Empty string when no log files exist.
     */
    suspend fun readLog(): String = withContext(Dispatchers.IO) {
        val backup = tree.logFile.parentFile?.let {
            val b = java.io.File(it, "wardove.log.1")
            if (b.exists()) b.readText() else ""
        } ?: ""
        val current = if (tree.logFile.exists()) tree.logFile.readText() else ""
        if (backup.isEmpty() && current.isEmpty()) return@withContext ""
        buildString {
            append("=== Wardove Diagnostic Log ===\n")
            append(header())
            append("\n==============================\n\n")
            if (backup.isNotEmpty()) append(backup)
            if (current.isNotEmpty()) append(current)
        }
    }

    /**
     * Writes the full log to a FileProvider-served cache file and returns its Uri.
     * The file lives under cacheDir/logs/ — the OS reclaims it automatically.
     * Throws on I/O failure — caller catches.
     */
    suspend fun writeShareFile(): Uri = withContext(Dispatchers.IO) {
        val text = readLog().ifEmpty { header() + "\n(no log entries yet)\n" }
        val dir = File(context.cacheDir, "logs").apply { mkdirs() }
        val ts = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val file = File(dir, "wardove-log-$ts.txt")
        file.writeText(text)
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** Deletes current and backup log files. */
    suspend fun clear() = withContext(Dispatchers.IO) {
        tree.logFile.takeIf { it.exists() }?.delete()
        tree.logFile.parentFile?.let {
            val backup = java.io.File(it, "wardove.log.1")
            backup.takeIf { b -> b.exists() }?.delete()
        }
    }
}
