package com.toggl.api.network.models.reports

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.api.network.adapters.SerializeAsDate
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
internal data class ProjectsSummaryBody(
    @SerializeAsDate
    @Json(name = "start_date")
    val startDate: OffsetDateTime,

    @SerializeAsDate
    @Json(name = "end_date")
    val endDate: OffsetDateTime?
)
