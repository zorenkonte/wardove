package com.app.wardove.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.wardove.data.local.entity.WearLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WearLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WearLog): Long

    @Query("SELECT * FROM wear_logs WHERE clothingItemId = :itemId ORDER BY wornDate DESC")
    fun observeForItem(itemId: Long): Flow<List<WearLog>>

    @Query("SELECT COUNT(*) FROM wear_logs WHERE clothingItemId = :itemId")
    suspend fun countForItem(itemId: Long): Int

    @Query("DELETE FROM wear_logs WHERE clothingItemId = :itemId")
    suspend fun deleteForItem(itemId: Long)
}
