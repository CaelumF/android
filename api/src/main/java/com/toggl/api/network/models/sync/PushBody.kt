package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.api.models.SyncApiClient
import com.toggl.api.models.SyncApiPreferences
import com.toggl.api.models.SyncApiProject
import com.toggl.api.models.SyncApiTag
import com.toggl.api.models.SyncApiTask
import com.toggl.api.models.SyncApiUser
import com.toggl.api.models.SyncApiWorkspace
import com.toggl.api.models.SyncApiTimeEntry

@JsonClass(generateAdapter = true)
data class PushBody(
    @Json(name = "time_entries")
    val timeEntries: List<PushAction<SyncApiTimeEntry>>?,
    val workspaces: List<PushAction<SyncApiWorkspace>>?,
    val projects: List<PushAction<SyncApiProject>>?,
    val clients: List<PushAction<SyncApiClient>>?,
    val tasks: List<PushAction<SyncApiTask>>?,
    val tags: List<PushAction<SyncApiTag>>?,
    val preferences: PushAction<SyncApiPreferences>?,
    val user: PushAction<SyncApiUser>?,
)

fun PushBody.isEmpty() =
    (timeEntries == null || timeEntries.none()) &&
    (tags == null || tags.none())  &&
    (projects == null || projects.none())  &&
    (clients == null || clients.none())  &&
    (tasks == null || tasks.none())  &&
    (workspaces == null || workspaces.none())  &&
    preferences == null  &&
    user == null
