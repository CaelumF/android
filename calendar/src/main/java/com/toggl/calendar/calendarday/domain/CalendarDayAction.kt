package com.toggl.calendar.calendarday.domain

import com.toggl.calendar.common.domain.CalendarItem
import java.time.OffsetDateTime

sealed class CalendarDayAction {
    data class ItemTapped(val calendarItem: CalendarItem) : CalendarDayAction()
    data class EmptyPositionLongPressed(val startTime: OffsetDateTime) : CalendarDayAction()
    data class TimeEntryDragged(val startTime: OffsetDateTime) : CalendarDayAction()
    data class StartTimeDragged(val startTime: OffsetDateTime) : CalendarDayAction()
    data class StopTimeDragged(val stopTime: OffsetDateTime) : CalendarDayAction()

    companion object
}

fun CalendarDayAction.formatForDebug() =
    when (this) {
        is CalendarDayAction.ItemTapped -> "Calendar item tapped: $calendarItem"
        is CalendarDayAction.EmptyPositionLongPressed -> "Calendar empty position tapped on: $startTime"
        is CalendarDayAction.TimeEntryDragged -> "Calendar item dragged to: $startTime"
        is CalendarDayAction.StartTimeDragged -> "Calendar item start time dragged to: $startTime"
        is CalendarDayAction.StopTimeDragged -> "Calendar item stop time dragged to: $stopTime"
    }
