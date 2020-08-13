package com.toggl.onboarding.login.domain

import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.onboarding.common.CoroutineTest
import com.toggl.onboarding.common.testReduceState
import com.toggl.repository.interfaces.UserRepository
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The ForgotPasswordTapped action")
class ForgotPasswordTappedActionTests : CoroutineTest() {
    private val errorMessages = LogUserInEffect.ErrorMessages("", "", "", "", "")
    private val authenticationApi: AuthenticationApiClient = mockk()
    private val userRepository: UserRepository = mockk()
    private val reducer =
        LoginReducer(authenticationApi, userRepository, errorMessages, dispatcherProvider)

    @Test
    fun `navigates to the password reset view`() = runBlocking {
        reducer.testReduceState(emptyLoginState(), LoginAction.ForgotPasswordTapped) { newState ->
            newState.backStack shouldBe backStackOf(Route.Welcome, Route.Login, Route.PasswordReset)
        }
    }
}
