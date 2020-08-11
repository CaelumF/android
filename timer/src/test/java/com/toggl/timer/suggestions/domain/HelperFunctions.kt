package com.toggl.timer.suggestions.domain

import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email

fun createInitialState(
    projects: List<Project> = emptyList(),
    timeEntries: List<TimeEntry> = emptyList(),
    suggestions: List<Suggestion> = emptyList(),
    clients: Map<Long, Client> = emptyMap(),
) = SuggestionsState(
    projects = projects.associateBy { it.id },
    clients = clients,
    timeEntries = timeEntries.associateBy { it.id },
    suggestions = suggestions,
)

fun createUser(
    id: Long = 0,
    token: ApiToken.Valid = ApiToken.from("12345678901234567890123456789012") as ApiToken.Valid,
    workspaceId: Long = 10L,
    email: Email.Valid = Email.from("email@email.com") as Email.Valid,
    name: String = ""
) = User(
    id = id,
    apiToken = token,
    defaultWorkspaceId = workspaceId,
    email = email,
    name = name
)

fun createSuggestionSources(
    workspaceId: Long = 1,
    timeEntries: Map<Long, TimeEntry> = mapOf(),
    projects: Map<Long, Project> = mapOf(),
    calendarEvents: Map<String, CalendarEvent> = mapOf()
) = SuggestionData(
    workspaceId,
    timeEntries,
    projects,
    calendarEvents
)
