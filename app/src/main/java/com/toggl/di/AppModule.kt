package com.toggl.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.StoreScopeProvider
import com.toggl.architecture.core.CompositeSubscription
import com.toggl.architecture.core.FlowStore
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.core.Store
import com.toggl.calendar.common.domain.CalendarAction
import com.toggl.calendar.common.domain.CalendarState
import com.toggl.common.DeepLinkUrls
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.domain.mappings.mapAppStateToCalendarState
import com.toggl.domain.mappings.mapAppStateToOnboardingState
import com.toggl.domain.mappings.mapAppStateToReportsState
import com.toggl.domain.mappings.mapAppStateToSettingsState
import com.toggl.domain.mappings.mapAppStateToTimerState
import com.toggl.domain.mappings.mapCalendarActionToAppAction
import com.toggl.domain.mappings.mapOnboardingActionToAppAction
import com.toggl.domain.mappings.mapReportsActionToAppAction
import com.toggl.domain.mappings.mapSettingsActionToAppAction
import com.toggl.domain.mappings.mapTimerActionToAppAction
import com.toggl.onboarding.common.domain.OnboardingAction
import com.toggl.onboarding.common.domain.OnboardingState
import com.toggl.reports.domain.ReportsAction
import com.toggl.reports.domain.ReportsState
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SettingsState
import com.toggl.timer.common.domain.TimerAction
import com.toggl.timer.common.domain.TimerState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun deeplinkUrls(
        @ApplicationContext context: Context
    ) = DeepLinkUrls.fromResources(context.resources)

    @InternalCoroutinesApi
    @Provides
    @Singleton
    fun appStore(
        @ProvideLoggingReducer reducer: Reducer<AppState, AppAction>,
        subscription: CompositeSubscription<AppState, AppAction>,
        dispatcherProvider: DispatcherProvider,
        application: Application
    ): Store<AppState, AppAction> {
        return FlowStore.create(
            initialState = AppState(),
            reducer = reducer,
            subscription = subscription,
            dispatcherProvider = dispatcherProvider,
            storeScopeProvider = application as StoreScopeProvider
        )
    }

    @Provides
    @Singleton
    fun dispatcherProvider() =
        DispatcherProvider(
            io = Dispatchers.IO,
            computation = Dispatchers.Default,
            main = Dispatchers.Main
        )
}

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppViewModelModule {
    @Provides
    fun onboardingStore(store: Store<AppState, AppAction>): Store<OnboardingState, OnboardingAction> =
        store.view(
            mapToLocalState = ::mapAppStateToOnboardingState,
            mapToGlobalAction = ::mapOnboardingActionToAppAction
        )

    @Provides
    fun timerStore(store: Store<AppState, AppAction>): Store<TimerState, TimerAction> =
        store.view(
            mapToLocalState = ::mapAppStateToTimerState,
            mapToGlobalAction = ::mapTimerActionToAppAction
        )

    @Provides
    fun reportsStore(store: Store<AppState, AppAction>): Store<ReportsState, ReportsAction> =
        store.optionalView(
            mapToLocalState = ::mapAppStateToReportsState,
            mapToGlobalAction = ::mapReportsActionToAppAction
        )

    @Provides
    fun calendarStore(store: Store<AppState, AppAction>): Store<CalendarState, CalendarAction> =
        store.view(
            mapToLocalState = ::mapAppStateToCalendarState,
            mapToGlobalAction = ::mapCalendarActionToAppAction
        )

    @Provides
    fun settingsStore(store: Store<AppState, AppAction>): Store<SettingsState, SettingsAction> =
        store.optionalView(
            mapToLocalState = ::mapAppStateToSettingsState,
            mapToGlobalAction = ::mapSettingsActionToAppAction
        )
}
