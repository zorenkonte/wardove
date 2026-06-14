package com.app.wardove.ui.laundry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.repository.ClothingRepository
import com.app.wardove.data.repository.LaundryRepository
import com.app.wardove.data.settings.AppSettings
import com.app.wardove.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LaundryTab(val label: String) {
    PILE("Pile"),
    WASHING("Washing")
}

data class CycleWithItems(
    val cycle: LaundryCycle,
    val items: List<ClothingItem>
)

data class PileEntry(
    val item: ClothingItem,
    val readyToWash: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LaundryViewModel @Inject constructor(
    clothingRepository: ClothingRepository,
    private val laundryRepository: LaundryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LaundryTab.PILE)
    val selectedTab: StateFlow<LaundryTab> = _selectedTab.asStateFlow()

    private val _selectedPileIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedPileIds: StateFlow<Set<Long>> = _selectedPileIds.asStateFlow()

    val threshold: StateFlow<Int> = settingsRepository.settings
        .map { it.laundryThreshold }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings.DEFAULT_LAUNDRY_THRESHOLD
        )

    private val rawPile = laundryRepository.observePile()

    val pile: StateFlow<List<ClothingItem>?> = rawPile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val pileEntries: StateFlow<List<PileEntry>?> = combine(rawPile, threshold) { items, t ->
        items.map { PileEntry(it, it.totalWearCount >= t) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val activeCycles: StateFlow<List<CycleWithItems>?> =
        laundryRepository.observeActiveCycles()
            .flatMapLatest { cycles ->
                if (cycles.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val flows = cycles.map { c ->
                        laundryRepository.observeItemsInCycle(c.id)
                            .map { items -> CycleWithItems(c, items) }
                    }
                    combine(flows) { it.toList() }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    private val _messages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun setTab(tab: LaundryTab) {
        _selectedTab.value = tab
    }

    fun togglePileSelection(id: Long) {
        _selectedPileIds.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun clearPileSelection() {
        _selectedPileIds.value = emptySet()
    }

    fun sendToLaundry() {
        val ids = _selectedPileIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            laundryRepository.startCycle(ids)
            val count = ids.size
            _selectedPileIds.value = emptySet()
            _selectedTab.value = LaundryTab.WASHING
            _messages.tryEmit("Sent $count item${if (count == 1) "" else "s"} to laundry")
        }
    }

    fun completeCycle(cycleId: Long) {
        viewModelScope.launch {
            val items = activeCycles.value?.firstOrNull { it.cycle.id == cycleId }?.items
            val count = items?.size ?: 0
            laundryRepository.completeCycle(cycleId)
            _messages.tryEmit("Marked $count item${if (count == 1) "" else "s"} as clean")
        }
    }

    fun incrementThreshold() {
        viewModelScope.launch {
            settingsRepository.setLaundryThreshold(threshold.value + 1)
        }
    }

    fun decrementThreshold() {
        viewModelScope.launch {
            settingsRepository.setLaundryThreshold(threshold.value - 1)
        }
    }
}
