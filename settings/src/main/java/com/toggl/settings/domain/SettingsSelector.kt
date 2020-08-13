package com.toggl.settings.domain

import android.content.Context
import com.toggl.architecture.core.Selector
import com.toggl.common.services.permissions.PermissionCheckerService
import com.toggl.models.domain.SettingsType
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.models.domain.Workspace
import com.toggl.settings.R
import com.toggl.settings.compose.toStr
import javax.inject.Inject

typealias SectionBlueprintProvider = suspend (SettingsState) -> List<SettingsSectionBlueprint>

class SettingsSelector @Inject constructor(
    private val context: Context,
    private val permissionCheckerService: PermissionCheckerService,
    private val sectionsBlueprintProvider: SectionBlueprintProvider
) : Selector<SettingsState, List<SettingsSectionViewModel>> {

    override suspend fun select(state: SettingsState): List<SettingsSectionViewModel> =
        sectionsBlueprintProvider(state).map { it.toViewModel(state.user, state.userPreferences, state.workspaces) }

    private fun SettingsSectionBlueprint.toViewModel(user: User, userPreferences: UserPreferences, workspaces: Map<Long, Workspace>): SettingsSectionViewModel =
        SettingsSectionViewModel(title.toStr(context), settingsList.mapNotNull { it.toViewModel(user, userPreferences, workspaces) })

    private fun SettingsType.toViewModel(user: User, userPreferences: UserPreferences, workspaces: Map<Long, Workspace>): SettingsViewModel? =
        when (this) {
            SettingsType.Name -> SettingsViewModel.ListChoice(
                context.getString(R.string.name),
                this,
                user.name
            )
            SettingsType.Email -> SettingsViewModel.ListChoice(
                context.getString(R.string.email_address),
                this,
                user.email.email
            )
            SettingsType.Workspace -> SettingsViewModel.ListChoice(
                context.getString(R.string.workspace),
                this,
                workspaces[user.defaultWorkspaceId]?.name ?: ""
            )
            SettingsType.DateFormat -> SettingsViewModel.ListChoice(
                context.getString(R.string.date_format),
                this,
                userPreferences.dateFormat.label
            )
            SettingsType.TwentyFourHourClock -> SettingsViewModel.Toggle(
                context.getString(R.string.use_24_hour_clock),
                this,
                userPreferences.twentyFourHourClockEnabled
            )
            SettingsType.DurationFormat -> SettingsViewModel.ListChoice(
                context.getString(R.string.duration_format),
                this,
                userPreferences.durationFormat.getTranslatedRepresentation(context)
            )
            SettingsType.FirstDayOfTheWeek -> SettingsViewModel.ListChoice(
                context.getString(R.string.first_day_of_the_week),
                this,
                userPreferences.firstDayOfTheWeek.getTranslatedRepresentation(context)
            )
            SettingsType.GroupSimilar -> SettingsViewModel.Toggle(
                context.getString(R.string.group_similar_time_entries),
                this,
                userPreferences.groupSimilarTimeEntriesEnabled
            )
            SettingsType.CellSwipe -> SettingsViewModel.Toggle(
                context.getString(R.string.cell_swipe_actions),
                this,
                userPreferences.cellSwipeActionsEnabled
            )
            SettingsType.ManualMode -> SettingsViewModel.Toggle(
                context.getString(R.string.manual_mode),
                this,
                userPreferences.manualModeEnabled
            )
            SettingsType.CalendarSettings -> SettingsViewModel.SubPage(
                context.getString(R.string.calendar_settings),
                this
            )
            SettingsType.SmartAlert ->
                if (!userPreferences.calendarIntegrationEnabled || !permissionCheckerService.hasCalendarPermission()) null
                else
                    SettingsViewModel.ListChoice(
                        context.getString(R.string.smart_alerts),
                        this,
                        userPreferences.smartAlertsOption.getTranslatedRepresentation(context)
                    )
            SettingsType.SubmitFeedback -> SettingsViewModel.SubPage(
                context.getString(R.string.submit_feedback),
                this
            )
            SettingsType.About -> SettingsViewModel.SubPage(
                context.getString(R.string.about),
                this
            )
            SettingsType.PrivacyPolicy -> SettingsViewModel.SubPage(
                context.getString(R.string.privacy_policy),
                this
            )
            SettingsType.TermsOfService -> SettingsViewModel.SubPage(
                context.getString(R.string.terms_of_service),
                this
            )
            SettingsType.Licenses -> SettingsViewModel.SubPage(
                context.getString(R.string.licenses),
                this
            )
            SettingsType.Help -> SettingsViewModel.SubPage(
                context.getString(R.string.help),
                this
            )
            SettingsType.SignOut -> SettingsViewModel.ActionRow(
                context.getString(R.string.sign_out),
                this
            )
            SettingsType.AllowCalendarAccess -> SettingsViewModel.Toggle(
                context.getString(R.string.allow_calendar_access),
                this,
                userPreferences.calendarIntegrationEnabled
            )
            is SettingsType.Calendar -> SettingsViewModel.Toggle(
                name,
                this,
                enabled
            )
            SettingsType.CalendarPermissionInfo -> SettingsViewModel.InfoText(
                context.getString(R.string.allow_calendar_message),
                this
            )
            is SettingsType.SingleChoiceSetting -> throw IllegalStateException("All single choice settings have to be handled separately")
            // debug
            SettingsType.InsertMockData -> SettingsViewModel.ListChoice(
                "Insert mock data",
                this,
                ""
            )
        }
}
