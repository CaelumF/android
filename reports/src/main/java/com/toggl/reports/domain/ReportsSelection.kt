package com.toggl.reports.domain

import com.toggl.models.common.DateRange

data class DateRangeSelection(
    val dateRange: DateRange,
    val source: SelectionSource
)

sealed class SelectionSource {
    object Initial : SelectionSource()
    object Calendar : SelectionSource()
    data class Shortcut(val shortcut: ReportsShortcut) : SelectionSource()
}
