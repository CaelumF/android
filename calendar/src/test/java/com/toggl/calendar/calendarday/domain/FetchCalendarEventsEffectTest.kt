package com.toggl.calendar.calendarday.domain

import com.toggl.calendar.common.CoroutineTest
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.CalendarService
import com.toggl.models.domain.UserPreferences
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

@DisplayName("The FetchCalendarEventsEffect effect")

class FetchCalendarEventsEffectTest : CoroutineTest() {
    private val userPreferences = UserPreferences.default

    @Test
    fun `The effect should call the calendar service's getCalendarEvents with the right from and start parameters`() =
        runBlockingTest {
            val calendarService = mockk<CalendarService>()
            coEvery { calendarService.getCalendarEvents(any(), any(), any()) }.returns(emptyList())
            val expectedStartTime = mockk<OffsetDateTime>()
            val expectedEndTime = mockk<OffsetDateTime>()

            val effect = FetchCalendarEventsEffect(
                calendarService = calendarService,
                fromStartDate = expectedStartTime,
                toEndDate = expectedEndTime,
                calendars = emptyMap(),
                userPreferences = userPreferences,
                dispatcherProvider = dispatcherProvider
            )

            val result = effect.execute()

            result.shouldBeTypeOf<CalendarDayAction.CalendarEventsFetched>()
            coVerify { calendarService.getCalendarEvents(expectedStartTime, expectedEndTime, any()) }
        }

    @Test
    fun `The effect should call the calendar service's getCalendarEvents with the calendar ids saved in the user preferences`() =
        runBlockingTest {
            val calendarService = mockk<CalendarService>()
            val expectedCalendars = (1..3).map { Calendar(it.toString(), it.toString(), it.toString()) }
            coEvery { calendarService.getCalendarEvents(any(), any(), any()) }.returns(emptyList())

            val effect = FetchCalendarEventsEffect(
                calendarService = calendarService,
                fromStartDate = mockk(),
                toEndDate = mockk(),
                calendars = expectedCalendars.associateBy { it.id },
                userPreferences = userPreferences.copy(calendarIds = listOf("1", "2", "3")),
                dispatcherProvider = dispatcherProvider
            )

            val result = effect.execute()

            result.shouldBeTypeOf<CalendarDayAction.CalendarEventsFetched>()
            coVerify { calendarService.getCalendarEvents(any(), any(), expectedCalendars) }
        }
}
