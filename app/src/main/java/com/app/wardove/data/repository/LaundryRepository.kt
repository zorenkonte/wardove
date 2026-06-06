package com.app.wardove.data.repository

import com.app.wardove.data.local.dao.ClothingDao
import com.app.wardove.data.local.dao.LaundryDao
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.local.entity.LaundryCycleItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaundryRepository @Inject constructor(
    private val laundryDao: LaundryDao,
    private val clothingDao: ClothingDao
) {
    fun observePile(): Flow<List<ClothingItem>> =
        clothingDao.observeByStatus(ClothingStatus.WORN)

    fun observeWashing(): Flow<List<ClothingItem>> =
        clothingDao.observeByStatus(ClothingStatus.IN_LAUNDRY)

    fun observeAllCycles(): Flow<List<LaundryCycle>> = laundryDao.observeAllCycles()

    fun observeActiveCycle(): Flow<LaundryCycle?> = laundryDao.observeActiveCycle()

    fun observeActiveCycles(): Flow<List<LaundryCycle>> = laundryDao.observeActiveCycles()

    fun observeCompletedCycles(): Flow<List<LaundryCycle>> = laundryDao.observeCompletedCycles()

    fun observeItemsInCycle(cycleId: Long): Flow<List<ClothingItem>> =
        laundryDao.observeItemsInCycle(cycleId)

    suspend fun startCycle(itemIds: List<Long>, now: Long = System.currentTimeMillis()): Long {
        val cycleId = laundryDao.insertCycle(
            LaundryCycle(startedAt = now, itemCount = itemIds.size)
        )
        laundryDao.insertCycleItems(itemIds.map { LaundryCycleItem(cycleId, it) })
        clothingDao.updateStatusForIds(itemIds, ClothingStatus.IN_LAUNDRY)
        return cycleId
    }

    suspend fun completeCycle(cycleId: Long, now: Long = System.currentTimeMillis()) {
        val items = laundryDao.getItemsInCycle(cycleId)
        if (items.isNotEmpty()) {
            clothingDao.markCleanAndReset(items.map { it.id }, ClothingStatus.CLEAN)
        }
        laundryDao.completeCycle(cycleId, now)
    }
}
