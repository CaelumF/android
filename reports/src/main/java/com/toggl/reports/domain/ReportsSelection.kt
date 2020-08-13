package com.toggl.reports.domain

import java.time.OffsetDateTime

data class DateRangeSelection(
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime?,
    val source: SelectionSource
)

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

sealed class SelectionSource {
    object Initial : SelectionSource()
    object Calendar : SelectionSource()
    class Shortcut(val shortcut: ReportsShortcut) : SelectionSource()
}
