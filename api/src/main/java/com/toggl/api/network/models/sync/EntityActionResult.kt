package com.toggl.api.network.models.sync

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActionResult<out T>(
    val success: Boolean,
    val result: T?
)
