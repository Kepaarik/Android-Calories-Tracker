package com.calorietracker.data.repository

import com.calorietracker.data.local.dao.WeightEntryDao
import com.calorietracker.data.mapper.WeightEntryMapper.toDomain
import com.calorietracker.data.mapper.WeightEntryMapper.toEntity
import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.domain.repository.WeightRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeightRepositoryImpl(
    private val weightEntryDao: WeightEntryDao
) : WeightRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun getWeightHistory(userId: Int): List<WeightEntry> {
        val entities = weightEntryDao.getEntriesByUser(userId)
        return entities.map { it.toDomain() }.sortedBy { it.date }
    }

    override suspend fun getWeightHistoryByDateRange(
        userId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<WeightEntry> {
        val allEntries = weightEntryDao.getEntriesByUser(userId)
        val startStr = startDate.format(dateFormatter)
        val endStr = endDate.format(dateFormatter)
        
        return allEntries
            .filter { it.date in startStr..endStr }
            .map { it.toDomain() }
            .sortedBy { it.date }
    }

    override suspend fun addWeightEntry(entry: WeightEntry): Result<WeightEntry> {
        return try {
            val entity = entry.toEntity()
            val insertedId = weightEntryDao.insertEntry(entity)
            
            val insertedEntry = entry.copy(id = insertedId.toInt())
            Result.success(insertedEntry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWeightEntry(id: Int): Result<Unit> {
        return try {
            weightEntryDao.deleteEntryById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
