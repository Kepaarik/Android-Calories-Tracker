package com.calorietracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val volumeMl: Int,
    val date: String, // ISO 8601
    val createdAt: Long
)
