package com.toggl.timer.suggestions.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.common.extensions.minutesUntil
import com.toggl.common.services.time.TimeService
import com.toggl.timer.di.MaxNumberOfCalendarSuggestions
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class CalendarSuggestionProvider @Inject constructor(
    private val timeService: TimeService,
    private val dispatcherProvider: DispatcherProvider,
    @MaxNumberOfCalendarSuggestions private val maxNumberOfSuggestions: Int
) : SuggestionProvider {
    private val lookBackTimeSpan = Duration.ofHours(1)
    private val lookAheadTimeSpan = Duration.ofHours(1)

    override suspend fun getSuggestions(suggestionData: SuggestionData): List<Suggestion> =
        withContext(dispatcherProvider.io) {
            val now = timeService.now()
            val startOfRange = now - lookBackTimeSpan
            val endOfRange = now + lookAheadTimeSpan

            suggestionData.calendarEvents.values
                .asSequence()
                .filter { it.description.isNotBlank() }
                .filter { it.startTime in startOfRange..endOfRange }
                .sortedBy { abs(now.minutesUntil(it.startTime)) }
                .map { Suggestion.Calendar(it, suggestionData.workspaceId) }
                .take(maxNumberOfSuggestions)
                .toList()
        }
}
