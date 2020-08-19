package com.toggl.onboarding.welcome.domain

sealed class WelcomeAction {
    object ContinueWithEmailButtonTapped : WelcomeAction()
    object ContinueWithGoogleButtonTapped : WelcomeAction()
    object LoginWithSsoButtonTapped : WelcomeAction()
    object CancelButtonTapped : WelcomeAction()

    companion object
}

fun WelcomeAction.formatForDebug() =
    when (this) {
        WelcomeAction.ContinueWithEmailButtonTapped -> "Continue with email button tapped"
        WelcomeAction.ContinueWithGoogleButtonTapped -> "Continue with google button tapped"
        WelcomeAction.LoginWithSsoButtonTapped -> "Continue with sso button tapped"
        WelcomeAction.CancelButtonTapped -> "Cancel button tapped"
    }
