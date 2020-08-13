package com.toggl.api.network.models.reports

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchProjectsBody(
    val ids: List<Long>
)
