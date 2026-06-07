package com.app.wardove.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.dao.WearLogWithItem
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    repository: CalendarRepository
) : ViewModel() {

    private val zone: ZoneId = ZoneId.systemDefault()

    val allWearLogsWithItems: StateFlow<List<WearLogWithItem>> =
        repository.observeAllWearLogsWithItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val datesWithWear: StateFlow<Set<LocalDate>> = allWearLogsWithItems
        .map { logs -> logs.mapTo(mutableSetOf()) { it.wornDate.toLocalDate() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    val itemsForSelectedDate: StateFlow<List<ClothingItem>> = combine(
        allWearLogsWithItems,
        _selectedDate
    ) { logs, date ->
        logs.filter { it.wornDate.toLocalDate() == date }
            .map { it.item }
            .distinctBy { it.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(zone).toLocalDate()
}
