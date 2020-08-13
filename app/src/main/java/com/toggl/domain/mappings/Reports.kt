package com.toggl.domain.mappings

import com.toggl.architecture.valueOrNull
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.reports.domain.ReportsAction
import com.toggl.reports.domain.ReportsState

fun mapAppStateToReportsState(appState: AppState): ReportsState? =
    appState.user.valueOrNull()?.let {
        ReportsState(
            user = it,
            clients = appState.clients,
            projects = appState.projects,
            workspaces = appState.workspaces,
            preferences = appState.userPreferences,
            localState = appState.reportsLocalState
        )
    }

fun mapReportsStateToAppState(appState: AppState, reportsState: ReportsState?): AppState =
    reportsState?.let {
        appState.copy(reportsLocalState = reportsState.localState)
    } ?: appState

fun mapReportsActionToAppAction(reportsAction: ReportsAction): AppAction =
    AppAction.Reports(reportsAction)
