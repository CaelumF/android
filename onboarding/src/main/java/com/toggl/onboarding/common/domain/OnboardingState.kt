package com.toggl.onboarding.common.domain

import com.toggl.architecture.Loadable
import com.toggl.common.feature.navigation.BackStack
import com.toggl.models.domain.User
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import com.toggl.onboarding.welcome.domain.WelcomeScreenConfiguration

data class OnboardingState(
    val user: Loadable<User>,
    val backStack: BackStack,
    val localState: LocalState
) {
    data class LocalState internal constructor(
        internal val email: Email,
        internal val password: Password,
        internal val resetPasswordResult: Loadable<String>,
        internal val welcomeScreenConfiguration: WelcomeScreenConfiguration
    ) {

        constructor() : this(Email.Invalid(""), Password.from(""), Loadable.Uninitialized, WelcomeScreenConfiguration.Regular)

        companion object
    }

    companion object
}
