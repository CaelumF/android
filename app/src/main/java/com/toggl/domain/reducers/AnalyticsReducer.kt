package com.toggl.domain.reducers

import com.toggl.api.exceptions.UnauthorizedException
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.feature.navigation.getRouteParam
import com.toggl.common.feature.services.analytics.AnalyticsService
import com.toggl.common.feature.services.analytics.Event
import com.toggl.common.feature.services.analytics.parameters.CalendarSuggestionProviderState
import com.toggl.common.feature.services.analytics.parameters.EditViewCloseReason
import com.toggl.common.feature.services.analytics.parameters.EditViewOpenReason.GroupHeader
import com.toggl.common.feature.services.analytics.parameters.EditViewOpenReason.RunningTimeEntryCard
import com.toggl.common.feature.services.analytics.parameters.EditViewOpenReason.SingleTimeEntry
import com.toggl.common.feature.services.analytics.parameters.SignOutReason
import com.toggl.common.feature.services.analytics.parameters.SuggestionProviderType
import com.toggl.common.feature.services.analytics.parameters.TimeEntryDeleteOrigin.GroupedLogSwipe
import com.toggl.common.feature.services.analytics.parameters.TimeEntryDeleteOrigin.LogSwipe
import com.toggl.common.feature.services.analytics.parameters.TimeEntryStopOrigin.Manual
import com.toggl.common.feature.timeentry.extensions.isRepresentingGroup
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.domain.loading.LoadingAction
import com.toggl.models.common.SwipeDirection
import com.toggl.models.domain.EditableTimeEntry
import com.toggl.onboarding.common.domain.OnboardingAction
import com.toggl.onboarding.login.domain.LoginAction
import com.toggl.settings.domain.SettingsAction
import com.toggl.timer.common.domain.TimerAction
import com.toggl.timer.log.domain.TimeEntriesLogAction
import com.toggl.timer.running.domain.RunningTimeEntryAction
import com.toggl.timer.startedit.domain.StartEditAction
import com.toggl.timer.suggestions.domain.Suggestion
import com.toggl.timer.suggestions.domain.SuggestionsAction
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject

