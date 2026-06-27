package com.app.wardove

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.IntentCompat
import androidx.fragment.app.FragmentActivity
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.additem.ShareItemSheet
import com.app.wardove.ui.theme.WardoveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ShareReceiverActivity : FragmentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uri: Uri? = if (
            intent.action == Intent.ACTION_SEND &&
            intent.type?.startsWith("image/") == true
        ) {
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
        } else null

        if (uri == null) {
            finish()
            return
        }

        val initialSettings = runBlocking { settingsRepository.settings.first() }

        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = initialSettings)

            WardoveTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                ShareItemSheet(
                    sharedImageUri = uri,
                    onSaved = {
                        Toast.makeText(
                            this,
                            getString(R.string.share_item_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    },
                    onDismiss = { finish() }
                )
            }
        }
    }
}
