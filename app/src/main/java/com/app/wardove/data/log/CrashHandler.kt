package com.app.wardove.data.log

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

/**
 * Installs an uncaught-exception handler that synchronously writes a FATAL entry to
 * [FileLoggingTree] before delegating to the previously-installed handler.
 * Synchronous write is intentional: it must survive process death.
 */
fun installCrashHandler(tree: FileLoggingTree, header: String) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        val timestamp = LocalDateTime.now().format(formatter)
        val entry = buildString {
            append("$timestamp FATAL/crash: Uncaught exception on thread '${thread.name}'\n")
            append(header)
            append("\n")
            append(android.util.Log.getStackTraceString(throwable))
            append("\n")
        }
        tree.appendRaw(entry)
        previous?.uncaughtException(thread, throwable)
    }
}
