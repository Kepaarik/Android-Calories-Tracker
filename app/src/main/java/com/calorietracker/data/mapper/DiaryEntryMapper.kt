package com.calorietracker.data.mapper

import com.calorietracker.data.remote.dto.DiaryEntryDto
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product
import java.time.LocalDate

object DiaryEntryMapper {

    fun DiaryEntryDto.toDomain(product: Product): DiaryEntry {
        return DiaryEntry(
            id = this.id,
            productId = this.productId,
            product = product,
            mealType = parseMealType(this.mealType),
            weightGrams = this.weightGrams,
            calories = this.calories,
            proteins = this.proteins,
            fats = this.fats,
            carbs = this.carbs,
            date = LocalDate.parse(this.date),
            createdAt = this.createdAt
        )
    }

    fun DiaryEntry.toDto(): DiaryEntryDto {
        throw UnsupportedOperationException("DiaryEntry to DTO conversion requires product data")
    }

    private fun parseMealType(mealType: String): MealType {
        return when (mealType.uppercase()) {
            "BREAKFAST" -> MealType.BREAKFAST
            "LUNCH" -> MealType.LUNCH
            "DINNER" -> MealType.DINNER
            "SNACK" -> MealType.SNACK
            else -> MealType.SNACK
        }
    }

    fun MealType.toDto(): String {
        return when (this) {
            MealType.BREAKFAST -> "breakfast"
            MealType.LUNCH -> "lunch"
            MealType.DINNER -> "dinner"
            MealType.SNACK -> "snack"
        }
    }
}
