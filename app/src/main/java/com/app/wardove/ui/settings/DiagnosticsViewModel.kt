package com.app.wardove.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.R
import com.app.wardove.data.log.DiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val repository: DiagnosticsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _logText = MutableStateFlow("")
    val logText: StateFlow<String> = _logText.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _shareUri = MutableStateFlow<Uri?>(null)
    val shareUri: StateFlow<Uri?> = _shareUri.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _logText.value = repository.readLog()
        }
    }

    fun share() {
        viewModelScope.launch {
            runCatching { repository.writeShareFile() }
                .onSuccess { _shareUri.value = it }
                .onFailure { _message.value = context.getString(R.string.diagnostics_message_share_failed, it.message) }
        }
    }

    fun onShareHandled() {
        _shareUri.value = null
    }

    fun clear() {
        viewModelScope.launch {
            repository.clear()
            _logText.value = ""
            _message.value = context.getString(R.string.diagnostics_message_cleared)
        }
    }

    fun onMessageShown() {
        _message.value = null
    }
}
