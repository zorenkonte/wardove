package com.app.wardove.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.backup.BackupRepository
import com.app.wardove.data.backup.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: BackupRepository
) : ViewModel() {

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _result = MutableStateFlow<BackupOutcome?>(null)
    val result: StateFlow<BackupOutcome?> = _result.asStateFlow()

    fun export(uri: Uri) {
        if (_isBusy.value) return
        _isBusy.value = true
        viewModelScope.launch {
            _result.value = BackupOutcome(BackupOperation.EXPORT, repository.exportTo(uri))
            _isBusy.value = false
        }
    }

    fun import(uri: Uri) {
        if (_isBusy.value) return
        _isBusy.value = true
        viewModelScope.launch {
            _result.value = BackupOutcome(BackupOperation.IMPORT, repository.importFrom(uri))
            _isBusy.value = false
        }
    }

    fun onResultShown() {
        _result.value = null
    }
}

enum class BackupOperation { EXPORT, IMPORT }

data class BackupOutcome(val operation: BackupOperation, val result: ImportResult)
