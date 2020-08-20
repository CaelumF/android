package com.toggl.onboarding.sso.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toggl.onboarding.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SsoLinkFragment : Fragment(R.layout.fragment_ssolink) {
    private val store: SsoStoreViewModel by viewModels()
}
