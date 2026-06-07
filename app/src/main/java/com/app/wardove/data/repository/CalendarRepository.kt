package com.app.wardove.data.repository

import com.app.wardove.data.local.dao.WearLogDao
import com.app.wardove.data.local.dao.WearLogWithItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(
    private val wearLogDao: WearLogDao
) {
    fun observeAllWearLogsWithItems(): Flow<List<WearLogWithItem>> =
        wearLogDao.observeAllWithItems()
}
