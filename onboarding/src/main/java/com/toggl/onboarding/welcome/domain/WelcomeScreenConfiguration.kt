package com.toggl.onboarding.welcome.domain

sealed class WelcomeScreenConfiguration {
    object Regular : WelcomeScreenConfiguration()
    object SsoLink : WelcomeScreenConfiguration()
}
