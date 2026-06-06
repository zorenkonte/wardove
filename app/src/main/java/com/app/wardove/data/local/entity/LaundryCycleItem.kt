package com.app.wardove.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "laundry_cycle_items",
    primaryKeys = ["cycleId", "clothingItemId"],
    foreignKeys = [
        ForeignKey(
            entity = LaundryCycle::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClothingItem::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cycleId"), Index("clothingItemId")]
)
data class LaundryCycleItem(
    val cycleId: Long,
    val clothingItemId: Long
)
