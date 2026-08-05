package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class GetDiaryEntriesUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): Result<List<DiaryEntry>> {
        return diaryRepository.getEntriesByDate(date)
    }
}
