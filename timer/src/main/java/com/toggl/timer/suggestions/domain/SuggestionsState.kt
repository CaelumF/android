package com.toggl.timer.suggestions.domain

import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.timer.common.domain.TimerState

data class SuggestionsState(
    val suggestions: List<Suggestion>,
    val timeEntries: Map<Long, TimeEntry>,
    val projects: Map<Long, Project>,
    val clients: Map<Long, Client>
) {
    companion object {
        fun fromTimerState(timerState: TimerState): SuggestionsState? {
            return SuggestionsState(
                suggestions = timerState.suggestions,
                timeEntries = timerState.timeEntries,
                projects = timerState.projects,
                clients = timerState.clients
            )
        }

        fun toTimerState(timerState: TimerState, suggestionsState: SuggestionsState?) =
            suggestionsState?.let {
                timerState.copy(
                    suggestions = suggestionsState.suggestions,
                    timeEntries = suggestionsState.timeEntries,
                    projects = suggestionsState.projects,
                    clients = suggestionsState.clients
                )
            } ?: timerState
    }
}
