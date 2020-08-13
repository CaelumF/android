package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class ApiTimeEntry(
    val id: Long,
    @Json(name = "server_deleted_at")
    val serverDeletedAt: Any?,
    val billable: Boolean,
    val description: String,
    val duration: Long,
    @Json(name = "project_id")
    val projectId: Long?,
    val start: OffsetDateTime,
    @Json(name = "tag_ids")
    val tagIds: List<Long>?,
    @Json(name = "task_id")
    val taskId: Long?,
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "workspace_id")
    val workspaceId: Long
)
