package com.toggl.domain.loading

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

sealed class LoadingAction {
    object StartLoading : LoadingAction()
    data class UserLoaded(val user: User?) : LoadingAction()
    data class TagsLoaded(val tags: List<Tag>) : LoadingAction()
    data class WorkspacesLoaded(val workspaces: List<Workspace>) : LoadingAction()
    data class ProjectsLoaded(val projects: List<Project>) : LoadingAction()
    data class ClientsLoaded(val clients: List<Client>) : LoadingAction()
    data class TasksLoaded(val tasks: List<Task>) : LoadingAction()
    data class TimeEntriesLoaded(val timeEntries: List<TimeEntry>) : LoadingAction()
    data class UserPreferencesLoaded(val userPreferences: UserPreferences) : LoadingAction()
    data class CalendarsLoaded(val calendars: List<Calendar>) : LoadingAction()
    data class SuggestionsLoaded(val suggestions: List<Suggestion>) : LoadingAction()
}

fun LoadingAction.formatForDebug() =
    when (this) {
        LoadingAction.StartLoading -> "Entities started loading"
        is LoadingAction.WorkspacesLoaded -> "Loaded ${workspaces.size} Workspaces"
        is LoadingAction.TimeEntriesLoaded -> "Loaded ${timeEntries.size} Time Entries"
        is LoadingAction.ProjectsLoaded -> "Loaded ${projects.size} projects"
        is LoadingAction.ClientsLoaded -> "Loaded ${clients.size} clients"
        is LoadingAction.TagsLoaded -> "Loaded ${tags.size} tags"
        is LoadingAction.TasksLoaded -> "Loaded ${tasks.size} tasks"
        is LoadingAction.UserPreferencesLoaded -> "Loaded $userPreferences"
        is LoadingAction.UserLoaded -> "Loaded $user"
        is LoadingAction.CalendarsLoaded -> "Loaded ${calendars.size} calendars"
        is LoadingAction.SuggestionsLoaded -> "Loaded ${suggestions.size} suggestions"
    }
