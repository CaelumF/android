package com.toggl.common.feature.services.calendar

import com.toggl.models.domain.EditableTimeEntry
import java.time.Duration
import java.time.OffsetDateTime

data class CalendarEvent(
    val id: String,
    val startTime: OffsetDateTime,
    val duration: Duration,
    val description: String,
    val color: String?,
    val calendarId: String,
    val calendarName: String = ""
)

fun CalendarEvent.toEditableTimeEntry(workspaceId: Long) =
    EditableTimeEntry.empty(workspaceId).copy(
        description = description,
        duration = duration,
        startTime = startTime
    )
