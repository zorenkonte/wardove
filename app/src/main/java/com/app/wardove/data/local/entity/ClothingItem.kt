package com.app.wardove.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

object ClothingStatus {
    const val CLEAN = "CLEAN"
    const val WORN = "WORN"
    const val IN_LAUNDRY = "IN_LAUNDRY"
}

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val category: String,
    val color: String,
    val imagePath: String,
    val status: String = ClothingStatus.CLEAN,
    val lastWornDate: Long? = null,
    val totalWearCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val price: Double? = null
)
