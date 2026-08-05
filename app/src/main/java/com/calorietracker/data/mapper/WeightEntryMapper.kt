package com.calorietracker.data.mapper

import com.calorietracker.data.remote.dto.WeightEntryDto
import com.calorietracker.domain.model.WeightEntry
import java.time.LocalDate

object WeightEntryMapper {

    fun WeightEntryDto.toDomain(): WeightEntry {
        return WeightEntry(
            id = this.id,
            userId = this.userId,
            weightKg = this.weightKg,
            date = LocalDate.parse(this.date),
            createdAt = this.createdAt
        )
    }

    fun WeightEntry.toDto(): WeightEntryDto {
        throw UnsupportedOperationException("WeightEntry to DTO conversion requires user ID")
    }
}
