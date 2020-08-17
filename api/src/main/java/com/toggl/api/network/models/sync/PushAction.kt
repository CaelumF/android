package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PushAction<out T>(
    val type: ActionType,
    val meta: MetaInfo?,
    val payload: ActionResult<T>?
)

@JsonClass(generateAdapter = true)
data class MetaInfo(
    val id: Long?,
    @Json(name = "client_assigned_id")
    val clientAssignedId: String?,
    @Json(name = "workspace_id")
    val workspaceId: Long?,
    @Json(name = "project_id")
    val projectId: Long?
)

enum class ActionType {
    @Json(name = "create") Create,
    @Json(name = "update") Update,
    @Json(name = "delete") Delete
}