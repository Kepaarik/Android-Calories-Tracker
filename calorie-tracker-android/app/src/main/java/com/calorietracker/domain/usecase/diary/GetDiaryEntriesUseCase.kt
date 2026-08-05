package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate

class GetDiaryEntriesUseCase(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): List<DiaryEntry> {
        return diaryRepository.getEntriesByDate(date)
    }
}
