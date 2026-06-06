package com.app.wardove.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laundry_cycles")
data class LaundryCycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val startedAt: Long,
    val completedAt: Long? = null,
    val itemCount: Int = 0
)
