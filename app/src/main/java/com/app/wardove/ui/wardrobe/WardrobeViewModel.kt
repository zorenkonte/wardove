package com.app.wardove.ui.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class WardrobeFilter(val label: String, val status: String?) {
    ALL("All", null),
    CLEAN("Clean", ClothingStatus.CLEAN),
    WORN("Worn", ClothingStatus.WORN),
    IN_LAUNDRY("In Laundry", ClothingStatus.IN_LAUNDRY)
}

enum class WardrobeSort(val label: String) {
    RECENTLY_WORN("Recently worn"),
    ALPHABETICAL("Alphabetical"),
    MOST_WORN("Most worn"),
    RECENTLY_ADDED("Recently added")
}

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    repository: ClothingRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(WardrobeFilter.ALL)
    val filter: StateFlow<WardrobeFilter> = _filter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sort = MutableStateFlow(WardrobeSort.RECENTLY_WORN)
    val sort: StateFlow<WardrobeSort> = _sort.asStateFlow()

    val filteredAndSortedItems: StateFlow<List<ClothingItem>> = combine(
        repository.observeAll(),
        _filter,
        _searchQuery,
        _sort
    ) { all, filter, query, sort ->
        val statusFiltered = filter.status?.let { s -> all.filter { it.status == s } } ?: all
        val searched = if (query.isBlank()) statusFiltered
        else statusFiltered.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
        }
        when (sort) {
            WardrobeSort.RECENTLY_WORN -> searched.sortedWith(
                compareByDescending<ClothingItem> { it.lastWornDate != null }
                    .thenByDescending { it.lastWornDate ?: 0L }
            )
            WardrobeSort.ALPHABETICAL -> searched.sortedBy { it.name.lowercase() }
            WardrobeSort.MOST_WORN -> searched.sortedByDescending { it.totalWearCount }
            WardrobeSort.RECENTLY_ADDED -> searched.sortedByDescending { it.createdAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setFilter(filter: WardrobeFilter) {
        _filter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSort(sort: WardrobeSort) {
        _sort.value = sort
    }
}
