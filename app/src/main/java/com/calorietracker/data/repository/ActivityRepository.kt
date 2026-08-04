package com.calorietracker.data.repository

import com.calorietracker.data.local.ActivityRecordDao
import com.calorietracker.data.model.ActivityRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val activityRecordDao: ActivityRecordDao
) {

    fun getAllRecords(userId: String): Flow<List<ActivityRecord>> = 
        activityRecordDao.getAllRecords(userId)

    fun getTodayRecord(userId: String): Flow<ActivityRecord?> = 
        activityRecordDao.getTodayRecord(userId)

    suspend fun getTodaySteps(userId: String): Long = 
        activityRecordDao.getTodaySteps(userId) ?: 0

    suspend fun getTodayCaloriesBurned(userId: String): Int = 
        activityRecordDao.getTodayCaloriesBurned(userId) ?: 0

    suspend fun addRecord(record: ActivityRecord): Long = 
        activityRecordDao.insertRecord(record)

    suspend fun updateRecord(record: ActivityRecord) = 
        activityRecordDao.updateRecord(record)

    suspend fun deleteRecord(record: ActivityRecord) = 
        activityRecordDao.deleteRecord(record)

    suspend fun clearAllUserRecords(userId: String) = 
        activityRecordDao.deleteAllUserRecords(userId)
}
