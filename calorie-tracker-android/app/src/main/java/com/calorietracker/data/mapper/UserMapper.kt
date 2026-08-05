package com.calorietracker.data.mapper

import com.calorietracker.data.local.entity.UserEntity
import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User

object UserMapper {
    
    fun UserEntity.toDomain(): User = User(
        id = id,
        username = username,
        email = email,
        age = age,
        gender = gender?.let { Gender.valueOf(it) },
        heightCm = heightCm,
        weightKg = weightKg,
        activityLevel = activityLevel?.let { ActivityLevel.valueOf(it) },
        createdAt = createdAt
    )
    
    fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        username = username,
        email = email,
        age = age,
        gender = gender?.name,
        heightCm = heightCm,
        weightKg = weightKg,
        activityLevel = activityLevel?.name,
        createdAt = createdAt
    )
}
