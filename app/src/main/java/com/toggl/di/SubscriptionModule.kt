package com.toggl.di

import android.content.Context
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.core.CompositeSubscription
import com.toggl.architecture.core.Subscription
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.domain.loading.LoadCalendarsSubscription
import com.toggl.domain.loading.LoadClientsSubscription
import com.toggl.domain.loading.LoadProjectsSubscription
import com.toggl.domain.loading.LoadSuggestionsSubscription
import com.toggl.domain.loading.LoadTagsSubscription
import com.toggl.domain.loading.LoadTasksSubscription
import com.toggl.domain.loading.LoadTimeEntriesSubscription
import com.toggl.domain.loading.LoadUserPreferencesSubscription
import com.toggl.domain.loading.LoadWorkspacesSubscription
import com.toggl.repository.Repository
import com.toggl.timer.suggestions.domain.SuggestionProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoSet

@Module
@InstallIn(ApplicationComponent::class)
object SubscriptionModule {

    @Provides
    fun compositeSubscription(
        subscriptions: Set<@JvmSuppressWildcards Subscription<AppState, AppAction>>
    ): CompositeSubscription<AppState, AppAction> =
        CompositeSubscription(subscriptions)

    @Provides
    @IntoSet
    fun loadTimeEntriesSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadTimeEntriesSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadProjectSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadProjectsSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadClientSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadClientsSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadTaskSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadTasksSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadTagSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadTagsSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadWorkspaceSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadWorkspacesSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadUserPreferencesSubscription(
        repository: Repository,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadUserPreferencesSubscription(repository, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadCalendarsSubscription(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadCalendarsSubscription(context, dispatcherProvider)

    @Provides
    @IntoSet
    fun loadSuggestionsSubscription(
        suggestionProvider: SuggestionProvider,
        dispatcherProvider: DispatcherProvider
    ): Subscription<AppState, AppAction> =
        LoadSuggestionsSubscription(suggestionProvider, dispatcherProvider)
}
