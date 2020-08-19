package com.toggl.onboarding.welcome.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.toggl.common.extensions.adjustMarginToNavigationBarInsets
import com.toggl.onboarding.R
import com.toggl.onboarding.welcome.domain.WelcomeAction
import com.toggl.onboarding.welcome.domain.WelcomeScreenConfiguration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    private val store: WelcomeStoreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager.adapter = WelcomeCarouselAdapter(requireContext())
        tab_indicator.setupWithViewPager(view_pager)

        continue_with_sso.adjustMarginToNavigationBarInsets()
        sso_cancel.adjustMarginToNavigationBarInsets()

        continue_with_email.setOnClickListener {
            store.dispatch(WelcomeAction.ContinueWithEmailButtonTapped)
        }

        continue_with_sso.setOnClickListener {
            store.dispatch(WelcomeAction.LoginWithSsoButtonTapped)
        }

        continue_with_google.setOnClickListener {
            store.dispatch(WelcomeAction.ContinueWithGoogleButtonTapped)
        }

        sso_cancel.setOnClickListener {
            store.dispatch(WelcomeAction.CancelButtonTapped)
        }

        store.state
            .map { it.configuration }
            .distinctUntilChanged()
            .onEach { config -> configureAccordingToSsoLinkState(config) }
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        view_pager.adapter = null
        super.onDestroyView()
    }

    private fun configureAccordingToSsoLinkState(targetConfiguration: WelcomeScreenConfiguration) {
        not_loading_views_view_group.isVisible = targetConfiguration == WelcomeScreenConfiguration.Regular
        sso_not_loading_views_view_group.isVisible = targetConfiguration == WelcomeScreenConfiguration.SsoLink
    }
}
