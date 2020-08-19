package com.toggl.onboarding.welcome.domain

import com.toggl.onboarding.common.CoroutineTest
import com.toggl.onboarding.common.testReduceNoEffects
import com.toggl.onboarding.common.testReduceState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The CancelButtonTapped action")
class CancelButtonTappedActionTests : CoroutineTest() {
    val reducer = createWelcomeReducer()
    val initialState = emptyWelcomeState()

    @Test
    fun `sets the welcome screen configuration to regular`() = runBlocking {
        val ssoState = initialState.copy(configuration = WelcomeScreenConfiguration.SsoLink)

        reducer.testReduceState(
            ssoState,
            WelcomeAction.CancelButtonTapped
        ) {
            it.configuration shouldBe WelcomeScreenConfiguration.Regular
        }
    }

    @Test
    fun `produces no effects`() = runBlocking {
        reducer.testReduceNoEffects(
            initialState,
            WelcomeAction.CancelButtonTapped
        )
    }
}
