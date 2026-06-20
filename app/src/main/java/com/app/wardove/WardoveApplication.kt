package com.app.wardove

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.app.wardove.work.UpdateCheckWorker
import com.app.wardove.BuildConfig
import com.app.wardove.data.log.DiagnosticsRepository
import com.app.wardove.data.log.FileLoggingTree
import com.app.wardove.data.log.installCrashHandler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WardoveApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var fileLoggingTree: FileLoggingTree
    @Inject lateinit var diagnosticsRepository: DiagnosticsRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Plant logging trees before anything else so startup events are captured.
        Timber.plant(fileLoggingTree)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        installCrashHandler(fileLoggingTree, diagnosticsRepository.header())
        Timber.i("App started — ${diagnosticsRepository.header()}")
        createNotificationChannel()
        scheduleUpdateCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when a new version of Wardove is available"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun scheduleUpdateCheck() {
        val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UPDATE_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        const val CHANNEL_ID = "updates"
        const val UPDATE_CHECK_WORK_NAME = "update_check"
    }
}
