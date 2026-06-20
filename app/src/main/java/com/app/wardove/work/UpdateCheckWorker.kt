package com.app.wardove.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.wardove.BuildConfig
import com.app.wardove.MainActivity
import com.app.wardove.R
import com.app.wardove.WardoveApplication
import com.app.wardove.data.repository.UpdateRepository
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.update.compareVersions
import com.app.wardove.ui.update.latestStableRelease
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class UpdateCheckWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateRepository: UpdateRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val releases = updateRepository.fetchReleases()
            val latest = latestStableRelease(releases) ?: return Result.success()

            // Up to date — nothing to notify
            if (compareVersions(latest.tagName, BuildConfig.VERSION_NAME) <= 0) {
                return Result.success()
            }

            // De-dupe: already notified for this tag
            val lastNotified = settingsRepository.lastNotifiedUpdateTag.first()
            if (lastNotified == latest.tagName) {
                return Result.success()
            }

            postNotification(latest.tagName)
            settingsRepository.setLastNotifiedUpdateTag(latest.tagName)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun postNotification(tagName: String) {
        // Runtime permission required on Android 13+
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, NAVIGATE_TO_UPDATE)
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, WardoveApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Update available")
            .setContentText("Wardove $tagName is ready to install")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val EXTRA_NAVIGATE_TO = "navigate_to"
        const val NAVIGATE_TO_UPDATE = "update"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE = 0
    }
}
