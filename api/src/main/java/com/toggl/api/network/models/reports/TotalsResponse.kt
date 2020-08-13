package com.toggl.api.network.models.reports

import com.squareup.moshi.JsonClass
import com.toggl.api.models.Resolution

@JsonClass(generateAdapter = true)
data class TotalsResponse(
    val seconds: Long,
    val graph: List<GraphItem>,
    val resolution: Resolution,
    val rates: List<RateInfo>?
)
