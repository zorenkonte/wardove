package com.app.wardove.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.log.DiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val repository: DiagnosticsRepository
) : ViewModel() {

    private val _logText = MutableStateFlow("")
    val logText: StateFlow<String> = _logText.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _logText.value = repository.readLog()
        }
    }

    fun export(uri: Uri) {
        viewModelScope.launch {
            runCatching { repository.exportTo(uri) }
                .onSuccess { _message.value = "Log exported successfully" }
                .onFailure { _message.value = "Export failed: ${it.message}" }
        }
    }

    fun clear() {
        viewModelScope.launch {
            repository.clear()
            _logText.value = ""
            _message.value = "Logs cleared"
        }
    }

    fun onMessageShown() {
        _message.value = null
    }
}
