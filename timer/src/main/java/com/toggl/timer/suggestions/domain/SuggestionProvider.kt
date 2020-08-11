package com.toggl.timer.suggestions.domain

import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry

interface SuggestionProvider {
    suspend fun getSuggestions(suggestionData: SuggestionData): List<Suggestion>
}

data class SuggestionData(
    val workspaceId: Long,
    val timeEntries: Map<Long, TimeEntry>,
    val projects: Map<Long, Project>,
    val calendarEvents: Map<String, CalendarEvent>
)

internal class ComposeSuggestionProvider(
    private val maxNumberOfSuggestions: Int,
    vararg providers: SuggestionProvider
) : SuggestionProvider {

    private val providers = providers.toList()

    override suspend fun getSuggestions(suggestionData: SuggestionData): List<Suggestion> =
        providers.flatMap { it.getSuggestions(suggestionData) }.take(maxNumberOfSuggestions)
}
