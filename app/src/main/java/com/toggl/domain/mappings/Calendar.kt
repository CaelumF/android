package com.toggl.domain.mappings

import com.toggl.calendar.common.domain.CalendarAction
import com.toggl.calendar.common.domain.CalendarState
import com.toggl.domain.AppAction
import com.toggl.domain.AppState

fun mapAppStateToCalendarState(appState: AppState): CalendarState =
    CalendarState(
        appState.user,
        appState.timeEntries,
        appState.projects,
        appState.clients,
        appState.backStack,
        appState.calendars,
        appState.calendarEvents,
        appState.userPreferences,
        appState.calendarLocalState
    )

fun mapCalendarStateToAppState(appState: AppState, calendarState: CalendarState): AppState =
    appState.copy(
        calendars = calendarState.calendars,
        backStack = calendarState.backStack,
        projects = calendarState.projects,
        timeEntries = calendarState.timeEntries,
        calendarLocalState = calendarState.localState
    )

fun mapCalendarActionToAppAction(timerAction: CalendarAction): AppAction =
    AppAction.Calendar(timerAction)
