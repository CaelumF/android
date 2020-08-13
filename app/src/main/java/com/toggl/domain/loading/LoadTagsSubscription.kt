package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.repository.interfaces.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LoadTagsSubscription(
    private val tagRepository: TagRepository,
    dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val projects = if (shouldStartLoading) tagRepository.loadTags()
        else flowOf(emptyList())
        return projects.map { LoadingAction.TagsLoaded(it) }
    }
}
