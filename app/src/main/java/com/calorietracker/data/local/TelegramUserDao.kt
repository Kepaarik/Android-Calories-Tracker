package com.calorietracker.data.local

import androidx.room.*
import com.calorietracker.data.model.TelegramUser
import kotlinx.coroutines.flow.Flow

@Dao
interface TelegramUserDao {

    @Query("SELECT * FROM telegram_user LIMIT 1")
    fun getCurrentUser(): Flow<TelegramUser?>

    @Query("SELECT * FROM telegram_user LIMIT 1")
    suspend fun getCurrentUserSync(): TelegramUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: TelegramUser): Long

    @Update
    suspend fun updateUser(user: TelegramUser)

    @Delete
    suspend fun deleteUser(user: TelegramUser)

    @Query("DELETE FROM telegram_user")
    suspend fun deleteAllUsers()
}
