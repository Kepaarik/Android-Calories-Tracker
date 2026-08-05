package com.calorietracker.di

import android.content.Context
import androidx.room.Room
import com.calorietracker.data.local.*
import com.calorietracker.data.local.dao.*
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
    fun provideUserDao(database: CalorieTrackerDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: CalorieTrackerDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideDiaryEntryDao(database: CalorieTrackerDatabase): DiaryEntryDao {
        return database.diaryEntryDao()
    }

    @Provides
    @Singleton
    fun provideWeightEntryDao(database: CalorieTrackerDatabase): WeightEntryDao {
        return database.weightEntryDao()
    }

    @Provides
    @Singleton
    fun provideWaterIntakeDao(database: CalorieTrackerDatabase): WaterIntakeDao {
        return database.waterIntakeDao()
    }
}

