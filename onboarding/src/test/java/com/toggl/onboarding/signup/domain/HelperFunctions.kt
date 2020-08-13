package com.toggl.onboarding.signup.domain

import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import com.toggl.repository.interfaces.UserRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher

private val testDispatcher = TestCoroutineDispatcher()
private val dispatcherProvider = DispatcherProvider(testDispatcher, testDispatcher, Dispatchers.Main)

fun emptySignUpState() = SignUpState(
    Loadable.Uninitialized,
    Email.Invalid(""),
    Password.from(""),
    backStackOf(Route.Welcome, Route.Login)
)

fun createSignUpReducer(
    apiClient: AuthenticationApiClient = mockk(),
    userRepository: UserRepository = mockk(),
    errorMessages: SignUserUpEffect.ErrorMessages = mockk(),
) = SignUpReducer(apiClient, userRepository, errorMessages, dispatcherProvider)
