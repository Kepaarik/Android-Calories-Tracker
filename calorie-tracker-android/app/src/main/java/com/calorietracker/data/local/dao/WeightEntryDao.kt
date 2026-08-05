package com.calorietracker.data.local.dao

import androidx.room.*
import com.calorietracker.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    
    @Query("SELECT * FROM weight_entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: Int): WeightEntryEntity?
    
    @Query("SELECT * FROM weight_entries WHERE id = :entryId")
    fun getEntryByIdFlow(entryId: Int): Flow<WeightEntryEntity?>
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    suspend fun getEntriesByUser(userId: Int): List<WeightEntryEntity>
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    fun getEntriesByUserFlow(userId: Int): Flow<List<WeightEntryEntity>>
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId AND date = :date")
    suspend fun getEntryByDate(userId: Int, date: String): WeightEntryEntity?
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId AND date = :date")
    fun getEntryByDateFlow(userId: Int, date: String): Flow<WeightEntryEntity?>
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getLatestEntries(userId: Int, limit: Int): List<WeightEntryEntity>
    
    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getLatestEntriesFlow(userId: Int, limit: Int): Flow<List<WeightEntryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WeightEntryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<WeightEntryEntity>)
    
    @Update
    suspend fun updateEntry(entry: WeightEntryEntity)
    
    @Delete
    suspend fun deleteEntry(entry: WeightEntryEntity)
    
    @Query("DELETE FROM weight_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Int)
    
    @Query("DELETE FROM weight_entries WHERE userId = :userId")
    suspend fun deleteEntriesByUser(userId: Int)
    
    @Query("DELETE FROM weight_entries")
    suspend fun deleteAllEntries()
}
