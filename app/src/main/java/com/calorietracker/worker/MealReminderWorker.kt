package com.calorietracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calorietracker.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalTime

@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val hour = LocalTime.now().hour
            
            val mealName = when {
                hour in 6..10 -> "завтрак"
                hour in 11..14 -> "обед"
                hour in 15..17 -> "перекус"
                hour in 18..22 -> "ужин"
                else -> "приём пищи"
            }
            
            NotificationHelper.showMealReminderNotification(applicationContext, mealName)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
