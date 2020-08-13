package com.toggl.calendar.contextualmenu.domain

import com.toggl.architecture.core.Selector
import com.toggl.calendar.common.domain.endTime
import com.toggl.calendar.common.domain.startTime
import com.toggl.common.feature.domain.ProjectViewModel
import com.toggl.common.feature.models.SelectedCalendarItem
import com.toggl.common.feature.timeentry.extensions.isRunning
import com.toggl.common.feature.timeentry.extensions.isStopped
import com.toggl.common.feature.timeentry.extensions.wasNotYetPersisted
import dagger.hilt.android.scopes.FragmentScoped
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@FragmentScoped
class ContextualMenuDisplaySelector @Inject constructor() : Selector<ContextualMenuState, ContextualMenuViewModel> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override suspend fun select(state: ContextualMenuState): ContextualMenuViewModel {
        return when (state.selectedItem) {
            is SelectedCalendarItem.SelectedTimeEntry -> state.createTimeEntryContextualMenu(state.selectedItem)
            is SelectedCalendarItem.SelectedCalendarEvent -> state.createCalendarEventContextualMenu(state.selectedItem)
        }
    }

    private fun ContextualMenuState.createTimeEntryContextualMenu(selectedItem: SelectedCalendarItem.SelectedTimeEntry): ContextualMenuViewModel.TimeEntryContextualMenu {
        val period = formatPeriod(selectedItem)
        val timeEntry = selectedItem.editableTimeEntry
        val contextualMenuActions = selectedItem.contextualMenuActions
        return ContextualMenuViewModel.TimeEntryContextualMenu(
            description = timeEntry.description,
            periodLabel = period,
            projectViewModel = getProjectViewModel(timeEntry.projectId),
            contextualMenuActions = contextualMenuActions
        )
    }

    private fun ContextualMenuState.createCalendarEventContextualMenu(selectedItem: SelectedCalendarItem.SelectedCalendarEvent): ContextualMenuViewModel.CalendarEventContextualMenu {
        val period = formatPeriod(selectedItem)
        val calendarEvent = selectedItem.calendarEvent
        return ContextualMenuViewModel.CalendarEventContextualMenu(
            description = calendarEvent.description,
            periodLabel = period,
            calendarColor = calendarEvent.color,
            calendarName = calendarEvent.calendarName
        )
    }

    private fun ContextualMenuState.getProjectViewModel(projectId: Long?): ProjectViewModel? =
        projects[projectId]?.let {
            ProjectViewModel(
                id = it.id,
                name = it.name,
                color = it.color,
                clientName = clients[it.clientId]?.name
            )
        }

    private fun formatPeriod(selectedItem: SelectedCalendarItem): String {
        val startTime = formatter.format(selectedItem.startTime)
        val endTime = formatter.format(selectedItem.endTime ?: OffsetDateTime.now())
        return "$startTime - $endTime"
    }

    private val SelectedCalendarItem.SelectedTimeEntry.contextualMenuActions: ContextualMenuActionsViewModel
        get() = when {
            this.editableTimeEntry.wasNotYetPersisted() -> ContextualMenuActionsViewModel.NewTimeEntryActions
            this.editableTimeEntry.isRunning() -> ContextualMenuActionsViewModel.RunningTimeEntryActions
            this.editableTimeEntry.isStopped() -> ContextualMenuActionsViewModel.StoppedTimeEntryActions
            else -> throw IllegalStateException("Invalid calendar item type")
        }
}
