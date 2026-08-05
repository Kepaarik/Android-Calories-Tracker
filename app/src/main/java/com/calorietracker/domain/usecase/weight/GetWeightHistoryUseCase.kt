package com.calorietracker.domain.usecase.weight

import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.domain.repository.WeightRepository
import java.time.LocalDate
import javax.inject.Inject

class GetWeightHistoryUseCase @Inject constructor(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(
        fromDate: LocalDate? = null,
        toDate: LocalDate? = null
    ): Result<List<WeightEntry>> {
        return weightRepository.getWeightHistory(fromDate, toDate)
    }
}
