package com.calorietracker.data.local.dao

import androidx.room.*
import com.calorietracker.data.local.entity.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    
    @Query("SELECT * FROM diary_entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: Int): DiaryEntryEntity?
    
    @Query("SELECT * FROM diary_entries WHERE id = :entryId")
    fun getEntryByIdFlow(entryId: Int): Flow<DiaryEntryEntity?>
    
    @Query("SELECT * FROM diary_entries WHERE date = :date AND userId = :userId ORDER BY createdAt ASC")
    suspend fun getEntriesByDate(date: String, userId: Int): List<DiaryEntryEntity>
    
    @Query("SELECT * FROM diary_entries WHERE date = :date AND userId = :userId ORDER BY createdAt ASC")
    fun getEntriesByDateFlow(date: String, userId: Int): Flow<List<DiaryEntryEntity>>
    
    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    suspend fun getAllEntriesForUser(userId: Int): List<DiaryEntryEntity>
    
    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    fun getAllEntriesForUserFlow(userId: Int): Flow<List<DiaryEntryEntity>>
    
    @Query("SELECT * FROM diary_entries WHERE mealType = :mealType AND date = :date AND userId = :userId ORDER BY createdAt ASC")
    suspend fun getEntriesByMealTypeAndDate(mealType: String, date: String, userId: Int): List<DiaryEntryEntity>
    
    @Query("SELECT * FROM diary_entries WHERE mealType = :mealType AND date = :date AND userId = :userId ORDER BY createdAt ASC")
    fun getEntriesByMealTypeAndDateFlow(mealType: String, date: String, userId: Int): Flow<List<DiaryEntryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<DiaryEntryEntity>)
    
    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)
    
    @Delete
    suspend fun deleteEntry(entry: DiaryEntryEntity)
    
    @Query("DELETE FROM diary_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Int)
    
    @Query("DELETE FROM diary_entries WHERE date = :date AND userId = :userId")
    suspend fun deleteEntriesByDate(date: String, userId: Int)
    
    @Query("DELETE FROM diary_entries")
    suspend fun deleteAllEntries()
}
