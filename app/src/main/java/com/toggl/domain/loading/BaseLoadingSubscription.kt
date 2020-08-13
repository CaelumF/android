package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Subscription
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class BaseLoadingSubscription(private val dispatcherProvider: DispatcherProvider) : Subscription<AppState, AppAction> {
    open val startLoadingTrigger: (AppState) -> Boolean = { state -> state.user is Loadable.Loaded }

    override fun subscribe(state: Flow<AppState>): Flow<AppAction.Loading> =
        state.map { startLoadingTrigger(it) }
            .distinctUntilChanged()
            .flatMapLatest { shouldStartLoading ->
                withContext(dispatcherProvider.io) {
                    subscribe(shouldStartLoading).map { AppAction.Loading(it) }
                }
            }

    abstract fun subscribe(shouldStartLoading: Boolean): Flow<LoadingAction>
}
