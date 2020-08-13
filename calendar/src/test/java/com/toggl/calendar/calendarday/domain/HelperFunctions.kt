package com.toggl.calendar.calendarday.domain

import com.toggl.calendar.common.validUser
import com.toggl.common.feature.models.SelectedCalendarItem
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.UserPreferences
import java.time.OffsetDateTime

fun createInitialState(
    timeEntries: List<TimeEntry> = listOf(),
    calendarEvents: List<CalendarEvent> = listOf(),
    projects: List<Project> = listOf(),
    selectedItem: SelectedCalendarItem? = null,
    date: OffsetDateTime = OffsetDateTime.now(),
    userPreferences: UserPreferences = UserPreferences.default,
    calendars: Map<String, Calendar> = emptyMap()
) = CalendarDayState(
    user = validUser,
    timeEntries = timeEntries.associateBy { it.id },
    projects = projects.associateBy { it.id },
    backStack = if (selectedItem == null) emptyList() else backStackOf(Route.ContextualMenu(selectedItem)),
    events = calendarEvents.associateBy { it.id },
    selectedDate = date,
    userPreferences = userPreferences,
    calendars = calendars
)
