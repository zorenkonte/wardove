package com.app.wardove.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.wardove.data.local.dao.ClothingDao
import com.app.wardove.data.local.dao.LaundryDao
import com.app.wardove.data.local.dao.WearLogDao
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.local.entity.LaundryCycleItem
import com.app.wardove.data.local.entity.WearLog

@Database(
    entities = [
        ClothingItem::class,
        WearLog::class,
        LaundryCycle::class,
        LaundryCycleItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WardoveDatabase : RoomDatabase() {
    abstract fun clothingDao(): ClothingDao
    abstract fun wearLogDao(): WearLogDao
    abstract fun laundryDao(): LaundryDao

    companion object {
        const val DATABASE_NAME = "wardove.db"
    }
}
