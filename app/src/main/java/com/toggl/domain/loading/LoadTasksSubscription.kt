package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.repository.interfaces.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LoadTasksSubscription(
    private val taskRepository: TaskRepository,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val projects = if (shouldStartLoading) taskRepository.loadTasks()
        else flowOf(emptyList())
        return projects.map { LoadingAction.TasksLoaded(it) }
    }
}
