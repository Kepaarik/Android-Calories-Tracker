package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DailySummary
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class CalculateDailySummaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): Result<DailySummary> {
        return diaryRepository.getDailySummary(date)
    }
}
