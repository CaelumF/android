package com.toggl.api.models

import com.squareup.moshi.Json

enum class Resolution {
    @Json(name = "day") Day,
    @Json(name = "week") Week,
    @Json(name = "month") Month
}
