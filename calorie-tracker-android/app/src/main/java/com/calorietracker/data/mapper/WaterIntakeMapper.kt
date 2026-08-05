package com.calorietracker.data.mapper

import com.calorietracker.data.local.entity.WaterIntakeEntity
import com.calorietracker.domain.model.WaterIntake

object WaterIntakeMapper {
    
    fun WaterIntakeEntity.toDomain(): WaterIntake = WaterIntake(
        id = id,
        volumeMl = volumeMl,
        date = date,
        createdAt = createdAt
    )
    
    fun WaterIntake.toEntity(userId: Int): WaterIntakeEntity = WaterIntakeEntity(
        id = id,
        userId = userId,
        volumeMl = volumeMl,
        date = date,
        createdAt = createdAt
    )
}
