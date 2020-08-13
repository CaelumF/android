package com.toggl.api.network.models.reports

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GraphItem(
    val seconds: Long,
    @Json(name = "by_rate")
    val byRate: Map<String, Long>?
)
