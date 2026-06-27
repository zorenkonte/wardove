package com.app.wardove.ui.additem

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.image.ImageStorage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareItemViewModel @Inject constructor(
    private val repository: ClothingRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemUiState())
    val state: StateFlow<AddItemUiState> = _state.asStateFlow()

    private var committed = false
    private var imageLoaded = false

    fun loadSharedImage(uri: Uri) {
        if (imageLoaded) return
        imageLoaded = true
        viewModelScope.launch {
            val path = imageStorage.saveImageFromUri(uri)
            _state.update { it.copy(imagePath = path) }
        }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setCategory(v: String) = _state.update { it.copy(category = v) }
    fun setColor(v: String) = _state.update { it.copy(color = v) }
    fun setNotes(v: String) = _state.update { it.copy(notes = v) }
    fun setPrice(v: String) {
        val filtered = v.filter { it.isDigit() || it == '.' }
            .let { s -> if (s.count { c -> c == '.' } > 1) s.substringBeforeLast('.') else s }
        _state.update { it.copy(price = filtered) }
    }

    private fun parsePrice(raw: String): Double? =
        raw.trim().takeIf { it.isNotBlank() }?.toDoubleOrNull()

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (!s.canSave) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                repository.insert(
                    ClothingItem(
                        name = s.name.trim(),
                        category = s.category,
                        color = s.color,
                        imagePath = s.imagePath!!,
                        notes = s.notes.ifBlank { null },
                        price = parsePrice(s.price)
                    )
                )
                committed = true
                onDone()
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!committed) {
            imageStorage.delete(_state.value.imagePath)
        }
    }
}
