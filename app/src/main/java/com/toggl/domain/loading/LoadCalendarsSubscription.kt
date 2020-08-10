package com.toggl.domain.loading

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.CalendarContract
import com.toggl.architecture.DispatcherProvider
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.services.calendar.permissionToReadCalendarWasGranted
import com.toggl.domain.AppState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

private class CalendarContentObserver(
    private val contentResolver: ContentResolver,
    private val calendarsFlow: MutableStateFlow<List<Calendar>>
) : ContentObserver(null) {
    private val calendarProjection = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.ACCOUNT_NAME
    )

    private val calendarIdIndex = 0
    private val calendarDisplayNameIndex = 1
    private val calendarAccountNameIndex = 2

    override fun deliverSelfNotifications(): Boolean = true

    override fun onChange(selfChange: Boolean) {
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            calendarProjection,
            null,
            null,
            null
        )

        calendarsFlow.value = sequence {
            cursor?.use {
                if (it.count <= 0)
                    return@sequence

                while (it.moveToNext()) {
                    val id = it.getString(calendarIdIndex)
                    val displayName = it.getString(calendarDisplayNameIndex)
                    val accountName = it.getString(calendarAccountNameIndex)

                    yield(Calendar(id, displayName, accountName))
                }
            }
        }.toList()
    }
}

@ExperimentalCoroutinesApi
class LoadCalendarsSubscription(
    private val context: Context,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {

    private val calendarsUri: Uri = CalendarContract.Calendars.CONTENT_URI
    override val startLoadingTrigger: (AppState) -> Boolean
        get() = { state -> super.startLoadingTrigger(state) && state.calendarPermissionWasGranted }

    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        if (!shouldStartLoading || !context.permissionToReadCalendarWasGranted())
            return flowOf(emptyList<Calendar>()).map { LoadingAction.CalendarsLoaded(it) }

        val calendarsFlow = MutableStateFlow<List<Calendar>>(emptyList())

        val calendarContentObserver = CalendarContentObserver(context.contentResolver, calendarsFlow)
        context.contentResolver.registerContentObserver(calendarsUri, true, calendarContentObserver)
        calendarContentObserver.onChange(true) // trigger the first load

        return calendarsFlow.map { LoadingAction.CalendarsLoaded(it) }
            .onCompletion { context.contentResolver.unregisterContentObserver(calendarContentObserver) }
    }
}
