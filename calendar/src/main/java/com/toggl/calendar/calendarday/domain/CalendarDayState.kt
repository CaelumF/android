package com.toggl.calendar.calendarday.domain

import com.toggl.architecture.Loadable
import com.toggl.calendar.common.domain.CalendarState
import com.toggl.common.feature.navigation.BackStack
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import java.time.OffsetDateTime

data class CalendarDayState(
    val user: User,
    val timeEntries: Map<Long, TimeEntry>,
    val projects: Map<Long, Project>,
    val backStack: BackStack,
    val calendars: Map<String, Calendar>,
    val events: Map<String, CalendarEvent>,
    val selectedDate: OffsetDateTime,
    val userPreferences: UserPreferences
) {
    companion object {
        fun fromCalendarState(calendarState: CalendarState): CalendarDayState? {
            val user = calendarState.user as? Loadable.Loaded<User> ?: return null

            return CalendarDayState(
                user.value,
                calendarState.timeEntries,
                calendarState.projects,
                calendarState.backStack,
                calendarState.calendars,
                calendarState.calendarEvents,
                calendarState.localState.selectedDate,
                calendarState.userPreferences
            )
        }

        fun toCalendarState(calendarState: CalendarState, calendarDayState: CalendarDayState?) =
            calendarDayState?.let {
                calendarState.copy(
                    timeEntries = calendarDayState.timeEntries,
                    projects = calendarDayState.projects,
                    backStack = calendarDayState.backStack,
                    calendars = calendarDayState.calendars,
                    calendarEvents = calendarDayState.events,
                    localState = calendarState.localState.copy(
                        selectedDate = calendarDayState.selectedDate
                    )
                )
            } ?: calendarState
    }
}
