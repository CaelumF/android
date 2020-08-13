package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiClient(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val name: String,
    @Json(name = "wid")
    val workspaceId: Long,
)
