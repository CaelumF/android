package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiTag(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val name: String,
    @Json(name = "workspace_id")
    val workspaceId: Long
)

@JsonClass(generateAdapter = true)
data class SyncApiTag(
    val id: Long?,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val name: String?,
    @Json(name = "workspace_id")
    val workspaceId: Long?
)
