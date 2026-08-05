package com.calorietracker.data.local.dao

import androidx.room.*
import com.calorietracker.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {
    
    @Query("SELECT * FROM water_intake WHERE id = :entryId")
    suspend fun getEntryById(entryId: Int): WaterIntakeEntity?
    
    @Query("SELECT * FROM water_intake WHERE id = :entryId")
    fun getEntryByIdFlow(entryId: Int): Flow<WaterIntakeEntity?>
    
    @Query("SELECT * FROM water_intake WHERE date = :date AND userId = :userId ORDER BY createdAt ASC")
    suspend fun getIntakesByDate(date: String, userId: Int): List<WaterIntakeEntity>
    
    @Query("SELECT * FROM water_intake WHERE date = :date AND userId = :userId ORDER BY createdAt ASC")
    fun getIntakesByDateFlow(date: String, userId: Int): Flow<List<WaterIntakeEntity>>
    
    @Query("SELECT SUM(volumeMl) FROM water_intake WHERE date = :date AND userId = :userId")
    suspend fun getTotalIntakeByDate(date: String, userId: Int): Int?
    
    @Query("SELECT * FROM water_intake WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    suspend fun getAllIntakesForUser(userId: Int): List<WaterIntakeEntity>
    
    @Query("SELECT * FROM water_intake WHERE userId = :userId ORDER BY date DESC, createdAt ASC")
    fun getAllIntakesForUserFlow(userId: Int): Flow<List<WaterIntakeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: WaterIntakeEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakes(intakes: List<WaterIntakeEntity>)
    
    @Update
    suspend fun updateIntake(intake: WaterIntakeEntity)
    
    @Delete
    suspend fun deleteIntake(intake: WaterIntakeEntity)
    
    @Query("DELETE FROM water_intake WHERE id = :entryId")
    suspend fun deleteIntakeById(entryId: Int)
    
    @Query("DELETE FROM water_intake WHERE date = :date AND userId = :userId")
    suspend fun deleteIntakesByDate(date: String, userId: Int)
    
    @Query("DELETE FROM water_intake")
    suspend fun deleteAllIntakes()
}
