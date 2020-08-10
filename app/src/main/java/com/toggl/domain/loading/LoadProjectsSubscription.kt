package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.repository.interfaces.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LoadProjectsSubscription(
    private val projectRepository: ProjectRepository,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val projects = if (shouldStartLoading) projectRepository.loadProjects()
        else flowOf(emptyList())
        return projects.map { LoadingAction.ProjectsLoaded(it) }
    }
}
