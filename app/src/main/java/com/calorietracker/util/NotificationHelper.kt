package com.calorietracker.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.calorietracker.MainActivity
import com.calorietracker.R

object NotificationHelper {
    
    const val CHANNEL_MEAL_REMINDER = "meal_reminder"
    const val CHANNEL_WATER_REMINDER = "water_reminder"
    const val CHANNEL_WEEKLY_REPORT = "weekly_report"
    const val CHANNEL_SYNC = "sync_channel"
    
    const val ID_MEAL_REMINDER = 1001
    const val ID_WATER_REMINDER = 1002
    const val ID_WEEKLY_REPORT = 1003
    const val ID_SYNC = 1004
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) ?: return
            
            // Meal Reminder Channel
            val mealChannel = NotificationChannel(
                CHANNEL_MEAL_REMINDER,
                "Напоминания о приёме пищи",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминания о завтраке, обеде, ужине и перекусах"
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            
            // Water Reminder Channel
            val waterChannel = NotificationChannel(
                CHANNEL_WATER_REMINDER,
                "Напоминания о питьевом режиме",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Регулярные напоминания пить воду"
                enableVibration(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            
            // Weekly Report Channel
            val weeklyChannel = NotificationChannel(
                CHANNEL_WEEKLY_REPORT,
                "Еженедельные отчёты",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Сводка результатов за неделю"
                enableVibration(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            
            // Sync Channel
            val syncChannel = NotificationChannel(
                CHANNEL_SYNC,
                "Синхронизация данных",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Фоновая синхронизация с сервером"
                enableVibration(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
            }
            
            notificationManager.createNotificationChannels(
                listOf(mealChannel, waterChannel, weeklyChannel, syncChannel)
            )
        }
    }
    
    fun showMealReminderNotification(context: Context, mealName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "dashboard")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_MEAL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Время поесть! 🍽️")
            .setContentText("Не забудьте добавить $mealName в дневник питания")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.notify(context, ID_MEAL_REMINDER, notification)
    }
    
    fun showWaterReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "dashboard")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_WATER_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💧 Пора пить воду!")
            .setContentText("Не забывайте поддерживать водный баланс")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.notify(context, ID_WATER_REMINDER, notification)
    }
    
    fun showWeeklyReportNotification(context: Context, summary: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "statistics")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_WEEKLY_REPORT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📊 Ваш недельный отчёт")
            .setContentText(summary)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.notify(context, ID_WEEKLY_REPORT, notification)
    }
    
    fun showSyncNotification(context: Context, isSuccessful: Boolean, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SYNC)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(if (isSuccessful) "Синхронизация завершена" else "Ошибка синхронизации")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(!isSuccessful)
            .build()
        
        NotificationManagerCompat.notify(context, ID_SYNC, notification)
    }
    
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