class AnalyticsReducer @Inject constructor(
    private val analyticsService: AnalyticsService
) : Reducer<AppState, AppAction> {
    override fun reduce(
        state: MutableValue<AppState>,
        action: AppAction
    ): List<Effect<AppAction>> {
        action.toEvents(state).forEach { analyticsService.track(it) }
        return noEffect()
    }

    private fun AppAction.toEvents(state: MutableValue<AppState>): List<Event> =
        when (this) {
            is AppAction.Onboarding -> when (val onboardingAction = action) {
                is OnboardingAction.Login -> onboardingAction.action.toEvents()
                else -> emptyList()
            }
            is AppAction.Loading -> when (val loadingAction = action) {
                is LoadingAction.SuggestionsLoaded -> loadingAction.toEvents(state)
                else -> emptyList()
            }
            is AppAction.Timer -> when (val timerAction = action) {
                is TimerAction.StartEditTimeEntry -> timerAction.action.toEvents(state)
                is TimerAction.TimeEntriesLog -> timerAction.action.toEvents()
                is TimerAction.RunningTimeEntry -> timerAction.action.toEvents()
                is TimerAction.Suggestions -> timerAction.action.toEvents()
                else -> emptyList()
            }
            is AppAction.Settings -> action.toEvents(state)
            else -> emptyList()
        }

    private fun LoadingAction.SuggestionsLoaded.toEvents(state: MutableValue<AppState>): List<Event> {
        val randomForestCount = 0
        val calendarCount = suggestions.filterIsInstance<Suggestion.Calendar>().size
        val mostUsedCount = suggestions.filterIsInstance<Suggestion.MostUsed>().size
        val providerCounts = mapOf(
            "Calendar" to calendarCount.toString(),
            "RandomForest" to randomForestCount.toString(),
            "MostUsedTimeEntries" to mostUsedCount.toString()
        )
        val calendarAuthorized = state().calendarPermissionWasGranted
        val workspaceCount = state().workspaces.size
        val calendarProviderState = when {
            !calendarAuthorized -> CalendarSuggestionProviderState.Unauthorized
            calendarCount > 0 -> CalendarSuggestionProviderState.SuggestionsAvailable
            else -> CalendarSuggestionProviderState.NoEvents
        }
        return listOf(Event.suggestionsPresented(suggestions.size, providerCounts, calendarProviderState, workspaceCount))
    }

    private fun LoginAction.toEvents(): List<Event> =
        listOfNotNull(
            when (this) {
                is LoginAction.SetUserError ->
                    when (failure.throwable) {
                        is UnauthorizedException -> Event.incorrectEmailOrPasswordLoginFailure()
                        else -> Event.unknownLoginFailure(failure.throwable)
                    }
                else -> null
            }
        )

    private fun StartEditAction.toEvents(state: MutableValue<AppState>): List<Event> =
        listOfNotNull(
            when (this) {
                StartEditAction.CloseButtonTapped,
                StartEditAction.DialogDismissed -> Event.editViewClosed(EditViewCloseReason.Close)
                StartEditAction.DoneButtonTapped ->
                    state().backStack.getRouteParam<EditableTimeEntry>()?.let {
                        Event.editViewClosed(
                            if (it.isRepresentingGroup()) EditViewCloseReason.GroupSave
                            else EditViewCloseReason.Save
                        )
                    }
                else -> null
            }
        )

    private fun RunningTimeEntryAction.toEvents(): List<Event> =
        listOfNotNull(
            when (this) {
                RunningTimeEntryAction.StopButtonTapped -> Event.timeEntryStopped(Manual)
                RunningTimeEntryAction.CardTapped -> Event.editViewOpened(RunningTimeEntryCard)
                else -> null
            }
        )

    private fun TimeEntriesLogAction.toEvents(): List<Event> =
        listOfNotNull(
            when {
                this is TimeEntriesLogAction.TimeEntryTapped
                -> Event.editViewOpened(SingleTimeEntry)
                this is TimeEntriesLogAction.TimeEntryGroupTapped
                -> Event.editViewOpened(GroupHeader)
                this is TimeEntriesLogAction.TimeEntrySwiped && this.direction == SwipeDirection.Right
                -> Event.timeEntryDeleted(LogSwipe)
                this is TimeEntriesLogAction.TimeEntryGroupSwiped && this.direction == SwipeDirection.Right
                -> Event.timeEntryDeleted(GroupedLogSwipe)
                this is TimeEntriesLogAction.UndoButtonTapped ->
                    Event.undoTapped()
                else -> null
            }
        )

    private fun SuggestionsAction.toEvents(): List<Event> =
        when (this) {
            is SuggestionsAction.SuggestionTapped -> when (suggestion) {
                is Suggestion.MostUsed -> listOf(Event.suggestionStarted(SuggestionProviderType.MostUsedTimeEntries))
                is Suggestion.Calendar -> {
                    val currentTime = OffsetDateTime.now()
                    val startTime = (suggestion as Suggestion.Calendar).calendarEvent.startTime
                    val offset = Duration.between(currentTime, startTime)
                    listOf(
                        Event.suggestionStarted(SuggestionProviderType.Calendar),
                        Event.calendarSuggestionContinueEvent(offset)
                    )
                }
            }
            else -> emptyList()
        }

    private fun SettingsAction.toEvents(state: MutableValue<AppState>): List<Event> =
        listOfNotNull(
            when (this) {
                SettingsAction.GroupSimilarTimeEntriesToggled -> Event.groupTimeEntriesSettingsChanged(!state().userPreferences.groupSimilarTimeEntriesEnabled)
                SettingsAction.SignOutTapped -> Event.signOutTapped(SignOutReason.Settings)
                else -> null
            }
        )
}
