package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import javax.inject.Inject

class CalculateCalorieNormUseCase @Inject constructor() {
    
    operator fun invoke(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int,
        activityLevel: ActivityLevel
    ): Int {
        val bmr = calculateBMR(gender, weightKg, heightCm, age)
        return calculateTDEE(bmr, activityLevel)
    }
    
    /**
     * Расчет базового уровня метаболизма (BMR) по формуле Миффлина-Сан Жеора
     */
    private fun calculateBMR(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int
    ): Int {
        return when (gender) {
            Gender.MALE -> {
                (10 * weightKg + 6.25 * heightCm - 5 * age + 5).toInt()
            }
            Gender.FEMALE -> {
                (10 * weightKg + 6.25 * heightCm - 5 * age - 161).toInt()
            }
        }
    }
    
    /**
     * Расчет суточной нормы калорий (TDEE) с учетом уровня активности
     */
    private fun calculateTDEE(bmr: Int, activityLevel: ActivityLevel): Int {
        return (bmr * activityLevel.multiplier).toInt()
    }
}
