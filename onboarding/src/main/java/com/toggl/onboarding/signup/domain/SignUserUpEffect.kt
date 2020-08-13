package com.toggl.onboarding.signup.domain

import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.api.exceptions.EmailIsAlreadyUsedException
import com.toggl.api.exceptions.OfflineException
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Failure
import com.toggl.architecture.core.Effect
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import com.toggl.repository.interfaces.UserRepository
import kotlinx.coroutines.withContext

class SignUserUpEffect(
    private val apiClient: AuthenticationApiClient,
    private val userRepository: UserRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val errorMessages: ErrorMessages,
    private val email: Email.Valid,
    private val password: Password.Strong
) : Effect<SignUpAction> {
    override suspend fun execute(): SignUpAction? = withContext(dispatcherProvider.io) {
        try {
            val user = apiClient.signUp(email, password)
            userRepository.set(user)
            SignUpAction.SetUser(user)
        } catch (exception: Exception) {
            val errorMessage = when (exception) {
                is EmailIsAlreadyUsedException -> errorMessages.emailIsAlreadyUsedError
                is OfflineException -> errorMessages.offline
                else -> errorMessages.genericSignUpError
            }
            SignUpAction.SetUserError(Failure(exception, errorMessage))
        }
    }

    data class ErrorMessages(
        val emailIsAlreadyUsedError: String,
        val genericSignUpError: String,
        val offline: String
    )
}
