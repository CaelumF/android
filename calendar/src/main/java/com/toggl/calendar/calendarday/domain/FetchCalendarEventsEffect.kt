package com.toggl.calendar.calendarday.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.core.Effect
import com.toggl.common.feature.extensions.getEnabledCalendars
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.CalendarService
import com.toggl.models.domain.UserPreferences
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

class FetchCalendarEventsEffect(
    private val calendarService: CalendarService,
    private val fromStartDate: OffsetDateTime,
    private val toEndDate: OffsetDateTime,
    private val calendars: Map<String, Calendar>,
    private val userPreferences: UserPreferences,
    private val dispatcherProvider: DispatcherProvider
) : Effect<CalendarDayAction.CalendarEventsFetched> {
    override suspend fun execute(): CalendarDayAction.CalendarEventsFetched? = withContext(dispatcherProvider.io) {
        val fromCalendars = calendars.values.getEnabledCalendars(userPreferences.calendarIds)

        calendarService.getCalendarEvents(fromStartDate, toEndDate, fromCalendars)
            .let(CalendarDayAction::CalendarEventsFetched)
    }
}
