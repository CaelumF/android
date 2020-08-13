package com.toggl.domain.loading

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.provider.CalendarContract
import com.toggl.architecture.DispatcherProvider
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.domain.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

class LoadCalendarsSubscription(
    private val calendarProvider: CalendarProvider,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {

    override val startLoadingTrigger: (AppState) -> Boolean
        get() = { state -> super.startLoadingTrigger(state) && state.calendarPermissionWasGranted }

    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        if (!shouldStartLoading)
            return flowOf(emptyList<Calendar>()).map { LoadingAction.CalendarsLoaded(it) }

        calendarProvider.unregister()
        return calendarProvider.calendarFlow()
            .map { LoadingAction.CalendarsLoaded(it) }
            .onCompletion { calendarProvider.unregister() }
    }
}

data class CalendarProvider(private val contentResolver: ContentResolver) {

    private val calendarsUri: Uri = CalendarContract.Calendars.CONTENT_URI
    private var calendarContentObserver: CalendarContentObserver? = null

    fun calendarFlow(): Flow<List<Calendar>> {
        val calendarsFlow = MutableStateFlow<List<Calendar>>(emptyList())
        calendarContentObserver = CalendarContentObserver(contentResolver, calendarsFlow).also { observer ->
            contentResolver.registerContentObserver(calendarsUri, true, observer)
            observer.onChange(true)
        }
        return calendarsFlow
    }

    fun unregister() {
        calendarContentObserver?.let { observer ->
            contentResolver.unregisterContentObserver(observer)
        }
        calendarContentObserver = null
    }
}

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
