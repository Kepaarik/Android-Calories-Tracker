package com.calorietracker.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.usecase.diary.AddDiaryEntryUseCase
import com.calorietracker.domain.usecase.diary.GetDiaryEntriesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DashboardViewModel
    private lateinit var getDiaryEntriesUseCase: GetDiaryEntriesUseCase
    private lateinit var addDiaryEntryUseCase: AddDiaryEntryUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDiaryEntriesUseCase = mockk()
        addDiaryEntryUseCase = mockk()
        viewModel = DashboardViewModel(getDiaryEntriesUseCase, addDiaryEntryUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty entries`() = runTest {
        // Given
        coEvery { getDiaryEntriesUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel.loadEntriesForToday()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.entries.isEmpty())
    }

    @Test
    fun `loadEntriesForToday should update uiState with entries`() = runTest {
        // Given
        val product = Product(
            id = 1,
            name = "Apple",
            caloriesPer100g = 52.0,
            proteinsPer100g = 0.3f,
            fatsPer100g = 0.2f,
            carbsPer100g = 14.0f
        )
        
        coEvery { getDiaryEntriesUseCase(any()) } returns flowOf(listOf(
            com.calorietracker.domain.model.DiaryEntry(
                id = 1,
                product = product,
                mealType = MealType.BREAKFAST,
                weightGrams = 150,
                date = java.time.LocalDate.now()
            )
        ))

        // When
        viewModel.loadEntriesForToday()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals("Apple", state.entries.first().product.name)
    }
}
