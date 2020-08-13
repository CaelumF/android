package com.toggl.api.network.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class SerializeAsDate

internal class DateAdapter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @ToJson
    fun toJson(@SerializeAsDate date: OffsetDateTime): String = formatter.format(date)

    @FromJson
    @SerializeAsDate
    fun fromJson(dateString: String): OffsetDateTime = formatter.parse(dateString) as OffsetDateTime
}
