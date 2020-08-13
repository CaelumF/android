package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiTask(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,

    val name: String,
    val active: Boolean,
    @Json(name = "project_id")
    val projectId: Long,
    @Json(name = "user_id")
    val userId: Long?,
    @Json(name = "workspace_id")
    val workspaceId: Long
)
