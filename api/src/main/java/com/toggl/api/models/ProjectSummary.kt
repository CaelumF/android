package com.toggl.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectSummary(
    @Json(name = "project_id")
    val projectId: Long?,
    @Json(name = "tracked_seconds")
    val trackedSeconds: Long,
    @Json(name = "billable_seconds")
    val billableSeconds: Long?
)
