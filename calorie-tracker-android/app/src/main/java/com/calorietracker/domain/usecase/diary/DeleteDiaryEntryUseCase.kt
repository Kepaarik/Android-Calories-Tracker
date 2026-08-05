package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.repository.DiaryRepository

class DeleteDiaryEntryUseCase(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return diaryRepository.deleteEntry(id)
    }
}
