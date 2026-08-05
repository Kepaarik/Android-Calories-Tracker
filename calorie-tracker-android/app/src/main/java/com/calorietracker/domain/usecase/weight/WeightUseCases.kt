package com.calorietracker.domain.usecase.weight

import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.domain.repository.WeightRepository
import java.time.LocalDate

class GetWeightHistoryUseCase(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(userId: Int): List<WeightEntry> {
        return weightRepository.getWeightHistory(userId)
    }
}

class GetWeightHistoryByDateRangeUseCase(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(
        userId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<WeightEntry> {
        return weightRepository.getWeightHistoryByDateRange(userId, startDate, endDate)
    }
}

class AddWeightEntryUseCase(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(entry: WeightEntry): Result<WeightEntry> {
        return weightRepository.addWeightEntry(entry)
    }
}

class DeleteWeightEntryUseCase(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return weightRepository.deleteWeightEntry(id)
    }
}
