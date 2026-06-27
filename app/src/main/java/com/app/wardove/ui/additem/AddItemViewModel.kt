package com.app.wardove.ui.additem

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.image.ImageStorage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.repository.ClothingRepository
import com.app.wardove.ui.navigation.WardoveDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddItemUiState(
    val name: String = "",
    val category: String = "",
    val color: String = "",
    val notes: String = "",
    val price: String = "",
    val imagePath: String? = null,
    val pendingCameraPath: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isImageLoading: Boolean = false,
    val loaded: Boolean = false
) {
    val canSave: Boolean
        get() = !isSaving &&
            name.isNotBlank() &&
            category.isNotBlank() &&
            color.isNotBlank()
}

@HiltViewModel
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ClothingRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val editingId: Long =
        savedStateHandle.get<Long>(WardoveDestinations.ADD_ITEM_ARG)
            ?: WardoveDestinations.ADD_ITEM_NEW_ID

    private val isEditing: Boolean = editingId > 0L
    private var committed: Boolean = false

    private val _state = MutableStateFlow(AddItemUiState(isEditing = isEditing))
    val state: StateFlow<AddItemUiState> = _state.asStateFlow()

    init {
        if (isEditing) {
            viewModelScope.launch {
                repository.getById(editingId)?.let { item ->
                    _state.update {
                        it.copy(
                            name = item.name,
                            category = item.category,
                            color = item.color,
                            notes = item.notes.orEmpty(),
                            price = item.price?.let { p -> formatPrice(p) }.orEmpty(),
                            imagePath = item.imagePath,
                            isEditing = true,
                            loaded = true
                        )
                    }
                } ?: _state.update { it.copy(loaded = true) }
            }
        } else {
            _state.update { it.copy(loaded = true) }
        }
    }

    fun prepareCameraCapture(): Uri {
        val file = imageStorage.createTempImageFile()
        _state.update { it.copy(pendingCameraPath = file.absolutePath) }
        return imageStorage.getUriForFile(file)
    }

    fun onCameraResult(success: Boolean) {
        val pending = _state.value.pendingCameraPath
        if (success && pending != null) {
            replaceImagePath(pending)
            _state.update { it.copy(pendingCameraPath = null) }
        } else if (pending != null) {
            imageStorage.delete(pending)
            _state.update { it.copy(pendingCameraPath = null) }
        }
    }

    fun clearImage() {
        val current = _state.value.imagePath ?: return
        if (isEditing && editingOriginalImagePath == null) editingOriginalImagePath = current
        if (current != editingOriginalImagePath) imageStorage.delete(current)
        _state.update { it.copy(imagePath = null) }
    }

    fun onGalleryUri(uri: Uri) {
        viewModelScope.launch {
            val newPath = imageStorage.saveImageFromUri(uri)
            replaceImagePath(newPath)
        }
    }

    private fun replaceImagePath(newPath: String) {
        val current = _state.value.imagePath
        val originalPath = if (isEditing) editingOriginalImagePath else null
        if (!current.isNullOrBlank() && current != newPath && current != originalPath) {
            imageStorage.delete(current)
        }
        if (editingOriginalImagePath == null && isEditing) {
            editingOriginalImagePath = current
        }
        _state.update { it.copy(imagePath = newPath) }
    }

    private var editingOriginalImagePath: String? = null

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

    private fun formatPrice(p: Double): String =
        if (p % 1.0 == 0.0) p.toLong().toString() else p.toString()

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (!s.canSave) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                if (isEditing) {
                    val existing = repository.getById(editingId) ?: run {
                        _state.update { it.copy(isSaving = false) }
                        return@launch
                    }
                    val newPath = s.imagePath
                    if (existing.imagePath != newPath) {
                        imageStorage.delete(existing.imagePath)
                    }
                    repository.update(
                        existing.copy(
                            name = s.name.trim(),
                            category = s.category,
                            color = s.color,
                            notes = s.notes.ifBlank { null },
                            price = parsePrice(s.price),
                            imagePath = newPath.orEmpty()
                        )
                    )
                } else {
                    repository.insert(
                        ClothingItem(
                            name = s.name.trim(),
                            category = s.category,
                            color = s.color,
                            imagePath = s.imagePath.orEmpty(),
                            notes = s.notes.ifBlank { null },
                            price = parsePrice(s.price)
                        )
                    )
                }
                committed = true
                onDone()
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        val s = _state.value
        s.pendingCameraPath?.let { imageStorage.delete(it) }
        if (!committed && !isEditing) {
            imageStorage.delete(s.imagePath)
        }
    }
}
