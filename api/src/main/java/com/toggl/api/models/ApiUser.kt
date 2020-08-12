package com.toggl.api.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiUser(
    val id: Long?,
    @Json(name = "user_id")
    val userId: Long?,
    @Json(name = "api_token")
    val apiToken: String,
    @Json(name = "beginning_of_week")
    val beginningOfWeek: Int,
    @Json(name = "default_workspace_id")
    val defaultWorkspaceId: Long?,
    val email: String,
    val fullname: String
)
