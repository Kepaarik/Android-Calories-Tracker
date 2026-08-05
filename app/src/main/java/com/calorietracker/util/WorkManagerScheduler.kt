package com.calorietracker.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.calorietracker.worker.MealReminderWorker
import com.calorietracker.worker.SyncDataWorker
import com.calorietracker.worker.WaterReminderWorker
import com.calorietracker.worker.WeeklyReportWorker
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    fun scheduleAllWorkers(context: Context) {
        scheduleSyncWorker(context)
        scheduleMealReminders(context)
        scheduleWaterReminders(context)
        scheduleWeeklyReport(context)
    }

    private fun scheduleSyncWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "sync_data_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun scheduleMealReminders(context: Context) {
        val breakfastWork = PeriodicWorkRequestBuilder<MealReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(8, TimeUnit.HOURS) // 8 утра
            .build()

        val lunchWork = PeriodicWorkRequestBuilder<MealReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(13, TimeUnit.HOURS) // 13 дня
            .build()

        val dinnerWork = PeriodicWorkRequestBuilder<MealReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(19, TimeUnit.HOURS) // 19 вечера
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "breakfast_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            breakfastWork
        )

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "lunch_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            lunchWork
        )

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "dinner_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            dinnerWork
        )
    }

    private fun scheduleWaterReminders(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            repeatInterval = 2,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun scheduleWeeklyReport(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WeeklyReportWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weekly_report",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancelAllWorkers(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    fun cancelWorker(context: Context, workName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }
}
