package com.toggl.onboarding.signup.domain

import com.toggl.architecture.Loadable
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.onboarding.common.CoroutineTest
import com.toggl.onboarding.common.testReduceState
import com.toggl.onboarding.common.validUser
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The set user action")
class SetUserActionTests : CoroutineTest() {
    private val reducer = createSignUpReducer()

    @Test
    fun `sets the user from the state and the route to the timer page`() = runBlockingTest {
        val initialState = emptySignUpState()

        reducer.testReduceState(initialState, SignUpAction.SetUser(validUser)) { newState ->
            newState shouldBe initialState.copy(
                backStack = backStackOf(Route.Timer),
                user = Loadable.Loaded(validUser)
            )
        }
    }
}
