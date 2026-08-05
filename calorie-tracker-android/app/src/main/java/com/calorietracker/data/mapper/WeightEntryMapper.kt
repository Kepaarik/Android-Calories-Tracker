package com.calorietracker.data.mapper

import com.calorietracker.data.local.entity.WeightEntryEntity
import com.calorietracker.domain.model.WeightEntry

object WeightEntryMapper {
    
    fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
        id = id,
        userId = userId,
        weightKg = weightKg,
        date = date,
        createdAt = createdAt
    )
    
    fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
        id = id,
        userId = userId,
        weightKg = weightKg,
        date = date,
        createdAt = createdAt
    )
}
