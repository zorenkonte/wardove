package com.app.wardove.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wear_logs",
    foreignKeys = [
        ForeignKey(
            entity = ClothingItem::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clothingItemId")]
)
data class WearLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clothingItemId: Long,
    val wornDate: Long
)
