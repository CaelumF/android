package com.toggl.calendar.di

import com.toggl.architecture.core.Reducer
import com.toggl.architecture.core.Store
import com.toggl.architecture.core.combine
import com.toggl.architecture.core.decorateWith
import com.toggl.architecture.core.optionalPullback
import com.toggl.architecture.core.pullback
import com.toggl.architecture.core.unwrap
import com.toggl.calendar.calendarday.domain.CalendarDayAction
import com.toggl.calendar.calendarday.domain.CalendarDayReducer
import com.toggl.calendar.calendarday.domain.CalendarDayState
import com.toggl.calendar.common.domain.CalendarAction
import com.toggl.calendar.common.domain.CalendarState
import com.toggl.calendar.common.domain.setSelectedItemToNull
import com.toggl.calendar.contextualmenu.domain.ContextualMenuAction
import com.toggl.calendar.contextualmenu.domain.ContextualMenuReducer
import com.toggl.calendar.contextualmenu.domain.ContextualMenuState
import com.toggl.calendar.datepicker.domain.CalendarDatePickerAction
import com.toggl.calendar.datepicker.domain.CalendarDatePickerReducer
import com.toggl.calendar.datepicker.domain.CalendarDatePickerState
import com.toggl.common.feature.handleClosableActionsUsing
import com.toggl.common.feature.timeentry.TimeEntryAction
import com.toggl.common.feature.timeentry.TimeEntryReducer
import com.toggl.common.feature.timeentry.TimeEntryState
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

typealias CalendarReducer = Reducer<CalendarState, CalendarAction>

@Module(subcomponents = [CalendarComponent::class])
class CalendarModule {

    @ExperimentalCoroutinesApi
    @Provides
    internal fun calendarDayStore(store: Store<CalendarState, CalendarAction>): Store<CalendarDayState, CalendarDayAction> =
        store.view(
            mapToLocalState = CalendarDayState.Companion::fromCalendarState,
            mapToGlobalAction = CalendarAction::CalendarDay
        )

    @ExperimentalCoroutinesApi
    @Provides
    internal fun datePickerStore(store: Store<CalendarState, CalendarAction>): Store<CalendarDatePickerState, CalendarDatePickerAction> =
        store.view(
            mapToLocalState = CalendarDatePickerState.Companion::fromCalendarState,
            mapToGlobalAction = CalendarAction::DatePicker
        )

    @ExperimentalCoroutinesApi
    @Provides
    internal fun contextualMenuStore(store: Store<CalendarState, CalendarAction>): Store<ContextualMenuState, ContextualMenuAction> =
        store.optionalView(
            mapToLocalState = ContextualMenuState.Companion::fromCalendarState,
            mapToGlobalAction = CalendarAction::ContextualMenu
        )

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Provides
    @Singleton
    internal fun calendarReducer(
        timeEntryReducer: TimeEntryReducer,
        calendarDayReducer: CalendarDayReducer,
        datePickerReducer: CalendarDatePickerReducer,
        contextualMenuReducer: ContextualMenuReducer
    ): CalendarReducer {

        return combine<CalendarState, CalendarAction>(
            calendarDayReducer.pullback(
                mapToLocalState = CalendarDayState.Companion::fromCalendarState,
                mapToLocalAction = CalendarAction::unwrap,
                mapToGlobalState = CalendarDayState.Companion::toCalendarState,
                mapToGlobalAction = CalendarAction::CalendarDay
            ),
            datePickerReducer.pullback(
                mapToLocalState = CalendarDatePickerState.Companion::fromCalendarState,
                mapToLocalAction = CalendarAction::unwrap,
                mapToGlobalState = CalendarDatePickerState.Companion::toCalendarState,
                mapToGlobalAction = CalendarAction::DatePicker
            ),
            contextualMenuReducer.optionalPullback(
                mapToLocalState = ContextualMenuState.Companion::fromCalendarState,
                mapToLocalAction = CalendarAction::unwrap,
                mapToGlobalState = ContextualMenuState.Companion::toCalendarState,
                mapToGlobalAction = CalendarAction::ContextualMenu
            )
        )
        .handleClosableActionsUsing(CalendarState::setSelectedItemToNull, ContextualMenuAction.Close::class.java)
        .decorateWith(timeEntryReducer)

    }

    private fun CalendarReducer.decorateWith(timeEntryReducer: TimeEntryReducer) =
        decorateWith(timeEntryReducer,
            mapToLocalState = { TimeEntryState(it.timeEntries) },
            mapToLocalAction = { CalendarAction.unwrapTimeEntryActionHolder(it) },
            mapToGlobalState = { globalState, localState -> globalState.copy(timeEntries = localState.timeEntries) },
            mapToGlobalAction = { CalendarAction.ContextualMenu(ContextualMenuAction.TimeEntryHandling(it)) }
        )
}