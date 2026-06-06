package com.app.wardove.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.repository.LaundryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val laundryRepository: LaundryRepository
) : ViewModel() {

    val cycles: StateFlow<List<LaundryCycle>?> = laundryRepository.observeCompletedCycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _expandedCycleId = MutableStateFlow<Long?>(null)
    val expandedCycleId: StateFlow<Long?> = _expandedCycleId.asStateFlow()

    val expandedItems: StateFlow<List<ClothingItem>> = _expandedCycleId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else laundryRepository.observeItemsInCycle(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun toggleExpand(cycleId: Long) {
        _expandedCycleId.update { if (it == cycleId) null else cycleId }
    }
}
