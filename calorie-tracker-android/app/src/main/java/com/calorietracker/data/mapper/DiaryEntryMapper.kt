package com.calorietracker.data.mapper

import com.calorietracker.data.local.entity.DiaryEntryEntity
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType

object DiaryEntryMapper {
    
    fun DiaryEntryEntity.toDomain(product: com.calorietracker.domain.model.Product): DiaryEntry = DiaryEntry(
        id = id,
        productId = productId,
        product = product,
        mealType = MealType.valueOf(mealType),
        weightGrams = weightGrams,
        calories = calories,
        proteins = proteins,
        fats = fats,
        carbs = carbs,
        date = date,
        createdAt = createdAt
    )
    
    fun DiaryEntry.toEntity(): DiaryEntryEntity = DiaryEntryEntity(
        id = id,
        userId = 0, // Will be set from user context
        productId = productId,
        mealType = mealType.name,
        weightGrams = weightGrams,
        calories = calories,
        proteins = proteins,
        fats = fats,
        carbs = carbs,
        date = date,
        createdAt = createdAt
    )
}
