package com.toggl.timer.suggestions.domain

import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effectOf
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.feature.services.calendar.toEditableTimeEntry
import com.toggl.common.feature.timeentry.TimeEntryAction
import com.toggl.common.services.time.TimeService
import com.toggl.repository.extensions.toStartDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestionsReducer @Inject constructor(
    private val timeService: TimeService,
) : Reducer<SuggestionsState, SuggestionsAction> {

    override fun reduce(
        state: MutableValue<SuggestionsState>,
        action: SuggestionsAction
    ): List<Effect<SuggestionsAction>> =
        when (action) {
            is SuggestionsAction.TimeEntryHandling -> noEffect()
            is SuggestionsAction.SuggestionTapped -> effectOf(
                SuggestionsAction.TimeEntryHandling(
                    when (val suggestion = action.suggestion) {
                        is Suggestion.MostUsed -> TimeEntryAction.ContinueTimeEntry(suggestion.timeEntry.id)
                        is Suggestion.Calendar -> TimeEntryAction.StartTimeEntry(
                            suggestion.calendarEvent.toEditableTimeEntry(suggestion.workspaceId).toStartDto(timeService.now())
                        )
                    }
                )
            )
        }
}
