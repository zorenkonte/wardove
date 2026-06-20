package com.app.wardove

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.lock.LockScreen
import com.app.wardove.ui.lock.LockViewModel
import com.app.wardove.ui.navigation.WardoveNavHost
import com.app.wardove.ui.theme.WardoveTheme
import com.app.wardove.work.UpdateCheckWorker
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val lockViewModel: LockViewModel by viewModels()

    private var stopTimestamp = 0L
    private val LOCK_GRACE_MS = 1_000L

    private lateinit var biometricPrompt: BiometricPrompt

    // Route to navigate to when opened from a notification; updated on new Intent as well.
    private var notificationNavRoute by mutableStateOf<String?>(null)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Permission result — no-op; worker silently skips notify() if denied.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Capture deep-link route from the launching intent (cold start via notification).
        notificationNavRoute = intent.getStringExtra(UpdateCheckWorker.EXTRA_NAVIGATE_TO)

        // Request POST_NOTIFICATIONS on Android 13+ the first time the app launches.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    lockViewModel.onBiometricSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    lockViewModel.onBiometricRequestConsumed()
                }

                override fun onAuthenticationFailed() {
                    // BiometricPrompt handles retries internally; do nothing
                }
            }
        )

        val initialSettings = runBlocking { settingsRepository.settings.first() }

        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = initialSettings)
            val isLocked by lockViewModel.isLocked.collectAsState()
            val biometricRequest by lockViewModel.biometricRequest.collectAsState()

            LaunchedEffect(biometricRequest) {
                if (biometricRequest) {
                    showBiometricPrompt()
                    lockViewModel.onBiometricRequestConsumed()
                }
            }

            WardoveTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    WardoveNavHost(
                        deepLinkRoute = notificationNavRoute,
                        onDeepLinkConsumed = { notificationNavRoute = null }
                    )
                    if (isLocked) {
                        LockScreen(onBiometricRequest = lockViewModel::requestBiometric)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Warm start: app was already running when notification was tapped.
        notificationNavRoute = intent.getStringExtra(UpdateCheckWorker.EXTRA_NAVIGATE_TO)
    }

    override fun onStart() {
        super.onStart()
        if (System.currentTimeMillis() - stopTimestamp > LOCK_GRACE_MS) {
            lifecycleScope.launch { lockViewModel.lockIfEnabled() }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            stopTimestamp = System.currentTimeMillis()
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Wardove")
            .setSubtitle("Use your biometric credential")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}
