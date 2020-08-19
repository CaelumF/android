package com.toggl.onboarding.welcome.domain

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher

private val testDispatcher = TestCoroutineDispatcher()
private val dispatcherProvider = DispatcherProvider(testDispatcher, testDispatcher, Dispatchers.Main)

fun emptyWelcomeState() = WelcomeState(
    Loadable.Uninitialized,
    emptyList(),
    WelcomeScreenConfiguration.Regular
)

fun createWelcomeReducer(
) = WelcomeReducer()
