package com.app.wardove.ui.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class WardrobeFilter(val label: String, val status: String?) {
    ALL("All", null),
    CLEAN("Clean", ClothingStatus.CLEAN),
    WORN("Worn", ClothingStatus.WORN),
    IN_LAUNDRY("In Laundry", ClothingStatus.IN_LAUNDRY)
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WardrobeViewModel @Inject constructor(
    repository: ClothingRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(WardrobeFilter.ALL)
    val filter: StateFlow<WardrobeFilter> = _filter.asStateFlow()

    val items: StateFlow<List<ClothingItem>> = _filter
        .flatMapLatest { f ->
            val status = f.status
            if (status == null) repository.observeAll()
            else repository.observeByStatus(status)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setFilter(filter: WardrobeFilter) {
        _filter.value = filter
    }
}
