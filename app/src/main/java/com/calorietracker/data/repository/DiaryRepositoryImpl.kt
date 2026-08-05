package com.calorietracker.data.repository

import com.calorietracker.data.mapper.DiaryEntryMapper.toDomain
import com.calorietracker.data.mapper.DiaryEntryMapper.toDto
import com.calorietracker.data.mapper.ProductMapper.toDomain
import com.calorietracker.data.remote.NetworkResult
import com.calorietracker.data.remote.api.DiaryApi
import com.calorietracker.data.remote.api.ProductApi
import com.calorietracker.data.remote.safeApiCall
import com.calorietracker.domain.model.DailySummary
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi,
    private val productApi: ProductApi
) : DiaryRepository {

    override suspend fun getEntriesByDate(date: LocalDate): Result<List<DiaryEntry>> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            diaryApi.getEntries("Bearer $token", date.toString())
        }) {
            is NetworkResult.Success -> {
                val dtos = result.data
                val entries = mutableListOf<DiaryEntry>()
                
                for (dto in dtos) {
                    val productResult = safeApiCall {
                        productApi.getProductById("Bearer $token", dto.productId)
                    }
                    
                    if (productResult is NetworkResult.Success) {
                        entries.add(dto.toDomain(productResult.data.toDomain()))
                    }
                }
                
                Result.success(entries)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to get entries: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun getEntriesByMealType(
        date: LocalDate,
        mealType: MealType
    ): Result<List<DiaryEntry>> {
        val entriesResult = getEntriesByDate(date)
        return if (entriesResult.isSuccess) {
            val entries = entriesResult.getOrNull()?.filter { it.mealType == mealType } ?: emptyList()
            Result.success(entries)
        } else {
            entriesResult
        }
    }

    override suspend fun addEntry(entry: DiaryEntry): Result<DiaryEntry> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        val request = com.calorietracker.data.remote.dto.CreateDiaryEntryRequestDto(
            productId = entry.productId,
            mealType = entry.mealType.toDto(),
            weightGrams = entry.weightGrams,
            date = entry.date.toString()
        )
        
        return when (val result = safeApiCall {
            diaryApi.addEntry("Bearer $token", request)
        }) {
            is NetworkResult.Success -> {
                val dto = result.data
                val productResult = safeApiCall {
                    productApi.getProductById("Bearer $token", dto.productId)
                }
                
                if (productResult is NetworkResult.Success) {
                    Result.success(dto.toDomain(productResult.data.toDomain()))
                } else {
                    Result.failure(Exception("Failed to get product details"))
                }
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to add entry: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun updateEntry(entry: DiaryEntry): Result<DiaryEntry> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        val request = com.calorietracker.data.remote.dto.UpdateDiaryEntryRequestDto(
            weightGrams = entry.weightGrams,
            mealType = entry.mealType.toDto()
        )
        
        return when (val result = safeApiCall {
            diaryApi.updateEntry("Bearer $token", entry.id, request)
        }) {
            is NetworkResult.Success -> {
                val dto = result.data
                val productResult = safeApiCall {
                    productApi.getProductById("Bearer $token", dto.productId)
                }
                
                if (productResult is NetworkResult.Success) {
                    Result.success(dto.toDomain(productResult.data.toDomain()))
                } else {
                    Result.failure(Exception("Failed to get product details"))
                }
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to update entry: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun deleteEntry(id: Int): Result<Unit> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            diaryApi.deleteEntry("Bearer $token", id)
        }) {
            is NetworkResult.Success -> {
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to delete entry: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun getDailySummary(date: LocalDate): Result<DailySummary> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            diaryApi.getDailySummary("Bearer $token", date.toString())
        }) {
            is NetworkResult.Success -> {
                val dto = result.data
                Result.success(
                    DailySummary(
                        date = LocalDate.parse(dto.date),
                        totalCalories = dto.totalCalories,
                        totalProteins = dto.totalProteins,
                        totalFats = dto.totalFats,
                        totalCarbs = dto.totalCarbs,
                        goalCalories = dto.goalCalories,
                        goalProteins = dto.goalProteins,
                        goalFats = dto.goalFats,
                        goalCarbs = dto.goalCarbs
                    )
                )
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to get summary: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    private fun getToken(): String? {
        // This should be retrieved from DataStore or SharedPreferences
        // For now, return null - will be implemented with proper token storage
        return null
    }
}
