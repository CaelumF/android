package com.toggl.onboarding.signup.domain

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.onboarding.common.CoroutineTest
import com.toggl.onboarding.common.testReduceState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The set user error action")
class SetUserErrorActionTests : CoroutineTest() {
    private val reducer = createSignUpReducer()
    private val failure = Failure(IllegalAccessException(), "")

    @Test
    fun `sets the user error`() = runBlockingTest {
        val initialState = emptySignUpState()

        reducer.testReduceState(initialState, SignUpAction.SetUserError(failure)) { newState ->
            newState shouldBe initialState.copy(
                user = Loadable.Error(failure)
            )
        }
    }
}
