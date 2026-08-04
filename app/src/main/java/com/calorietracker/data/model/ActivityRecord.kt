package com.calorietracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_records")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val steps: Long = 0,
    val distance: Float = 0f, // in meters
    val caloriesBurned: Int = 0,
    val activeMinutes: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val source: ActivitySource = ActivitySource.GOOGLE_FIT
)

enum class ActivitySource {
    GOOGLE_FIT,
    MANUAL,
    DEVICE_SENSOR
}
