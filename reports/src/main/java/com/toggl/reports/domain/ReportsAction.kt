package com.toggl.reports.domain

import com.toggl.architecture.Failure
import com.toggl.reports.models.ReportData

sealed class ReportsAction {
    object ViewAppeared : ReportsAction()
    data class ReportLoaded(val reportData: ReportData) : ReportsAction()
    data class ReportFailed(val failure: Failure) : ReportsAction()
    object AvailableOnOtherPlansTapped : ReportsAction()
}

fun ReportsAction.formatForDebug() =
    when (this) {
        ReportsAction.ViewAppeared -> "Reports view appeared"
        is ReportsAction.ReportLoaded -> "Reports Loaded"
        is ReportsAction.ReportFailed -> "Report loading failed with ${failure.errorMessage}"
        ReportsAction.AvailableOnOtherPlansTapped -> "Available on other plans tapped"
    }
