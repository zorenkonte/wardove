package com.app.wardove.data.local.dao

import androidx.room.Embedded
import com.app.wardove.data.local.entity.ClothingItem

data class WearLogWithItem(
    val wornDate: Long,
    @Embedded val item: ClothingItem
)
