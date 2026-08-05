package com.calorietracker.domain.util

import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender

object CalorieCalculator {

    /**
     * Формула Миффлина-Сан Жеора для расчёта базального метаболизма (BMR)
     */
    fun calculateBMR(gender: Gender, weightKg: Double, heightCm: Double, age: Int): Double {
        return when (gender) {
            Gender.MALE -> 10 * weightKg + 6.25 * heightCm - 5 * age + 5
            Gender.FEMALE -> 10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }
    }

    /**
     * Расчёт суточной нормы калорий (TDEE) с учётом уровня активности
     */
    fun calculateTDEE(bmr: Double, activityLevel: ActivityLevel): Double {
        return bmr * activityLevel.multiplier
    }

    /**
     * Полный расчёт суточной нормы калорий
     */
    fun calculateDailyCalorieNorm(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int,
        activityLevel: ActivityLevel
    ): Double {
        val bmr = calculateBMR(gender, weightKg, heightCm, age)
        return calculateTDEE(bmr, activityLevel)
    }

    /**
     * Расчёт целевых макронутриентов (БЖУ) в граммах
     * Пропорции: 30% белки, 30% жиры, 40% углеводы
     */
    fun calculateMacros(totalCalories: Double): Macronutrients {
        return Macronutrients(
            proteinsGrams = (totalCalories * 0.30) / 4, // 4 калории на грамм белка
            fatsGrams = (totalCalories * 0.30) / 9,     // 9 калорий на грамм жира
            carbsGrams = (totalCalories * 0.40) / 4     // 4 калории на грамм углеводов
        )
    }

    data class Macronutrients(
        val proteinsGrams: Double,
        val fatsGrams: Double,
        val carbsGrams: Double
    )
}
