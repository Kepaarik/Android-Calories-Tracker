package com.calorietracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calorietracker.data.repository.DiaryRepository
import com.calorietracker.data.repository.UserProfileRepository
import com.calorietracker.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val diaryRepository: DiaryRepository,
    private val userProfileRepository: UserProfileRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Синхронизация дневника питания
            val localEntries = diaryRepository.getAllEntries().first()
            val syncResult = diaryRepository.syncWithServer(localEntries)
            
            // Синхронизация профиля пользователя
            val userProfile = userProfileRepository.getUserProfile().first()
            userProfile?.let { profile ->
                userProfileRepository.updateUserProfile(profile)
            }
            
            // Показываем уведомление о результате
            NotificationHelper.showSyncNotification(
                applicationContext,
                isSuccessful = syncResult.isSuccess,
                message = if (syncResult.isSuccess) "Все данные синхронизированы" else "Проверьте подключение к интернету"
            )
            
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
