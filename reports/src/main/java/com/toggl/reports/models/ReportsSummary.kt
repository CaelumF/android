package com.toggl.reports.models

import java.time.Duration

data class ProjectSummaryReport(
    val totalSeconds: Duration,
    val billablePercentage: Float,
    val segments: List<ChartSegment>,
)
