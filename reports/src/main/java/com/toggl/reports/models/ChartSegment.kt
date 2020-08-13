package com.toggl.reports.models

import java.time.Duration

data class ChartSegment(
    val trackedTime: Duration,
    val billableSeconds: Duration,
    val percentage: Float,
    val projectName: String,
    val clientName: String?,
    val color: String
)
