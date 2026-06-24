package com.app.wardove.widget

import com.app.wardove.data.repository.ClothingRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun clothingRepository(): ClothingRepository
}
