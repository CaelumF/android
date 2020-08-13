package com.toggl.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.models.domain.DateFormat
import com.toggl.models.domain.DurationFormat

@JsonClass(generateAdapter = true)
data class ApiPreferences(
    @Json(name = "timeofday_format")
    val timeOfDayFormat: String,

    @Json(name = "date_format")
    val dateFormat: DateFormat,

    @Json(name = "duration_format")
    val durationFormat: DurationFormat,

    @Json(name = "CollapseTimeEntries")
    val collapseTimeEntries: Boolean
)


@JsonClass(generateAdapter = true)
data class SyncApiPreferences(
    @Json(name = "timeofday_format")
    val timeOfDayFormat: String?,

    @Json(name = "date_format")
    val dateFormat: DateFormat?,

    @Json(name = "duration_format")
    val durationFormat: DurationFormat?,

    @Json(name = "CollapseTimeEntries")
    val collapseTimeEntries: Boolean?
)
