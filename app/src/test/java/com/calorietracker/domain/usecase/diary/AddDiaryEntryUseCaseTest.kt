package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.DiaryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AddDiaryEntryUseCaseTest {

    private lateinit var diaryRepository: DiaryRepository
    private lateinit var addDiaryEntryUseCase: AddDiaryEntryUseCase

    @Before
    fun setup() {
        diaryRepository = mockk()
        addDiaryEntryUseCase = AddDiaryEntryUseCase(diaryRepository)
    }

    @Test
    fun `invoke should call repository addEntry with correct parameters`() = runTest {
        // Given
        val product = Product(
            id = 1,
            name = "Apple",
            caloriesPer100g = 52.0,
            proteinsPer100g = 0.3f,
            fatsPer100g = 0.2f,
            carbsPer100g = 14.0f
        )
        val entry = DiaryEntry(
            id = 1,
            product = product,
            mealType = MealType.BREAKFAST,
            weightGrams = 150,
            date = LocalDate.now()
        )
        
        coEvery { diaryRepository.addEntry(any()) } returns Result.success(entry)

        // When
        val result = addDiaryEntryUseCase(product, MealType.BREAKFAST, 150, LocalDate.now())

        // Then
        assertTrue(result.isSuccess)
        assertEquals(entry, result.getOrNull())
        coVerify { diaryRepository.addEntry(any()) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val product = Product(
            id = 1,
            name = "Apple",
            caloriesPer100g = 52.0,
            proteinsPer100g = 0.3f,
            fatsPer100g = 0.2f,
            carbsPer100g = 14.0f
        )
        val errorMessage = "Database error"
        
        coEvery { diaryRepository.addEntry(any()) } returns Result.failure(Exception(errorMessage))

        // When
        val result = addDiaryEntryUseCase(product, MealType.BREAKFAST, 150, LocalDate.now())

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
