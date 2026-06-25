package com.app.wardove

import android.Manifest
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.lock.LockScreen
import com.app.wardove.ui.lock.LockViewModel
import com.app.wardove.ui.navigation.WardoveNavHost
import com.app.wardove.ui.theme.WardoveTheme
import com.app.wardove.ui.navigation.ShortcutActions
import com.app.wardove.ui.util.ISSUES_URL
import com.app.wardove.ui.util.openCustomTab
import com.app.wardove.util.ShakeDetector
import com.app.wardove.work.UpdateCheckWorker
import androidx.core.content.pm.ShortcutManagerCompat
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

    // Shake-to-report
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    @Volatile private var shakeEnabled = false
    private val shakeDetector = ShakeDetector(onShake = ::onShakeDetected)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Permission result — no-op; worker silently skips notify() if denied.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Capture deep-link route from the launching intent (cold start via notification or shortcut).
        notificationNavRoute = resolveNavRoute(intent)

        // Set up shake detection sensor
        sensorManager = getSystemService(SensorManager::class.java)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Track shake-to-report toggle from DataStore
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.settings.collect { shakeEnabled = it.shakeToReportEnabled }
            }
        }

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
        // Warm start: app was already running when notification or shortcut was tapped.
        notificationNavRoute = resolveNavRoute(intent)
    }

    override fun onStart() {
        super.onStart()
        if (System.currentTimeMillis() - stopTimestamp > LOCK_GRACE_MS) {
            lifecycleScope.launch { lockViewModel.lockIfEnabled() }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(shakeDetector, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            stopTimestamp = System.currentTimeMillis()
        }
    }

    /**
     * Resolves the Compose nav route to navigate to from an incoming intent.
     * Checks for a shortcut custom action first, then falls back to the
     * notification string extra so both paths share the same deepLinkRoute flow.
     */
    private fun resolveNavRoute(intent: Intent): String? {
        val shortcutRoute = ShortcutActions.routeForAction(intent.action)
        if (shortcutRoute != null) {
            ShortcutActions.shortcutIdForAction(intent.action)?.let { id ->
                ShortcutManagerCompat.reportShortcutUsed(this, id)
            }
            return shortcutRoute
        }
        return intent.getStringExtra(UpdateCheckWorker.EXTRA_NAVIGATE_TO)
    }

    private fun onShakeDetected() {
        if (!shakeEnabled) return
        vibrate()
        openCustomTab(this, ISSUES_URL)
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            val vibrator = getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                vibrator?.vibrate(50)
            }
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
