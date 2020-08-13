package com.toggl.reports.models

import com.toggl.api.models.Resolution

data class TotalsReport(
    val resolution: Resolution,
    val amounts: List<Amount>,
    val groups: List<ReportsTotalsGroup>
)
