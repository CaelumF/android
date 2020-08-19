package com.toggl.onboarding.welcome.domain

import com.toggl.architecture.Loadable
import com.toggl.common.feature.navigation.BackStack
import com.toggl.models.domain.User
import com.toggl.onboarding.common.domain.OnboardingState

data class WelcomeState(
    val user: Loadable<User>,
    val backStack: BackStack,
    val configuration: WelcomeScreenConfiguration
) {
    companion object {
        fun fromOnboardingState(onboardingState: OnboardingState) =
            WelcomeState(
                onboardingState.user,
                onboardingState.backStack,
                onboardingState.localState.welcomeScreenConfiguration
            )

        fun toOnboardingState(onboardingState: OnboardingState, welcomeState: WelcomeState) =
            onboardingState.copy(
                user = welcomeState.user,
                backStack = welcomeState.backStack,
                localState = onboardingState.localState.copy(welcomeScreenConfiguration = welcomeState.configuration)
            )
    }
}
