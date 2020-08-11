package com.toggl.timer.suggestions.domain

import com.toggl.common.feature.timeentry.TimeEntryAction
import com.toggl.common.feature.timeentry.TimeEntryActionHolder

sealed class SuggestionsAction {
    data class SuggestionTapped(val suggestion: Suggestion) : SuggestionsAction()
    data class TimeEntryHandling(override val timeEntryAction: TimeEntryAction) : SuggestionsAction(), TimeEntryActionHolder
}

fun SuggestionsAction.formatForDebug() =
    when (this) {
        is SuggestionsAction.SuggestionTapped -> "Selected suggestion $suggestion"
        is SuggestionsAction.TimeEntryHandling -> "Time entry action $timeEntryAction"
    }
