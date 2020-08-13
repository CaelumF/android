package com.toggl.onboarding.signup.domain

import com.toggl.architecture.Failure
import com.toggl.models.domain.User

sealed class SignUpAction {
    object SignUpButtonTapped : SignUpAction()
    object GoToLoginTapped : SignUpAction()
    data class EmailEntered(val email: String) : SignUpAction()
    data class PasswordEntered(val password: String) : SignUpAction()
    data class SetUser(val user: User) : SignUpAction()
    data class SetUserError(val failure: Failure) : SignUpAction()

    companion object
}

fun SignUpAction.formatForDebug() =
    when (this) {
        SignUpAction.SignUpButtonTapped -> "Sign Up button tapped"
        is SignUpAction.EmailEntered -> "Email entered $email"
        is SignUpAction.PasswordEntered -> "Password entered $password"
        SignUpAction.GoToLoginTapped -> "Login button tapped"
        is SignUpAction.SetUser -> "Setting user $user"
        is SignUpAction.SetUserError -> "Setting user error ${failure.errorMessage}"
    }
