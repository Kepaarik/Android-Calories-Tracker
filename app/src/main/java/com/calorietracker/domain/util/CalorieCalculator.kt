package com.calorietracker.domain.util

import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender

/**
 * Калькулятор калорий на основе формулы Миффлина-Сан Жеора
 */
object CalorieCalculator {

    /**
     * Расчет базового уровня метаболизма (BMR) по формуле Миффлина-Сан Жеора
     * @param gender пол пользователя
     * @param weightKg вес в килограммах
     * @param heightCm рост в сантиметрах
     * @param age возраст в годах
     * @return BMR в калориях
     */
    fun calculateBMR(gender: Gender, weightKg: Double, heightCm: Double, age: Int): Double {
        return when (gender) {
            Gender.MALE -> 10 * weightKg + 6.25 * heightCm - 5 * age + 5
            Gender.FEMALE -> 10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }
    }

    /**
     * Расчет общего расхода энергии (TDEE) с учетом уровня активности
     * @param bMR базовый уровень метаболизма
     * @param activityLevel уровень активности
     * @return TDEE в калориях
     */
    fun calculateTDEE(bMR: Double, activityLevel: ActivityLevel): Double {
        return bMR * activityLevel.multiplier
    }

    /**
     * Расчет суточной нормы калорий
     * @param gender пол пользователя
     * @param weightKg вес в килограммах
     * @param heightCm рост в сантиметрах
     * @param age возраст в годах
     * @param activityLevel уровень активности
     * @return суточная норма калорий
     */
    fun calculateDailyCalorieNorm(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int,
        activityLevel: ActivityLevel
    ): Int {
        val bmr = calculateBMR(gender, weightKg, heightCm, age)
        val tdee = calculateTDEE(bmr, activityLevel)
        return tdee.toInt()
    }

    /**
     * Расчет рекомендуемого распределения БЖУ
     * @param dailyCalories суточная норма калорий
     * @return рекомендованные граммы белков, жиров и углеводов
     */
    fun calculateMacros(dailyCalories: Int): MacrosResult {
        // Стандартное соотношение: 30% белки, 25% жиры, 45% углеводы
        val proteinCalories = dailyCalories * 0.30
        val fatCalories = dailyCalories * 0.25
        val carbCalories = dailyCalories * 0.45

        // 1г белка = 4 ккал, 1г жира = 9 ккал, 1г углеводов = 4 ккал
        val proteinGrams = (proteinCalories / 4).toInt()
        val fatGrams = (fatCalories / 9).toInt()
        val carbGrams = (carbCalories / 4).toInt()

        return MacrosResult(
            proteins = proteinGrams.toDouble(),
            fats = fatGrams.toDouble(),
            carbs = carbGrams.toDouble()
        )
    }

    data class MacrosResult(
        val proteins: Double,
        val fats: Double,
        val carbs: Double
    )
}
