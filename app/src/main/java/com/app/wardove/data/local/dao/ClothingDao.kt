package com.app.wardove.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.wardove.data.local.entity.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {

    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE status = :status ORDER BY createdAt DESC")
    fun observeByStatus(status: String): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    fun observeById(id: Long): Flow<ClothingItem?>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getById(id: Long): ClothingItem?

    @Query("SELECT * FROM clothing_items WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<ClothingItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClothingItem): Long

    @Update
    suspend fun update(item: ClothingItem)

    @Delete
    suspend fun delete(item: ClothingItem)

    @Query("UPDATE clothing_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE clothing_items SET status = :status WHERE id IN (:ids)")
    suspend fun updateStatusForIds(ids: List<Long>, status: String)

    @Query("UPDATE clothing_items SET lastWornDate = :date, totalWearCount = totalWearCount + 1, status = :status WHERE id = :id")
    suspend fun markWorn(id: Long, date: Long, status: String)

    @Query("UPDATE clothing_items SET totalWearCount = 0, status = :status WHERE id IN (:ids)")
    suspend fun markCleanAndReset(ids: List<Long>, status: String)
}
