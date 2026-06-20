package com.app.wardove.data.log

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private const val LOG_SUBDIR = "logs"
private const val LOG_FILE = "wardove.log"
private const val LOG_BACKUP = "wardove.log.1"
private const val MAX_LOG_SIZE_BYTES = 512 * 1024L // 512 KB

@Singleton
class FileLoggingTree @Inject constructor(
    @ApplicationContext private val context: Context
) : Timber.Tree() {

    private val logDir: File
        get() = File(context.filesDir, LOG_SUBDIR).apply { mkdirs() }

    val logFile: File get() = File(logDir, LOG_FILE)
    private val backupFile: File get() = File(logDir, LOG_BACKUP)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val level = when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG   -> "D"
            Log.INFO    -> "I"
            Log.WARN    -> "W"
            Log.ERROR   -> "E"
            Log.ASSERT  -> "A"
            else        -> "?"
        }
        val line = buildString {
            append(LocalDateTime.now().format(formatter))
            append(" $level/${tag ?: "app"}: $message")
            if (t != null) {
                append("\n")
                append(Log.getStackTraceString(t))
            }
            append("\n")
        }
        appendToFile(line)
    }

    fun appendRaw(text: String) {
        appendToFile(text)
    }

    @Synchronized
    private fun appendToFile(text: String) {
        val file = logFile
        if (file.exists() && file.length() >= MAX_LOG_SIZE_BYTES) {
            backupFile.delete()
            file.renameTo(backupFile)
        }
        FileWriter(file, /* append = */ true).use { it.write(text) }
    }
}
