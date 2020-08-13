package com.toggl.onboarding.signup.domain

import com.toggl.architecture.Loadable
import com.toggl.onboarding.common.CoroutineTest
import com.toggl.onboarding.common.strongPassword
import com.toggl.onboarding.common.testReduce
import com.toggl.onboarding.common.testReduceNoOp
import com.toggl.onboarding.common.validEmail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The SignUpButtonTappedAction")
class SignUpButtonTappedActionTests : CoroutineTest() {
    private val reducer = createSignUpReducer()

    @Test
    fun `for a valid email and a strong password, sets the user to loading and returns the sign up effect`() = runBlocking {
        val initialState = emptySignUpState().copy(email = validEmail, password = strongPassword)

        reducer.testReduce(initialState, SignUpAction.SignUpButtonTapped) { newState, effects ->
            newState.user shouldBe Loadable.Loading
            effects.first().shouldBeTypeOf<SignUserUpEffect>()
            effects.size shouldBe 1
        }
    }

    @Test
    fun `for an invalid email, does nothing`() = runBlocking {
        val initialState = emptySignUpState().copy(password = strongPassword)

        reducer.testReduceNoOp(initialState, SignUpAction.SignUpButtonTapped)
    }

    @Test
    fun `for a weak password, does nothing`() = runBlocking {
        val initialState = emptySignUpState().copy(email = validEmail)

        reducer.testReduceNoOp(initialState, SignUpAction.SignUpButtonTapped)
    }

    @Test
    fun `for a loading user, does nothing`() = runBlocking {
        val initialState = emptySignUpState().copy(email = validEmail, password = strongPassword, user = Loadable.Loading)

        reducer.testReduceNoOp(initialState, SignUpAction.SignUpButtonTapped)
    }

    @Test
    fun `for a loaded user, does nothing`() = runBlocking {
        val initialState = emptySignUpState().copy(email = validEmail, password = strongPassword, user = Loadable.Loaded(mockk()))

        reducer.testReduceNoOp(initialState, SignUpAction.SignUpButtonTapped)
    }
}
