package com.calorietracker.domain.usecase.weight

import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.domain.repository.WeightRepository
import java.time.LocalDate
import javax.inject.Inject

class AddWeightEntryUseCase @Inject constructor(
    private val weightRepository: WeightRepository
) {
    suspend operator fun invoke(
        weightKg: Double,
        date: LocalDate = LocalDate.now()
    ): Result<WeightEntry> {
        return weightRepository.addWeightEntry(weightKg, date)
    }
}
