package com.toggl.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiProject(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,

    @Json(name = "workspace_id")
    val workspaceId: Long,
    @Json(name = "client_id")
    val clientId: Long?,
    val name: String,
    @Json(name = "is_private")
    val isPrivate: Boolean,
    val active: Boolean,
    val color: String,
    val billable: Boolean?,
)

@JsonClass(generateAdapter = true)
data class SyncApiProject(
    val id: Long?,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    @Json(name = "workspace_id")
    val workspaceId: Long?,
    @Json(name = "client_id")
    val clientId: Long?,
    val name: String?,
    @Json(name = "is_private")
    val isPrivate: Boolean?,
    val active: Boolean?,
    val color: String?,
    val billable: Boolean?,
)
