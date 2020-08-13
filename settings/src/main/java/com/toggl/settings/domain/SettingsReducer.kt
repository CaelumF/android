package com.toggl.settings.domain

import com.toggl.api.clients.feedback.FeedbackApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.architecture.extensions.effectOf
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.navigation.ExternalLocation
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.pop
import com.toggl.common.feature.navigation.push
import com.toggl.common.services.permissions.PermissionCheckerService
import com.toggl.models.domain.PlatformInfo
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.repository.interfaces.SettingsRepository
import com.toggl.repository.interfaces.UserRepository
import javax.inject.Inject

class SettingsReducer @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionCheckerService: PermissionCheckerService,
    private val platformInfo: PlatformInfo,
    private val signOutEffect: SignOutEffect,
    private val feedbackDataBuilder: FeedbackDataBuilder,
    private val feedbackApiClient: FeedbackApiClient,
    private val mockDatabaseInitializer: MockDatabaseInitializer,
    private val dispatcherProvider: DispatcherProvider
) : Reducer<SettingsState, SettingsAction> {

    override fun reduce(
        state: MutableValue<SettingsState>,
        action: SettingsAction
    ): List<Effect<SettingsAction>> =
        when (action) {
            // Text results
            is SettingsAction.UpdateEmail -> state.updateUser { copy(email = action.email) }
            is SettingsAction.UpdateName -> state.updateUser { copy(name = action.name) }
            is SettingsAction.UserUpdated -> state.mutateWithoutEffects { copy(user = action.user) }

            // Toggles
            is SettingsAction.ManualModeToggled -> state.updatePrefs { copy(manualModeEnabled = !manualModeEnabled) }
            is SettingsAction.Use24HourClockToggled -> state.updatePrefs { copy(twentyFourHourClockEnabled = !twentyFourHourClockEnabled) }
            is SettingsAction.CellSwipeActionsToggled -> state.updatePrefs { copy(cellSwipeActionsEnabled = !cellSwipeActionsEnabled) }
            is SettingsAction.GroupSimilarTimeEntriesToggled -> state.updatePrefs { copy(groupSimilarTimeEntriesEnabled = !groupSimilarTimeEntriesEnabled) }

            // Dialogs
            is SettingsAction.OpenSelectionDialog -> state.navigateTo(Route.SettingsDialog(action.settingType))
            is SettingsAction.UserPreferencesUpdated -> state.mutateWithoutEffects { copy(userPreferences = action.userPreferences) }
            is SettingsAction.WorkspaceSelected -> state.updateUser { copy(defaultWorkspaceId = action.selectedWorkspaceId) }
            is SettingsAction.DateFormatSelected -> state.updatePrefs { copy(dateFormat = action.dateFormat) }
            is SettingsAction.DurationFormatSelected -> state.updatePrefs { copy(durationFormat = action.durationFormat) }
            is SettingsAction.FirstDayOfTheWeekSelected -> state.updatePrefs { copy(firstDayOfTheWeek = action.firstDayOfTheWeek) }
            is SettingsAction.SmartAlertsOptionSelected -> state.updatePrefs { copy(smartAlertsOption = action.smartAlertsOption) }
            is SettingsAction.MockDataSetSelected -> effect(InsertMockDataEffect(action.mockDataSetSize.amount, mockDatabaseInitializer, dispatcherProvider))
            is SettingsAction.FinishedEditingSetting -> {
                state.updateSendFeedbackRequestStateWithoutEffects(Loadable.Uninitialized)
                state.mutateWithoutEffects { copy(backStack = backStack.pop()) }
            }

            // Feedback
            SettingsAction.OpenSubmitFeedbackTapped -> state.navigateTo(Route.Feedback)
            is SettingsAction.FeedbackSent -> state.updateSendFeedbackRequestStateWithoutEffects(Loadable.Loaded(Unit))
            is SettingsAction.SendFeedbackResultSeen -> {
                val feedbackRequestResultThatWasSeen = state().localState.sendFeedbackRequest
                state.updateSendFeedbackRequestStateWithoutEffects(Loadable.Uninitialized)
                if (feedbackRequestResultThatWasSeen is Loadable.Loaded<Unit>) {
                    effectOf(SettingsAction.FinishedEditingSetting)
                } else noEffect()
            }
            is SettingsAction.SetSendFeedbackError -> state.updateSendFeedbackRequestStateWithoutEffects(
                Loadable.Error(Failure(action.throwable, ""))
            )
            is SettingsAction.SendFeedbackTapped -> {
                state.mutate {
                    copy(
                        localState = localState.copy(sendFeedbackRequest = Loadable.Loading)
                    )
                }
                effect(
                    SendFeedbackEffect(
                        action.feedbackMessage,
                        state().user,
                        platformInfo,
                        feedbackDataBuilder,
                        feedbackApiClient,
                        state().userPreferences,
                        dispatcherProvider
                    )
                )
            }

            // Calendar settings
            SettingsAction.OpenCalendarSettingsTapped -> state.navigateTo(Route.CalendarSettings)
            is SettingsAction.AllowCalendarAccessToggled -> state.handleAllowCalendarAccessToggled()
            is SettingsAction.CalendarPermissionRequested -> state.mutateWithoutEffects { copy(shouldRequestCalendarPermission = true) }
            is SettingsAction.CalendarPermissionReceived -> state.mutateWithoutEffects { copy(shouldRequestCalendarPermission = false) }
            is SettingsAction.UserCalendarIntegrationToggled -> state.updatePrefs {
                if (calendarIds.contains(action.calendarId)) copy(calendarIds = calendarIds - action.calendarId)
                else copy(calendarIds = calendarIds + action.calendarId)
            }

            // About & Help
            SettingsAction.OpenAboutTapped -> state.navigateTo(Route.About)
            SettingsAction.OpenLicencesTapped -> state.navigateTo(Route.Licences)
            SettingsAction.OpenPrivacyPolicyTapped -> state.mutateWithoutEffects { copy(externalLocationToShow = ExternalLocation.PrivacyPolicy) }
            SettingsAction.OpenTermsOfServiceTapped -> state.mutateWithoutEffects { copy(externalLocationToShow = ExternalLocation.TermsOfService) }
            SettingsAction.OpenHelpTapped -> state.mutateWithoutEffects { copy(externalLocationToShow = ExternalLocation.Help) }

            // Sign out
            SettingsAction.SignOutTapped -> effect(signOutEffect)
            SettingsAction.SignOutCompleted -> noEffect()
        }

    private fun MutableValue<SettingsState>.handleAllowCalendarAccessToggled(): List<Effect<SettingsAction>> {
        val updatePrefsEffects = updatePrefs {
            copy(
                calendarIntegrationEnabled = !calendarIntegrationEnabled,
                calendarIds = if (calendarIntegrationEnabled) calendarIds else emptyList()
            )
        }

        val integrationIsCurrentlyDisabled = !this().userPreferences.calendarIntegrationEnabled

        return if (integrationIsCurrentlyDisabled && !permissionCheckerService.hasCalendarPermission())
            updatePrefsEffects + RequestCalendarPermissionEffect()
        else
            updatePrefsEffects
    }

    private fun MutableValue<SettingsState>.updateUser(updateBlock: User.() -> User) =
        effect(UpdateUserEffect(this().user.updateBlock(), userRepository, dispatcherProvider))

    private fun MutableValue<SettingsState>.updatePrefs(updateBlock: UserPreferences.() -> UserPreferences) =
        effect(UpdateUserPreferencesEffect(this().userPreferences.updateBlock(), settingsRepository, dispatcherProvider))

    private fun MutableValue<SettingsState>.updateSendFeedbackRequestStateWithoutEffects(loadable: Loadable<Unit>) =
        mutateWithoutEffects<SettingsState, SettingsAction> {
            copy(
                localState = localState.copy(sendFeedbackRequest = loadable)
            )
        }

    private fun MutableValue<SettingsState>.navigateTo(route: Route): List<Effect<SettingsAction>> =
        mutateWithoutEffects { copy(backStack = backStack.push(route)) }
}
