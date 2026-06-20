package com.app.wardove.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/**
 * Opens [url] in a Chrome Custom Tab themed to the app's primary color (#1A1A1A toolbar).
 * Falls back to a plain ACTION_VIEW intent if no Custom Tabs-compatible browser is installed.
 */
fun openCustomTab(context: Context, url: String) {
    val uri: Uri = url.toUri()
    try {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(0xFF1A1A1A.toInt())
                    .build()
            )
            .build()
            .launchUrl(context, uri)
    } catch (_: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
