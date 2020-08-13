package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiWorkspace(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val name: String,
    val admin: Boolean,
    @Json(name = "only_admins_may_create_projects")
    val onlyAdminsMayCreateProjects: Boolean,
    @Json(name = "projects_billable_by_default")
    val projectsBillableByDefault: Boolean
)

@JsonClass(generateAdapter = true)
data class SyncApiWorkspace(
    val id: Long?,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val name: String?,
    val admin: Boolean?,
    @Json(name = "only_admins_may_create_projects")
    val onlyAdminsMayCreateProjects: Boolean?,
    @Json(name = "projects_billable_by_default")
    val projectsBillableByDefault: Boolean?
)
