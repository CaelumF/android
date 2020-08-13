package com.toggl.common.feature.services.calendar

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import com.toggl.repository.interfaces.SettingsRepository
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class CursorCalendarService @Inject constructor(
    val context: Context,
    val settingsRepository: SettingsRepository
) : CalendarService {

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

    override suspend fun getCalendarEvents(
        fromStartDate: OffsetDateTime,
        toEndDate: OffsetDateTime,
        fromCalendars: List<Calendar>
    ): List<CalendarEvent> {
        if (!context.permissionToReadCalendarWasGranted() || fromCalendars.isEmpty())
            return emptyList()

        val cursor = CalendarContract.Instances.query(
            context.contentResolver,
            eventsProjection,
            fromStartDate.toEpochSecond() * 1000,
            toEndDate.toEpochSecond() * 1000
        )

        val calendarIds = fromCalendars.map { it.id }.toSet()

        return sequence {
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
