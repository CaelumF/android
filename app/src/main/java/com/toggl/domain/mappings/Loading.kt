package com.toggl.domain.mappings

import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.domain.loading.LoadingAction
import com.toggl.domain.loading.LoadingState

fun mapAppStateToLoadingState(appState: AppState): LoadingState =
    LoadingState(
        user = appState.user,
        tags = appState.tags.values,
        projects = appState.projects.values,
        tasks = appState.tasks.values,
        clients = appState.clients.values,
        workspaces = appState.workspaces.values,
        timeEntries = appState.timeEntries.values,
        suggestions = appState.suggestions,
        calendars = appState.calendars.values,
        calendarEvents = appState.calendarEvents.values,
        userPreferences = appState.userPreferences,
        backStack = appState.backStack
    )

fun mapLoadingStateToAppState(appState: AppState, loadingState: LoadingState): AppState =
    appState.copy(
        user = loadingState.user,
        tags = loadingState.tags.associateBy { it.id },
        projects = loadingState.projects.associateBy { it.id },
        tasks = loadingState.tasks.associateBy { it.id },
        clients = loadingState.clients.associateBy { it.id },
        workspaces = loadingState.workspaces.associateBy { it.id },
        timeEntries = loadingState.timeEntries.associateBy { it.id },
        suggestions = loadingState.suggestions.toList(),
        calendars = loadingState.calendars.associateBy { it.id },
        calendarEvents = loadingState.calendarEvents.associateBy { it.id },
        userPreferences = loadingState.userPreferences,
        backStack = loadingState.backStack
    )

fun mapLoadingActionToAppAction(loadingAction: LoadingAction): AppAction =
    AppAction.Loading(loadingAction)
