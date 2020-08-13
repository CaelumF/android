package com.toggl.onboarding.signup.domain

import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.extensions.returnEffect
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import com.toggl.models.validation.toEmail
import com.toggl.models.validation.toPassword
import com.toggl.repository.interfaces.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpReducer @Inject constructor(
    private val apiClient: AuthenticationApiClient,
    private val userRepository: UserRepository,
    private val errorMessages: SignUserUpEffect.ErrorMessages,
    private val dispatcherProvider: DispatcherProvider
) : Reducer<SignUpState, SignUpAction> {

    override fun reduce(
        state: MutableValue<SignUpState>,
        action: SignUpAction
    ): List<Effect<SignUpAction>> =
        when (action) {
            SignUpAction.SignUpButtonTapped -> {
                val currentState = state()
                if (currentState.email is Email.Valid && currentState.password is Password.Strong &&
                    currentState.user !is Loadable.Loading && currentState.user !is Loadable.Loaded
                ) {
                    state.mutate { copy(user = Loadable.Loading) } returnEffect signUserUpEffect(
                        currentState.email,
                        currentState.password
                    )
                } else {
                    noEffect()
                }
            }
            is SignUpAction.EmailEntered -> state.mutateWithoutEffects { copy(email = action.email.toEmail()) }
            is SignUpAction.PasswordEntered -> state.mutateWithoutEffects { copy(password = action.password.toPassword()) }
            SignUpAction.GoToLoginTapped -> state.mutateWithoutEffects {
                copy(backStack = backStackOf(Route.Welcome, Route.Login))
            }
            is SignUpAction.SetUser -> state.mutateWithoutEffects {
                copy(
                    user = Loadable.Loaded(action.user),
                    backStack = backStackOf(Route.Timer)
                )
            }
            is SignUpAction.SetUserError -> state.mutateWithoutEffects {
                copy(user = Loadable.Error(action.failure))
            }
        }

    private fun signUserUpEffect(email: Email.Valid, password: Password.Strong) = effect(
        SignUserUpEffect(
            apiClient,
            userRepository,
            dispatcherProvider,
            errorMessages,
            email,
            password
        )
    )
}
