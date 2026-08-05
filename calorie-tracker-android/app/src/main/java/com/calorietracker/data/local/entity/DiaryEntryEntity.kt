package com.calorietracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val mealType: String, // ENUM as String
    val weightGrams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val date: String, // ISO 8601
    val createdAt: Long
)
