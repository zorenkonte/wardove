package com.app.wardove

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.wardove.data.repository.ClothingRepository
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.lock.LockScreen
import com.app.wardove.ui.lock.LockViewModel
import com.app.wardove.ui.navigation.ShortcutActions
import com.app.wardove.ui.navigation.WardoveNavHost
import com.app.wardove.ui.onboarding.OnboardingScreen
import com.app.wardove.ui.theme.WardoveTheme
import com.app.wardove.ui.theme.isDarkTheme
import com.app.wardove.ui.util.ISSUES_URL
import com.app.wardove.ui.util.openCustomTab
import com.app.wardove.util.ShakeDetector
import com.app.wardove.work.UpdateCheckWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var clothingRepository: ClothingRepository

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

        // Read settings synchronously so the first frame already has the right theme and
        // knows whether to show the welcome tour (avoids a flash of the wrong screen).
        // Users upgrading from a version without onboarding already have a wardrobe —
        // don't make them sit through the tour.
        val initialSettings = runBlocking {
            val s = settingsRepository.settings.first()
            if (!s.onboardingCompleted && clothingRepository.countAll() > 0) {
                settingsRepository.setOnboardingCompleted(true)
                s.copy(onboardingCompleted = true)
            } else {
                s
            }
        }

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

            // Keep status/navigation bar icon contrast in sync with the *app's* theme
            // choice, not just the system setting (a forced Dark theme on a light
            // system would otherwise get dark icons on a dark background).
            val dark = isDarkTheme(settings.themeMode)
            LaunchedEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT
                    ) { dark },
                    navigationBarStyle = SystemBarStyle.auto(
                        LIGHT_NAV_SCRIM, DARK_NAV_SCRIM
                    ) { dark }
                )
            }

            WardoveTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = settings.onboardingCompleted,
                        transitionSpec = {
                            (fadeIn() + scaleIn(initialScale = 0.96f)) togetherWith fadeOut()
                        },
                        label = "onboardingGate",
                        // Hide the content tree from accessibility services while locked
                        // so TalkBack can't read the wardrobe through the lock overlay.
                        modifier = if (isLocked) Modifier.clearAndSetSemantics { } else Modifier
                    ) { onboarded ->
                        if (onboarded) {
                            WardoveNavHost(
                                deepLinkRoute = notificationNavRoute,
                                onDeepLinkConsumed = { notificationNavRoute = null }
                            )
                        } else {
                            OnboardingScreen(
                                updateNotificationsEnabled = settings.updateNotificationsEnabled
                            )
                        }
                    }
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

    /**
     * Unlock prompt. Accepts weak biometrics *or* the device PIN/pattern/password so a
     * user who removed their fingerprints (or whose sensor is broken) is never locked
     * out of their own wardrobe. DEVICE_CREDENTIAL forbids a negative button.
     */
    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.lock_prompt_title))
            .setSubtitle(getString(R.string.lock_prompt_subtitle))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private companion object {
        // Same scrims androidx.activity uses for its default three-button nav bar.
        val LIGHT_NAV_SCRIM = AndroidColor.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_NAV_SCRIM = AndroidColor.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
