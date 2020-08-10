package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.repository.interfaces.ClientRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class LoadClientsSubscription(
    private val taskRepository: ClientRepository,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val projects = if (shouldStartLoading) taskRepository.loadClients()
        else flowOf(emptyList())
        return projects.map { LoadingAction.ClientsLoaded(it) }
    }
}
