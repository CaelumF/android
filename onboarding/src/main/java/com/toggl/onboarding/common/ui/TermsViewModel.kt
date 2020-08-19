package com.toggl.onboarding.common.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.toggl.architecture.core.Store
import com.toggl.onboarding.common.domain.OnboardingAction
import com.toggl.onboarding.common.domain.OnboardingState

class TermsViewModel @ViewModelInject constructor(
    store: Store<OnboardingState, OnboardingAction>
) : ViewModel(), Store<OnboardingState, OnboardingAction> by store
