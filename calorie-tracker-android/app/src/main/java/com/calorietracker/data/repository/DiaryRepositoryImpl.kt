package com.calorietracker.data.repository

import com.calorietracker.data.local.dao.DiaryEntryDao
import com.calorietracker.data.local.dao.ProductDao
import com.calorietracker.data.mapper.DiaryEntryMapper.toDomain
import com.calorietracker.data.mapper.DiaryEntryMapper.toEntity
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DiaryRepositoryImpl(
    private val diaryEntryDao: DiaryEntryDao,
    private val productDao: ProductDao
) : DiaryRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun getEntriesByDate(date: LocalDate): List<DiaryEntry> {
        val dateStr = date.format(dateFormatter)
        // TODO: Получить userId из контекста (DataStore или сессии)
        val userId = getCurrentUserId()
        
        if (userId == null) {
            return emptyList()
        }
        
        val entities = diaryEntryDao.getEntriesByDate(dateStr, userId)
        return entities.mapNotNull { entity ->
            val product = productDao.getProductById(entity.productId)?.let { 
                com.calorietracker.data.mapper.ProductMapper.toDomain(it) 
            }
            if (product != null) {
                entity.toDomain(product)
            } else {
                null
            }
        }
    }

    override suspend fun getEntriesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DiaryEntry> {
        val userId = getCurrentUserId() ?: return emptyList()
        
        val allEntries = diaryEntryDao.getAllEntriesForUser(userId)
        val startStr = startDate.format(dateFormatter)
        val endStr = endDate.format(dateFormatter)
        
        return allEntries
            .filter { it.date in startStr..endStr }
            .mapNotNull { entity ->
                val product = productDao.getProductById(entity.productId)?.let {
                    com.calorietracker.data.mapper.ProductMapper.toDomain(it)
                }
                if (product != null) {
                    entity.toDomain(product)
                } else {
                    null
                }
            }
            .sortedBy { it.date }
    }

    override suspend fun addEntry(entry: DiaryEntry): Result<DiaryEntry> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val entity = entry.toEntity().copy(userId = userId)
            val insertedId = diaryEntryDao.insertEntry(entity)
            
            val insertedEntry = entry.copy(id = insertedId.toInt())
            Result.success(insertedEntry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEntry(entry: DiaryEntry): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val entity = entry.toEntity().copy(userId = userId)
            diaryEntryDao.updateEntry(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEntry(id: Int): Result<Unit> {
        return try {
            diaryEntryDao.deleteEntryById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Получение текущего ID пользователя.
     * В реальной реализации должно получать из DataStore или сессии.
     */
    private fun getCurrentUserId(): Int? {
        // TODO: Реализовать получение userId из DataStore
        // Это временная заглушка для разработки
        return 1
    }
}
