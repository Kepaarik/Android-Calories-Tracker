package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository

class AddDiaryEntryUseCase(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(entry: DiaryEntry): Result<DiaryEntry> {
        return diaryRepository.addEntry(entry)
    }
}
