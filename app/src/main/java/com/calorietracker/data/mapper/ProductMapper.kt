package com.calorietracker.data.mapper

import com.calorietracker.data.remote.dto.ProductDto
import com.calorietracker.domain.model.Product

object ProductMapper {

    fun ProductDto.toDomain(): Product {
        return Product(
            id = this.id,
            name = this.name,
            caloriesPer100g = this.caloriesPer100g,
            proteinsPer100g = this.proteinsPer100g,
            fatsPer100g = this.fatsPer100g,
            carbsPer100g = this.carbsPer100g,
            barcode = this.barcode,
            createdAt = this.createdAt
        )
    }

    fun Product.toDto(): ProductDto {
        return ProductDto(
            id = this.id,
            name = this.name,
            caloriesPer100g = this.caloriesPer100g,
            proteinsPer100g = this.proteinsPer100g,
            fatsPer100g = this.fatsPer100g,
            carbsPer100g = this.carbsPer100g,
            barcode = this.barcode,
            createdAt = this.createdAt
        )
    }
}
