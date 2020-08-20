package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class ActionResult<out T> {
    open class SuccessResult<out T> : ActionResult<T>()
    data class EntitySuccessResult<out T>(val result: T?) : SuccessResult<T>()
    data class ErrorResult<out T>(val result: SyncError) : ActionResult<T>()
}

@JsonClass(generateAdapter = true)
data class SyncError(
    @Json(name = "error_message")
    val errorMessage: ErrorMessage
)

@JsonClass(generateAdapter = true)
data class ErrorMessage(
    val code: Long,
    @Json(name = "default_message")
    val defaultMessage: String
)
