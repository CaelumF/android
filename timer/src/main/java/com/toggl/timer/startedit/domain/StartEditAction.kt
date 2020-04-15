package com.toggl.timer.startedit.domain

import com.toggl.models.common.AutocompleteSuggestion
import com.toggl.models.domain.TimeEntry
import com.toggl.timer.common.domain.TimerAction

sealed class StartEditAction {
    object BillableTapped : StartEditAction()
    object CloseButtonTapped : StartEditAction()
    object DialogDismissed : StartEditAction()
    object DoneButtonTapped : StartEditAction()
    data class DescriptionEntered(val description: String) : StartEditAction()
    data class TimeEntryUpdated(val id: Long, val timeEntry: TimeEntry) : StartEditAction()
    data class TimeEntryStarted(val startedTimeEntry: TimeEntry, val stoppedTimeEntry: TimeEntry?) : StartEditAction()
    data class AutocompleteSuggestionsUpdated(val autocompleteSuggestions: List<AutocompleteSuggestion>) : StartEditAction()

    companion object {
        fun fromTimerAction(timerAction: TimerAction): StartEditAction? =
            if (timerAction !is TimerAction.StartTimeEntry) null
            else timerAction.startEditAction

        fun toTimerAction(startEditAction: StartEditAction): TimerAction =
            TimerAction.StartTimeEntry(
                startEditAction
            )
    }
}

fun StartEditAction.formatForDebug() =
    when (this) {
        StartEditAction.CloseButtonTapped -> "Close button tapped"
        StartEditAction.DialogDismissed -> "Dialog dismissed"
        StartEditAction.DoneButtonTapped -> "Done button tapped"
        is StartEditAction.DescriptionEntered -> "Description changed to $description"
        is StartEditAction.TimeEntryUpdated -> "Time entry with id $id updated"
        is StartEditAction.TimeEntryStarted -> "Time entry started with id $startedTimeEntry.id"
        StartEditAction.BillableTapped -> "Billable toggled in the running time entry"
        is StartEditAction.AutocompleteSuggestionsUpdated -> "AutocompleteSuggestions updated with $autocompleteSuggestions"
    }
