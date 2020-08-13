package com.toggl.repository.interfaces

import com.toggl.models.domain.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun loadUserPreferences(): Flow<UserPreferences>
    suspend fun saveUserPreferences(userPreferences: UserPreferences)
}
