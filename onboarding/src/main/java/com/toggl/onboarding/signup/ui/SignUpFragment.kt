package com.toggl.onboarding.signup.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.toggl.architecture.Loadable
import com.toggl.architecture.errorMessageOrEmptyString
import com.toggl.common.extensions.doSafeAfterTextChanged
import com.toggl.common.extensions.setSafeText
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import com.toggl.onboarding.R
import com.toggl.onboarding.signup.domain.SignUpAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {
    private val store: SignUpStoreViewModel by viewModels()

    private lateinit var emailWatcher: TextWatcher
    private lateinit var passwordWatcher: TextWatcher

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailWatcher = email.doSafeAfterTextChanged { store.dispatch(SignUpAction.EmailEntered(it.toString())) }
        passwordWatcher = password.doSafeAfterTextChanged { store.dispatch(SignUpAction.PasswordEntered(it.toString())) }

        login_button.setOnClickListener {
            store.dispatch(SignUpAction.GoToLoginTapped)
        }

        val defaultCriteriaColor = ContextCompat.getColor(requireContext(), R.color.authentication_text)
        val errorCriteriaColor = ContextCompat.getColor(requireContext(), R.color.error)

        store.state
            .map { it.password is Password.Strong }
            .filter { it }
            .onEach { password_strength_criteria.setTextColor(defaultCriteriaColor) }
            .launchIn(lifecycleScope)

        store.state
            .map { it.user }
            .onEach {
                loading_indicator.isVisible = it is Loadable.Loading
                sign_up_button.isEnabled = it !is Loadable.Loading && it !is Loadable.Loaded

                val errorMessage = it.errorMessageOrEmptyString()
                error_label.apply {
                    text = errorMessage
                    isVisible = errorMessage.isNotEmpty()
                }
            }
            .launchIn(lifecycleScope)

        sign_up_button.setOnClickListener {
            if (!password.text.representsStrongPassword())
                password_strength_criteria.setTextColor(errorCriteriaColor)

            with(email.text) {
                if (isNullOrBlank())
                    email_label.error = getString(R.string.no_email_error)
                else if (!representsValidEmail())
                    email_label.error = getString(R.string.invalid_email_error)
                else
                    email_label.error = ""
            }

            store.dispatch(SignUpAction.SignUpButtonTapped)
        }
    }

    override fun onResume() {
        super.onResume()

        store.state
            .map { it.email }
            .onEach { email?.setSafeText(it.toString()) }
            .launchIn(lifecycleScope)

        store.state
            .map { it.password }
            .onEach { password?.setSafeText(it.toString()) }
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        email?.removeTextChangedListener(emailWatcher)
        password?.removeTextChangedListener(passwordWatcher)

        super.onDestroyView()
    }

    private fun Editable?.representsStrongPassword() =
        if (this == null) false else Password.from(this.toString()) is Password.Strong

    private fun Editable?.representsValidEmail() =
        if (this == null) false else Email.from(this.toString()) is Email.Valid
}
