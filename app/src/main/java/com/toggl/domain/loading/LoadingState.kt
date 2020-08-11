package com.toggl.domain.loading

import com.toggl.architecture.Loadable
import com.toggl.common.feature.navigation.BackStack
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.Tag
import com.toggl.models.domain.Task
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.models.domain.Workspace
import com.toggl.timer.suggestions.domain.Suggestion

data class LoadingState(
    val user: Loadable<User>,
    val workspaces: Collection<Workspace>,
    val projects: Collection<Project>,
    val clients: Collection<Client>,
    val tags: Collection<Tag>,
    val tasks: Collection<Task>,
    val timeEntries: Collection<TimeEntry>,
    val suggestions: Collection<Suggestion>,
    val calendars: Collection<Calendar>,
    val userPreferences: UserPreferences,
    val backStack: BackStack
)
