package com.calorietracker.di

import android.content.Context
import androidx.room.Room
import com.calorietracker.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CalorieTrackerDatabase {
        return Room.databaseBuilder(
            context,
            CalorieTrackerDatabase::class.java,
            "calorie_tracker_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(database: CalorieTrackerDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }

    @Provides
    @Singleton
    fun provideDailyGoalDao(database: CalorieTrackerDatabase): DailyGoalDao {
        return database.dailyGoalDao()
    }

    @Provides
    @Singleton
    fun provideActivityRecordDao(database: CalorieTrackerDatabase): ActivityRecordDao {
        return database.activityRecordDao()
    }

    @Provides
    @Singleton
    fun provideTelegramUserDao(database: CalorieTrackerDatabase): TelegramUserDao {
        return database.telegramUserDao()
    }
}
