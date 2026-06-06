package com.app.wardove.ui.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.image.ImageStorage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.WearLog
import com.app.wardove.data.repository.ClothingRepository
import com.app.wardove.ui.navigation.WardoveDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ClothingRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val itemId: Long =
        savedStateHandle.get<Long>(WardoveDestinations.ITEM_DETAIL_ARG) ?: 0L

    val item: StateFlow<ClothingItem?> = repository.observeById(itemId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val wearLogs: StateFlow<List<WearLog>> = repository.observeWearLogs(itemId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val wornToday: StateFlow<Boolean> = wearLogs
        .map { logs -> logs.any { isSameDay(it.wornDate, System.currentTimeMillis()) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun wearToday() {
        viewModelScope.launch { repository.markWornToday(itemId) }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            val current = repository.getById(itemId)
            if (current != null) {
                repository.delete(current)
                imageStorage.delete(current.imagePath)
            }
            onDone()
        }
    }

    private fun isSameDay(a: Long, b: Long): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
