package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.util.CalorieCalculator

data class CalorieNormResult(
    val bmr: Double,      // Basal Metabolic Rate
    val tdee: Double,     // Total Daily Energy Expenditure
    val recommendedCalories: Double,
    val proteinGrams: Double,
    val fatsGrams: Double,
    val carbsGrams: Double
)

class CalculateCalorieNormUseCase {
    
    operator fun invoke(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int,
        activityLevel: ActivityLevel,
        goal: WeightGoal = WeightGoal.MAINTAIN
    ): CalorieNormResult {
        val bmr = CalorieCalculator.calculateBMR(gender, weightKg, heightCm, age)
        val tdee = CalorieCalculator.calculateTDEE(bmr, activityLevel)
        
        val recommendedCalories = when (goal) {
            WeightGoal.LOSE -> tdee - 500  // Дефицит 500 ккал
            WeightGoal.MAINTAIN -> tdee
            WeightGoal.GAIN -> tdee + 500  // Профицит 500 ккал
        }
        
        // Расчёт макронутриентов (стандартное соотношение: 30% белки, 25% жиры, 45% углеводы)
        val proteinGrams = (recommendedCalories * 0.30) / 4.0  // 1г белка = 4 ккал
        val fatsGrams = (recommendedCalories * 0.25) / 9.0     // 1г жира = 9 ккал
        val carbsGrams = (recommendedCalories * 0.45) / 4.0    // 1г углеводов = 4 ккал
        
        return CalorieNormResult(
            bmr = bmr,
            tdee = tdee,
            recommendedCalories = recommendedCalories,
            proteinGrams = proteinGrams,
            fatsGrams = fatsGrams,
            carbsGrams = carbsGrams
        )
    }
}

enum class WeightGoal {
    LOSE,      // Похудение
    MAINTAIN,  // Поддержание
    GAIN       // Набор массы
}
