package com.toggl.repository.extensions

import com.toggl.database.models.DatabaseTimeEntry
import com.toggl.database.models.DatabaseTimeEntryWithTags
import com.toggl.database.models.DatabaseUser
import com.toggl.database.properties.BooleanSyncProperty
import com.toggl.database.properties.IntSyncProperty
import com.toggl.database.properties.LongSyncProperty
import com.toggl.database.properties.StringSyncProperty
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.repository.dto.CreateTimeEntryDTO
import com.toggl.repository.dto.StartTimeEntryDTO

fun CreateTimeEntryDTO.toDatabaseModel() = DatabaseTimeEntryWithTags(
    toDatabaseTimeEntry(),
    tagIds
)

fun StartTimeEntryDTO.toDatabaseModel() = DatabaseTimeEntryWithTags(
    toDatabaseTimeEntry(),
    tagIds
)

private fun CreateTimeEntryDTO.toDatabaseTimeEntry() = DatabaseTimeEntry.from(
    serverId = null,
    description = description,
    startTime = startTime,
    duration = duration,
    billable = billable,
    workspaceId = workspaceId,
    projectId = projectId,
    taskId = taskId,
    isDeleted = false
)

private fun StartTimeEntryDTO.toDatabaseTimeEntry() = DatabaseTimeEntry.from(
    serverId = null,
    description = description,
    startTime = startTime,
    duration = null,
    billable = billable,
    workspaceId = workspaceId,
    projectId = projectId,
    taskId = taskId,
    isDeleted = false
)

fun User.toDatabaseModel() = DatabaseUser(
    serverId = id,
    apiToken = apiToken.toString(),
    email = StringSyncProperty.from(email.toString()),
    name = StringSyncProperty.from(name),
    defaultWorkspaceId = LongSyncProperty.from(defaultWorkspaceId),
    // User Preferences
    manualModeEnabled = BooleanSyncProperty.from(UserPreferences.default.manualModeEnabled),
    twentyFourHourClockEnabled = BooleanSyncProperty.from(UserPreferences.default.twentyFourHourClockEnabled),
    groupSimilarTimeEntriesEnabled = BooleanSyncProperty.from(UserPreferences.default.groupSimilarTimeEntriesEnabled),
    cellSwipeActionsEnabled = UserPreferences.default.cellSwipeActionsEnabled,
    calendarIntegrationEnabled = UserPreferences.default.calendarIntegrationEnabled,
    calendarIds = UserPreferences.default.calendarIds,
    dateFormat = StringSyncProperty.from(UserPreferences.default.dateFormat.name),
    durationFormat = StringSyncProperty.from(UserPreferences.default.durationFormat.name),
    firstDayOfTheWeek = IntSyncProperty.from(UserPreferences.default.firstDayOfTheWeek.value),
    smartAlertsOption = StringSyncProperty.from(UserPreferences.default.smartAlertsOption.name)
)
