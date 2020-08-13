package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.models.domain.UserPreferences
import com.toggl.repository.interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LoadUserPreferencesSubscription(
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) : BaseLoadingSubscription(dispatcherProvider) {
    override fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction> {
        val userPreferences = if (shouldStartLoading) settingsRepository.loadUserPreferences()
        else flowOf(UserPreferences.default)
        return userPreferences.map { LoadingAction.UserPreferencesLoaded(it) }
    }
}
