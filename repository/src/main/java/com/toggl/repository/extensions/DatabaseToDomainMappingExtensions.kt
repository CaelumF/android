package com.toggl.repository.extensions

import com.toggl.database.models.DatabaseClient
import com.toggl.database.models.DatabaseProject
import com.toggl.database.models.DatabaseTag
import com.toggl.database.models.DatabaseTask
import com.toggl.database.models.DatabaseTimeEntry
import com.toggl.database.models.DatabaseTimeEntryWithTags
import com.toggl.database.models.DatabaseUser
import com.toggl.database.models.DatabaseWorkspace
import com.toggl.database.properties.updateWith
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.Tag
import com.toggl.models.domain.Task
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.domain.Workspace
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email

infix fun DatabaseTimeEntryWithTags.updateWith(updatedTimeEntry: TimeEntry): DatabaseTimeEntryWithTags = copy(
    timeEntry = timeEntry updateWith updatedTimeEntry,
    tags = tags // TODO 🤷‍
)

infix fun DatabaseTimeEntry.updateWith(updatedTimeEntry: TimeEntry): DatabaseTimeEntry = copy(
    description = description updateWith updatedTimeEntry.description,
    startTime = startTime updateWith updatedTimeEntry.startTime,
    duration = duration updateWith updatedTimeEntry.duration,
    billable = billable updateWith updatedTimeEntry.billable,
    workspaceId = workspaceId updateWith updatedTimeEntry.workspaceId,
    projectId = projectId updateWith updatedTimeEntry.projectId,
    taskId = taskId updateWith updatedTimeEntry.taskId,
    isDeleted = isDeleted.updateWith(updatedTimeEntry.isDeleted)
)

fun DatabaseTimeEntryWithTags.toModel() = TimeEntry(
    timeEntry.id,
    timeEntry.description.current,
    timeEntry.startTime.current,
    timeEntry.duration.current,
    timeEntry.billable.current,
    timeEntry.workspaceId.current,
    timeEntry.projectId.current,
    timeEntry.taskId.current,
    timeEntry.isDeleted.current,
    tags
)

fun DatabaseTimeEntry.toModelWithoutTags() = TimeEntry(
    id,
    description.current,
    startTime.current,
    duration.current,
    billable.current,
    workspaceId.current,
    projectId.current,
    taskId.current,
    isDeleted.current,
    emptyList()
)

fun DatabaseProject.toModel() = Project(
    id,
    name.current,
    color.current,
    active.current,
    isPrivate.current,
    billable.current,
    workspaceId.current,
    clientId.current
)

fun DatabaseTag.toModel() = Tag(
    id,
    name,
    workspaceId
)

fun DatabaseWorkspace.toModel() = Workspace(
    id,
    name,
    features
)

fun DatabaseClient.toModel() = Client(
    id,
    name.current,
    workspaceId.current
)

fun DatabaseTask.toModel() = Task(
    id,
    name.current,
    active.current,
    projectId.current,
    workspaceId.current,
    userId.current
)

fun DatabaseUser.toModel() = User(
    serverId,
    ApiToken.from(apiToken) as ApiToken.Valid,
    Email.from(email) as Email.Valid,
    name,
    defaultWorkspaceId
)
