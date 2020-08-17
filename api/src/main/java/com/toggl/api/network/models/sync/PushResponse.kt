package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.api.models.SyncApiClient
import com.toggl.api.models.SyncApiPreferences
import com.toggl.api.models.SyncApiProject
import com.toggl.api.models.SyncApiTag
import com.toggl.api.models.SyncApiTask
import com.toggl.api.models.SyncApiTimeEntry
import com.toggl.api.models.SyncApiUser
import com.toggl.api.models.SyncApiWorkspace

@JsonClass(generateAdapter = true)
data class PushResponse(
    @Json(name = "time_entries")
    val timeEntries: List<PushAction<SyncApiTimeEntry>>,
    val workspaces: List<PushAction<SyncApiWorkspace>>,
    val projects: List<PushAction<SyncApiProject>>,
    val clients: List<PushAction<SyncApiClient>>,
    val tasks: List<PushAction<SyncApiTask>>,
    val tags: List<PushAction<SyncApiTag>>,
    val user: ActionResult<SyncApiUser>,
    val preferences: ActionResult<SyncApiPreferences>
)
