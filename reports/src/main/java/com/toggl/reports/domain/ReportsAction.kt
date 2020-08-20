package com.toggl.reports.domain

import com.toggl.architecture.Failure
import com.toggl.reports.models.ReportData
import java.time.OffsetDateTime

sealed class ReportsAction {
    object ViewAppeared : ReportsAction()
    data class ReportLoaded(val reportData: ReportData) : ReportsAction()
    data class ReportFailed(val failure: Failure) : ReportsAction()
    data class DateRangeSelected(val dateRangeSelection: DateRangeSelection) : ReportsAction()
    data class DatePicked(val date: OffsetDateTime) : ReportsAction()
    data class ShortcutPicked(val shortcut: ReportsShortcut) : ReportsAction()
    object OpenDateRangePickerButtonTapped : ReportsAction()
    object DateRangePickerCloseButtonTapped : ReportsAction()
    object DateRangePickerAcceptButtonTapped : ReportsAction()
    object AvailableOnOtherPlansTapped : ReportsAction()
}

fun ReportsAction.formatForDebug() =
    when (this) {
        ReportsAction.ViewAppeared -> "Reports view appeared"
        is ReportsAction.ReportLoaded -> "Reports Loaded"
        is ReportsAction.ReportFailed -> "Report loading failed with ${failure.errorMessage}"
        ReportsAction.AvailableOnOtherPlansTapped -> "Available on other plans tapped"
        is ReportsAction.DatePicked -> "Picked date $date"
        is ReportsAction.ShortcutPicked -> "Picked shortcut $shortcut"
        ReportsAction.OpenDateRangePickerButtonTapped -> "Open date picker button tapped"
        ReportsAction.DateRangePickerCloseButtonTapped -> "Close date picker button tapped"
        ReportsAction.DateRangePickerAcceptButtonTapped -> "Date accepted button tapped"
        is ReportsAction.DateRangeSelected -> "Selected date range ${dateRangeSelection.dateRange} due to ${dateRangeSelection.source}"
    }
