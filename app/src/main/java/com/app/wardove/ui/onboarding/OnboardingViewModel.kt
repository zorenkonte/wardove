package com.app.wardove.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /** Marks the tour as seen; [com.app.wardove.MainActivity] swaps to the main UI reactively. */
    fun complete() {
        viewModelScope.launch { settingsRepository.setOnboardingCompleted(true) }
    }

    /**
     * Records the user's choice from the optional notifications step. Called after the
     * runtime permission result on Android 13+, so a denied prompt turns the toggle off.
     */
    fun setUpdateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setUpdateNotificationsEnabled(enabled) }
    }
}
