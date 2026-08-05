package com.calorietracker.domain.model

data class Product(
    val id: Int,
    val name: String,
    val caloriesPer100g: Double,
    val proteinsPer100g: Double,
    val fatsPer100g: Double,
    val carbsPer100g: Double,
    val barcode: String?,
    val createdAt: Long
) {
    fun calculateNutrition(weightGrams: Int): NutritionalInfo {
        val factor = weightGrams / 100.0
        return NutritionalInfo(
            calories = (caloriesPer100g * factor).roundToDecimal(),
            proteins = (proteinsPer100g * factor).roundToDecimal(),
            fats = (fatsPer100g * factor).roundToDecimal(),
            carbs = (carbsPer100g * factor).roundToDecimal()
        )
    }

    private fun Double.roundToDecimal(): Double = kotlin.math.round(this * 10) / 10
}

data class NutritionalInfo(
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
) {
    operator fun plus(other: NutritionalInfo): NutritionalInfo {
        return NutritionalInfo(
            calories = this.calories + other.calories,
            proteins = this.proteins + other.proteins,
            fats = this.fats + other.fats,
            carbs = this.carbs + other.carbs
        )
    }
}
