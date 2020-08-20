package com.toggl.reports.domain

import com.toggl.common.extensions.beginningOfWeek
import com.toggl.common.extensions.toBeginningOfTheDay
import com.toggl.models.common.DateRange
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.temporal.ChronoField

enum class ReportsShortcut {
    Today,
    Yesterday,
    ThisWeek,
    LastWeek,
    ThisMonth,
    LastMonth,
    ThisYear,
    LastYear
}

fun ReportsShortcut.dateRange(now: OffsetDateTime, beginningOfWeek: DayOfWeek): DateRange =
    when (this) {
        ReportsShortcut.Today -> DateRange(now, null)
        ReportsShortcut.Yesterday -> DateRange(now.minusDays(1), null)
        ReportsShortcut.ThisWeek -> {
            val firstDayOfWeek = now.beginningOfWeek(beginningOfWeek)
            DateRange(firstDayOfWeek, firstDayOfWeek.plusDays(6))
        }
        ReportsShortcut.LastWeek -> {
            val firstDayOfLastWeek = now.beginningOfWeek(beginningOfWeek).minusDays(7)
            DateRange(firstDayOfLastWeek, firstDayOfLastWeek.plusDays(6))
        }
        ReportsShortcut.ThisMonth -> {
            val firstOfMonth = now.with(ChronoField.DAY_OF_MONTH, 1).toBeginningOfTheDay()
            val lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1)
            DateRange(firstOfMonth, lastOfMonth)
        }
        ReportsShortcut.LastMonth -> {
            val firstOfMonth = now.with(ChronoField.DAY_OF_MONTH, 1).toBeginningOfTheDay()
            val lastOfLastMonth = firstOfMonth.minusDays(1)
            val firstOfLastMonth = lastOfLastMonth.with(ChronoField.DAY_OF_MONTH, 1).toBeginningOfTheDay()
            DateRange(firstOfLastMonth, lastOfLastMonth)
        }
        ReportsShortcut.ThisYear -> {
            val firstOfYear = now.with(ChronoField.DAY_OF_YEAR, 1).toBeginningOfTheDay()
            val lastOfYear = firstOfYear.plusYears(1).minusDays(1)
            DateRange(firstOfYear, lastOfYear)
        }
        ReportsShortcut.LastYear -> {
            val firstOfYear = now.with(ChronoField.DAY_OF_YEAR, 1).toBeginningOfTheDay()
            val lastOfLastYear = firstOfYear.minusDays(1)
            val firstOfLastYear = lastOfLastYear.with(ChronoField.DAY_OF_YEAR, 1).toBeginningOfTheDay()
            DateRange(firstOfLastYear, lastOfLastYear)
        }
    }
