package com.calorietracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val caloriesPer100g: Double,
    val proteinsPer100g: Double,
    val fatsPer100g: Double,
    val carbsPer100g: Double,
    val barcode: String? = null,
    val createdAt: Long
)
