package com.app.wardove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.app.wardove.data.settings.SettingsRepository
import com.app.wardove.ui.lock.LockScreen
import com.app.wardove.ui.lock.LockViewModel
import com.app.wardove.ui.navigation.WardoveNavHost
import com.app.wardove.ui.theme.WardoveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val lockViewModel: LockViewModel by viewModels()

    private var stopTimestamp = 0L
    private val LOCK_GRACE_MS = 1_000L

    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                    WardoveNavHost()
                    if (isLocked) {
                        LockScreen(onBiometricRequest = lockViewModel::requestBiometric)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (System.currentTimeMillis() - stopTimestamp > LOCK_GRACE_MS) {
            lockViewModel.lock()
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
