package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.api.models.ApiClient
import com.toggl.api.models.ApiPreferences
import com.toggl.api.models.ApiProject
import com.toggl.api.models.ApiTag
import com.toggl.api.models.ApiTask
import com.toggl.api.models.ApiTimeEntry
import com.toggl.api.models.ApiUser
import com.toggl.api.models.ApiWorkspace
import com.toggl.api.models.ApiWorkspaceFeature

@JsonClass(generateAdapter = true)
data class PullResponse(
    @Json(name = "server_time")
    val serverTime: Int,
    val clients: List<ApiClient>,
    val preferences: ApiPreferences,
    val projects: List<ApiProject>,
    val tags: List<ApiTag>,
    val tasks: List<ApiTask>,
    @Json(name = "time_entries")
    val timeEntries: List<ApiTimeEntry>,
    val user: ApiUser,
    val workspaces: List<ApiWorkspace>,
    @Json(name = "workspace_features")
    val workspaceFeatures: List<ApiWorkspaceFeature>
)
