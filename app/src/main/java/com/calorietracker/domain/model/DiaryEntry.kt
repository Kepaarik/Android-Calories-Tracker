package com.calorietracker.domain.model

import java.time.LocalDate

data class DiaryEntry(
    val id: Int,
    val productId: Int,
    val product: Product,
    val mealType: MealType,
    val weightGrams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val date: LocalDate,
    val createdAt: Long
)

enum class MealType {
    BREAKFAST,  // Завтрак
    LUNCH,      // Обед
    DINNER,     // Ужин
    SNACK       // Перекус
}

data class DailySummary(
    val date: LocalDate,
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val goalCalories: Int = 2000,
    val goalProteins: Double = 150.0,
    val goalFats: Double = 67.0,
    val goalCarbs: Double = 250.0
) {
    val caloriesRemaining: Double
        get() = goalCalories - totalCalories

    val proteinRemaining: Double
        get() = goalProteins - totalProteins

    val fatsRemaining: Double
        get() = goalFats - totalFats

    val carbsRemaining: Double
        get() = goalCarbs - totalCarbs

    val caloriesProgress: Float
        get() = (totalCalories / goalCalories).coerceIn(0.0, 1.0).toFloat()

    val proteinProgress: Float
        get() = (totalProteins / goalProteins).coerceIn(0.0, 1.0).toFloat()

    val fatsProgress: Float
        get() = (totalFats / goalFats).coerceIn(0.0, 1.0).toFloat()

    val carbsProgress: Float
        get() = (totalCarbs / goalCarbs).coerceIn(0.0, 1.0).toFloat()
}
