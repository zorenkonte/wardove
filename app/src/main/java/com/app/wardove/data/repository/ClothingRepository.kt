package com.app.wardove.data.repository

import android.content.Context
import com.app.wardove.data.local.dao.ClothingDao
import com.app.wardove.data.local.dao.WearLogDao
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.local.entity.WearLog
import com.app.wardove.widget.StatsWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClothingRepository @Inject constructor(
    private val clothingDao: ClothingDao,
    private val wearLogDao: WearLogDao,
    @ApplicationContext private val context: Context,
) {
    // Fire-and-forget scope for widget refresh; tied to singleton lifetime (app lifecycle).
    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeAll(): Flow<List<ClothingItem>> = clothingDao.observeAll()

    fun observeByStatus(status: String): Flow<List<ClothingItem>> =
        clothingDao.observeByStatus(status)

    fun observeById(id: Long): Flow<ClothingItem?> = clothingDao.observeById(id)

    suspend fun getById(id: Long): ClothingItem? = clothingDao.getById(id)

    suspend fun insert(item: ClothingItem): Long {
        val id = clothingDao.insert(item)
        notifyWidget()
        return id
    }

    suspend fun update(item: ClothingItem) {
        clothingDao.update(item)
        notifyWidget()
    }

    suspend fun delete(item: ClothingItem) {
        clothingDao.delete(item)
        notifyWidget()
    }

    suspend fun markWornToday(id: Long, now: Long = System.currentTimeMillis()) {
        clothingDao.markWorn(id, now, ClothingStatus.WORN)
        wearLogDao.insert(WearLog(clothingItemId = id, wornDate = now))
        notifyWidget()
    }

    suspend fun unwearToday(id: Long, now: Long = System.currentTimeMillis()) {
        val (start, end) = dayBounds(now)
        val deleted = wearLogDao.deleteForItemInRange(id, start, end)
        if (deleted <= 0) return
        val latest = wearLogDao.latestForItem(id)
        clothingDao.markUnworn(
            id = id,
            date = latest?.wornDate,
            decrement = deleted,
            status = if (latest != null) ClothingStatus.WORN else ClothingStatus.CLEAN
        )
        notifyWidget()
    }

    private fun dayBounds(time: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        val end = start + 24L * 60L * 60L * 1000L
        return start to end
    }

    suspend fun countByStatus(status: String): Int = clothingDao.countByStatus(status)

    fun observeWearLogs(itemId: Long): Flow<List<WearLog>> =
        wearLogDao.observeForItem(itemId)

    /** Triggers a widget re-render after any wardrobe mutation. Non-blocking. */
    private fun notifyWidget() {
        widgetScope.launch { StatsWidget().updateAll(context) }
    }
}
