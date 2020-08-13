package com.toggl.api.network.models.reports

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RateInfo(
    @Json(name = "billable_seconds")
    val billableSeconds: Long,
    @Json(name = "hourly_rate_in_cents")
    val hourlyRateInCents: Long,
    val currency: String
)
