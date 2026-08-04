package com.calorietracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.calorietracker.data.model.*

@Database(
    entities = [
        FoodEntry::class,
        DailyGoal::class,
        ActivityRecord::class,
        TelegramUser::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CalorieTrackerDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun activityRecordDao(): ActivityRecordDao
    abstract fun telegramUserDao(): TelegramUserDao

    companion object {
        @Volatile
        private var INSTANCE: CalorieTrackerDatabase? = null

        fun getDatabase(context: Context): CalorieTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalorieTrackerDatabase::class.java,
                    "calorie_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
