package com.toggl.timer.suggestions.domain

import com.toggl.common.Constants
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.validation.ApiToken

fun createInitialState(
    user: User = User(ApiToken.Invalid, defaultWorkspaceId = 10),
    projects: List<Project> = emptyList(),
    timeEntries: List<TimeEntry> = emptyList(),
    maxNumberOfSuggestions: Int = Constants.Suggestions.maxNumberOfSuggestions
) = SuggestionsState(
    user = user,
    projects = projects.associateBy { it.id },
    timeEntries = timeEntries.associateBy { it.id },
    maxNumberOfSuggestions = maxNumberOfSuggestions
)