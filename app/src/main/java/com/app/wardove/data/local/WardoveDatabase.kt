package com.app.wardove.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = false
)
abstract class WardoveDatabase : RoomDatabase() {
    abstract fun clothingDao(): ClothingDao
    abstract fun wearLogDao(): WearLogDao
    abstract fun laundryDao(): LaundryDao

    companion object {
        const val DATABASE_NAME = "wardove.db"

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE clothing_items ADD COLUMN price REAL DEFAULT NULL")
            }
        }
    }
}
