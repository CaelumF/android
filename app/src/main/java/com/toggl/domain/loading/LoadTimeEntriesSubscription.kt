package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.repository.interfaces.TimeEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LoadTimeEntriesSubscription(
    private val timeEntryRepository: TimeEntryRepository,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val timeEntries = if (shouldStartLoading) timeEntryRepository.loadTimeEntries()
        else flowOf(emptyList())
        return timeEntries.map { LoadingAction.TimeEntriesLoaded(it) }
    }
}
