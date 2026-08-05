package com.calorietracker.data.repository

import com.calorietracker.data.mapper.WeightEntryMapper.toDomain
import com.calorietracker.data.remote.NetworkResult
import com.calorietracker.data.remote.api.WeightApi
import com.calorietracker.data.remote.safeApiCall
import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.domain.repository.WeightRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepositoryImpl @Inject constructor(
    private val weightApi: WeightApi
) : WeightRepository {

    override suspend fun getWeightHistory(
        fromDate: LocalDate?,
        toDate: LocalDate?
    ): Result<List<WeightEntry>> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            weightApi.getWeightHistory(
                "Bearer $token",
                fromDate?.toString(),
                toDate?.toString()
            )
        }) {
            is NetworkResult.Success -> {
                val entries = result.data.map { it.toDomain() }
                Result.success(entries)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to get weight history: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun addWeightEntry(
        weightKg: Double,
        date: LocalDate
    ): Result<WeightEntry> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        val request = com.calorietracker.data.remote.dto.CreateWeightEntryRequestDto(
            weightKg = weightKg,
            date = date.toString()
        )
        
        return when (val result = safeApiCall {
            weightApi.addWeightEntry("Bearer $token", request)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomain())
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to add weight entry: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun deleteWeightEntry(id: Int): Result<Unit> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            weightApi.deleteWeightEntry("Bearer $token", id)
        }) {
            is NetworkResult.Success -> {
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to delete weight entry: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    private fun getToken(): String? {
        // This should be retrieved from DataStore or SharedPreferences
        return null
    }
}
