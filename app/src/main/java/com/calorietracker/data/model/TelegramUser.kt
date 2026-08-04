package com.calorietracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telegram_user")
data class TelegramUser(
    @PrimaryKey
    val id: Long,
    val firstName: String,
    val lastName: String? = null,
    val username: String? = null,
    val photoUrl: String? = null,
    val authDate: Long = System.currentTimeMillis(),
    val hash: String
)
