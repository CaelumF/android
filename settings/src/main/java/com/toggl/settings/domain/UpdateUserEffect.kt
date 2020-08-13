package com.toggl.settings.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.core.Effect
import com.toggl.models.domain.User
import com.toggl.repository.interfaces.UserRepository
import kotlinx.coroutines.withContext

class UpdateUserEffect(
    private val newUser: User,
    private val userRepository: UserRepository,
    private val dispatcherProvider: DispatcherProvider
) : Effect<SettingsAction> {

    override suspend fun execute(): SettingsAction = withContext(dispatcherProvider.io) {
        userRepository.update(newUser)
        SettingsAction.UserUpdated(newUser)
    }
}
