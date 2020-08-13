package com.toggl.domain.loading

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.provider.CalendarContract
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Subscription
import com.toggl.common.feature.extensions.getEnabledCalendars
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.common.services.time.TimeService
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class LoadCalendarsEventsSubscription(
    private val timeService: TimeService,
    private val minDateProvider: OffsetDateTime.() -> OffsetDateTime,
    private val maxDateProvider: OffsetDateTime.() -> OffsetDateTime,
    private val calendarEventsProvider: CalendarEventsProvider
) : Subscription<AppState, AppAction> {

    override fun subscribe(state: Flow<AppState>): Flow<AppAction.Loading> =
        state.map { s -> if (s.calendarPermissionWasGranted && s.user is Loadable.Loaded) s else null }
            .map { s -> s to timeService.toRange(minDateProvider, maxDateProvider) }
            .distinctUntilChanged()
            .flatMapLatest { (s, timeRange) ->
                if (s != null) calendarEventsProvider.toCalendarEventsFlow(s, timeRange)
                else {
                    calendarEventsProvider.unregister()
                    flowOf(emptyList())
                }
            }
            .distinctUntilChanged()
            .map { AppAction.Loading(LoadingAction.CalendarEventsLoaded(it)) }
            .onCompletion { calendarEventsProvider.unregister() }

    private fun TimeService.toRange(
        minDateProvider: OffsetDateTime.() -> OffsetDateTime,
        maxDateProvider: OffsetDateTime.() -> OffsetDateTime
    ): ClosedRange<OffsetDateTime> {
        val now = now()
        return now.minDateProvider()..now.maxDateProvider()
    }
}

data class CalendarEventsProvider(
    private val contentResolver: ContentResolver,
    private val dispatcherProvider: DispatcherProvider
) {
    private var calendarEventsContentObserver: CalendarEventsContentObserver? = null

    suspend fun toCalendarEventsFlow(appState: AppState, timeRange: ClosedRange<OffsetDateTime>): Flow<List<CalendarEvent>> = withContext(dispatcherProvider.io) {
        val calendarsFlow = MutableStateFlow<List<CalendarEvent>>(emptyList())
        calendarEventsContentObserver = CalendarEventsContentObserver(
            contentResolver,
            timeRange.start,
            timeRange.endInclusive,
            appState.calendars.values.getEnabledCalendars(appState.userPreferences.calendarIds),
            calendarsFlow
        ).also { observer ->
            contentResolver.registerContentObserver(CalendarContract.CONTENT_URI, true, observer)
            observer.onChange(true)
        }
        calendarsFlow
    }

    fun unregister() {
        calendarEventsContentObserver?.let { observer ->
            contentResolver.unregisterContentObserver(observer)
        }
        calendarEventsContentObserver = null
    }
}

private class CalendarEventsContentObserver(
    private val contentResolver: ContentResolver,
    private val fromStartDate: OffsetDateTime,
    private val toEndDate: OffsetDateTime,
    private val fromCalendars: List<Calendar>,
    private val calendarEventsFlow: MutableStateFlow<List<CalendarEvent>>
) : ContentObserver(null) {
    private val eventsProjection = arrayOf(
        CalendarContract.Instances._ID,
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.TITLE,
        CalendarContract.Instances.DISPLAY_COLOR,
        CalendarContract.Instances.CALENDAR_ID,
        CalendarContract.Instances.ALL_DAY,
        CalendarContract.Instances.CALENDAR_DISPLAY_NAME
    )
    private val eventIdIndex = 0
    private val eventStartDateIndex = 1
    private val eventEndDateIndex = 2
    private val eventDescriptionIndex = 3
    private val eventDisplayColorIndex = 4
    private val eventCalendarIdIndex = 5
    private val eventIsAllDayIndex = 6
    private val eventCalendarDisplayNameIndex = 7

    override fun deliverSelfNotifications(): Boolean = true

    override fun onChange(selfChange: Boolean) {
        val cursor = CalendarContract.Instances.query(
            contentResolver,
            eventsProjection,
            fromStartDate.toEpochSecond() * 1000,
            toEndDate.toEpochSecond() * 1000
        )

        val calendarIds = fromCalendars.map { it.id }.toSet()

        val newCalendarEvents = sequence {
            cursor?.use {
                if (it.count <= 0)
                    return@sequence

                while (it.moveToNext()) {
                    val isAllDay = it.getInt(eventIsAllDayIndex) == 1
                    val calendarId = it.getString(eventCalendarIdIndex)
                    if (!isAllDay && calendarIds.contains(calendarId)) {
                        yield(it.nextCalendarItem())
                    }
                }
            }
        }.toList()
        calendarEventsFlow.value = newCalendarEvents
    }

    private fun Cursor.nextCalendarItem(): CalendarEvent {
        val id = this.getString(eventIdIndex)
        val startDateUnixTime = this.getLong(eventStartDateIndex)
        val endDateUnixTime = this.getLong(eventEndDateIndex)
        val description = this.getString(eventDescriptionIndex)
        val colorHex = this.getInt(eventDisplayColorIndex)
        val calendarId = this.getString(eventCalendarIdIndex)
        val calendarName = this.getString(eventCalendarDisplayNameIndex)
        val rgb = String.format("#%06X", (0xFFFFFF and colorHex))

        val startTime = Instant.ofEpochMilli(startDateUnixTime).atOffset(ZoneOffset.UTC)

        return CalendarEvent(
            id = id,
            startTime = startTime,
            duration = Duration.ofMillis(endDateUnixTime - startDateUnixTime),
            description = description,
            color = rgb,
            calendarId = calendarId,
            calendarName = calendarName
        )
    }
}
