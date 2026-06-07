package com.app.wardove.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.repository.CalendarRepository
import com.app.wardove.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CostPerWearItem(
    val item: ClothingItem,
    val costPerWear: Double
)

data class StatsUiState(
    val totalItems: Int = 0,
    val totalWears: Int = 0,
    val mostWornItem: ClothingItem? = null,
    val leastWornItem: ClothingItem? = null,
    val longestUnworn: ClothingItem? = null,
    val costPerWearItems: List<CostPerWearItem> = emptyList(),
    val categoryBreakdown: Map<String, Int> = emptyMap(),
    val averageWearsPerItem: Double = 0.0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    clothingRepository: ClothingRepository,
    calendarRepository: CalendarRepository
) : ViewModel() {

    val state: StateFlow<StatsUiState> = combine(
        clothingRepository.observeAll(),
        calendarRepository.observeAllWearLogsWithItems()
    ) { items, logs ->
        val totalItems = items.size
        val totalWears = logs.size
        val mostWorn = items.maxByOrNull { it.totalWearCount }?.takeIf { it.totalWearCount > 0 }
        val leastWorn = items.filter { it.totalWearCount >= 1 }
            .minByOrNull { it.totalWearCount }
        val longestUnworn = items
            .filter { it.status == ClothingStatus.CLEAN }
            .minByOrNull { it.lastWornDate ?: Long.MIN_VALUE }
        val costPerWear = items
            .filter { it.price != null && it.totalWearCount > 0 }
            .map { CostPerWearItem(it, it.price!! / it.totalWearCount) }
            .sortedByDescending { it.costPerWear }
        val breakdown = items.groupingBy { it.category }.eachCount()
        val avg = if (totalItems > 0) {
            val raw = totalWears.toDouble() / totalItems
            kotlin.math.round(raw * 10.0) / 10.0
        } else 0.0
        StatsUiState(
            totalItems = totalItems,
            totalWears = totalWears,
            mostWornItem = mostWorn,
            leastWornItem = leastWorn,
            longestUnworn = longestUnworn,
            costPerWearItems = costPerWear,
            categoryBreakdown = breakdown,
            averageWearsPerItem = avg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState()
    )
}
