package com.calorietracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calorietracker.data.repository.DiaryRepository
import com.calorietracker.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

@HiltWorker
class WeeklyReportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val diaryRepository: DiaryRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val now = LocalDate.now()
            val weekFields = WeekFields.of(Locale.getDefault())
            val currentWeek = now.get(weekFields.weekOfWeekBasedYear())
            
            // Получаем записи за последнюю неделю
            val startDate = now.minusDays(7)
            val entries = diaryRepository.getEntriesByDateRange(startDate, now).first()
            
            // Считаем статистику
            val totalCalories = entries.sumOf { it.calories.toLong() }
            val avgCalories = if (entries.isNotEmpty()) totalCalories / entries.size else 0
            val totalEntries = entries.size
            
            val summary = "За неделю: $totalEntries записей, средний расход: $avgCalories ккал/день"
            
            NotificationHelper.showWeeklyReportNotification(applicationContext, summary)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
