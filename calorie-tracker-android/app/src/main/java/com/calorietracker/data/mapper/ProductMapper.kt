package com.calorietracker.data.mapper

import com.calorietracker.data.local.entity.ProductEntity
import com.calorietracker.domain.model.Product

object ProductMapper {
    
    fun ProductEntity.toDomain(): Product = Product(
        id = id,
        name = name,
        caloriesPer100g = caloriesPer100g,
        proteinsPer100g = proteinsPer100g,
        fatsPer100g = fatsPer100g,
        carbsPer100g = carbsPer100g,
        barcode = barcode,
        createdAt = createdAt
    )
    
    fun Product.toEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        caloriesPer100g = caloriesPer100g,
        proteinsPer100g = proteinsPer100g,
        fatsPer100g = fatsPer100g,
        carbsPer100g = carbsPer100g,
        barcode = barcode,
        createdAt = createdAt
    )
}
