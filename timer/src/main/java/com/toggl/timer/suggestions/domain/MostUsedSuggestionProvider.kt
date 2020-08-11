package com.toggl.timer.suggestions.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.common.extensions.minutesUntil
import com.toggl.common.services.time.TimeService
import com.toggl.models.domain.Project
import com.toggl.models.domain.TimeEntry
import com.toggl.timer.di.MaxNumberOfMostUsedSuggestions
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MostUsedSuggestionProvider @Inject constructor(
    private val timeService: TimeService,
    private val dispatcherProvider: DispatcherProvider,
    @MaxNumberOfMostUsedSuggestions private val maxNumberOfSuggestions: Int
) : SuggestionProvider {
    private val daysToQuery = 42L
    private val thresholdPeriodInMinutes = Duration.ofDays(daysToQuery).toMinutes()

    override suspend fun getSuggestions(suggestionData: SuggestionData): List<Suggestion> = withContext(dispatcherProvider.io) {
        suggestionData.timeEntries.values
            .filter { isSuitableForSuggestion(it, suggestionData.projects) }
            .let(::toMostUsedSuggestion)
            .take(maxNumberOfSuggestions)
    }

    private fun isSuitableForSuggestion(timeEntry: TimeEntry, projects: Map<Long, Project>): Boolean {
        val hasDescription = timeEntry.description.isNotBlank()
        val hasProject = timeEntry.projectId?.let { projects[it]?.name?.isNotBlank() } ?: false
        val isRecent = timeEntry.startTime.minutesUntil(timeService.now()) <= thresholdPeriodInMinutes
        val isActive = isTimeEntryActive(timeEntry, projects)

        return isRecent && isActive && (hasDescription || hasProject)
    }

    private fun isTimeEntryActive(timeEntry: TimeEntry, projects: Map<Long, Project>): Boolean =
        !timeEntry.isDeleted &&
            timeEntry.projectId?.let { projects[it]?.active } ?: true

    private fun toMostUsedSuggestion(timeEntries: Collection<TimeEntry>) =
        timeEntries
            .groupBy { TimeEntrySuggestionInfo(it.description, it.projectId, it.taskId) }
            .values.sortedByDescending { it.size }
            .map { it.first() }
            .map { Suggestion.MostUsed(it) }
            .toList()

    private data class TimeEntrySuggestionInfo(val description: String, val projectId: Long?, val taskId: Long?)
}
