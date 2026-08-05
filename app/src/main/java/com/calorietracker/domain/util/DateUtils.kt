package com.calorietracker.domain.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale("ru"))
    private val shortFormatter = DateTimeFormatter.ofPattern("dd.MM", java.util.Locale("ru"))

    fun LocalDate.toIsoString(): String = this.format(isoFormatter)

    fun LocalDate.toDisplayString(): String = this.format(displayFormatter)

    fun LocalDate.toShortString(): String = this.format(shortFormatter)

    fun fromIsoString(dateString: String): LocalDate = LocalDate.parse(dateString, isoFormatter)

    fun getToday(): LocalDate = LocalDate.now()

    fun getYesterday(): LocalDate = LocalDate.now().minusDays(1)

    fun getStartOfWeek(): LocalDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY)

    fun getEndOfWeek(): LocalDate = LocalDate.now().with(java.time.DayOfWeek.SUNDAY)

    fun getLast7Days(): List<LocalDate> {
        return (0 until 7).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
    }

    fun getLast30Days(): List<LocalDate> {
        return (0 until 30).map { LocalDate.now().minusDays(it.toLong()) }
    }
}
