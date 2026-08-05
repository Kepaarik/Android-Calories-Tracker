package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository
import javax.inject.Inject

class UpdateDiaryEntryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(entry: DiaryEntry): Result<DiaryEntry> {
        return diaryRepository.updateEntry(entry)
    }
}
