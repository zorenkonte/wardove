package com.app.wardove.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.local.entity.LaundryCycleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: LaundryCycle): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycleItems(items: List<LaundryCycleItem>)

    @Query("UPDATE laundry_cycles SET completedAt = :completedAt WHERE id = :cycleId")
    suspend fun completeCycle(cycleId: Long, completedAt: Long)

    @Query("SELECT * FROM laundry_cycles ORDER BY startedAt DESC")
    fun observeAllCycles(): Flow<List<LaundryCycle>>

    @Query("SELECT * FROM laundry_cycles WHERE completedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    fun observeActiveCycle(): Flow<LaundryCycle?>

    @Query("SELECT * FROM laundry_cycles WHERE completedAt IS NULL ORDER BY startedAt DESC")
    fun observeActiveCycles(): Flow<List<LaundryCycle>>

    @Query("SELECT * FROM laundry_cycles WHERE completedAt IS NOT NULL ORDER BY completedAt DESC")
    fun observeCompletedCycles(): Flow<List<LaundryCycle>>

    @Query("SELECT * FROM laundry_cycles WHERE completedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveCycle(): LaundryCycle?

    @Query("SELECT * FROM laundry_cycles WHERE id = :cycleId")
    suspend fun getCycle(cycleId: Long): LaundryCycle?

    @Query(
        """
        SELECT ci.* FROM clothing_items ci
        INNER JOIN laundry_cycle_items lci ON lci.clothingItemId = ci.id
        WHERE lci.cycleId = :cycleId
        """
    )
    fun observeItemsInCycle(cycleId: Long): Flow<List<ClothingItem>>

    @Query(
        """
        SELECT ci.* FROM clothing_items ci
        INNER JOIN laundry_cycle_items lci ON lci.clothingItemId = ci.id
        WHERE lci.cycleId = :cycleId
        """
    )
    suspend fun getItemsInCycle(cycleId: Long): List<ClothingItem>
}
