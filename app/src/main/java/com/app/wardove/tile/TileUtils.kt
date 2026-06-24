package com.app.wardove.tile

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.app.wardove.MainActivity

/**
 * Launches [MainActivity] with the given intent [action], collapsing the Quick Settings panel.
 * Uses the [PendingIntent] overload on API 34+ (required) and falls back to the
 * direct-Intent overload on older versions.
 */
internal fun TileService.launchMainActivity(action: String) {
    val intent = Intent(this, MainActivity::class.java).apply {
        this.action = action
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pi)
    } else {
        @Suppress("DEPRECATION")
        startActivityAndCollapse(intent)
    }
}
