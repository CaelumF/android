package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Subscription
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.models.domain.User
import com.toggl.timer.suggestions.domain.SuggestionData
import com.toggl.timer.suggestions.domain.SuggestionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LoadSuggestionsSubscription(
    private val suggestionProvider: SuggestionProvider,
    private val dispatcherProvider: DispatcherProvider
) : Subscription<AppState, AppAction> {
    override fun subscribe(state: Flow<AppState>): Flow<AppAction.Loading> =
        state.map { newState -> newState.toSuggestionSources() }
            .distinctUntilChanged()
            .map { suggestionSource ->
                suggestionSource?.let {
                    withContext(dispatcherProvider.io) {
                        return@withContext suggestionProvider.getSuggestions(it)
                    }
                } ?: emptyList()
            }
            .distinctUntilChanged()
            .map { suggestions -> AppAction.Loading(LoadingAction.SuggestionsLoaded(suggestions)) }

    private fun AppState.toSuggestionSources(): SuggestionData? =
        if (user is Loadable.Loaded<User>) {
            SuggestionData(
                user.value.defaultWorkspaceId,
                timeEntries,
                projects,
                calendarEvents
            )
        } else null
}
