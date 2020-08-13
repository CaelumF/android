package com.toggl.api.network.models.feedback

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
internal data class FeedbackBody(
    val email: String,
    val message: String,
    val data: List<KeyValue>
) : Serializable

@JsonClass(generateAdapter = true)
data class KeyValue(val key: String, val value: String)

fun Map<String, String>.toKeyValue() = this.map { (k, v) -> KeyValue(k, v) }
