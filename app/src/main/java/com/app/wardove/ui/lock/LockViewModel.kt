package com.app.wardove.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.settings.AppLockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val repo: AppLockRepository
) : ViewModel() {

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _biometricRequest = MutableStateFlow(false)
    val biometricRequest: StateFlow<Boolean> = _biometricRequest.asStateFlow()

    val isAppLockEnabled: StateFlow<Boolean> = repo.isAppLockEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun lock() {
        if (isAppLockEnabled.value) {
            _isLocked.value = true
            _biometricRequest.value = true
        }
    }

    fun onBiometricSuccess() {
        _isLocked.value = false
        _biometricRequest.value = false
    }

    fun requestBiometric() {
        _biometricRequest.value = true
    }

    fun onBiometricRequestConsumed() {
        _biometricRequest.value = false
    }
}
