package com.toggl.settings.domain

import com.toggl.common.feature.compose.ResOrStr
import com.toggl.common.feature.compose.ResOrStr.Empty
import com.toggl.common.feature.compose.ResOrStr.Res
import com.toggl.common.feature.compose.ResOrStr.Str
import com.toggl.common.feature.extensions.getEnabledCalendars
import com.toggl.common.services.permissions.PermissionCheckerService
import com.toggl.models.domain.SettingsType
import com.toggl.settings.BuildConfig
import com.toggl.settings.R
import javax.inject.Inject

class SettingsStructureBlueprint @Inject constructor(
    private val permissionCheckerService: PermissionCheckerService
) {

    suspend fun calendarSections(state: SettingsState): List<SettingsSectionBlueprint> {
        val calendarIntegrationEnabled = permissionCheckerService.hasCalendarPermission() &&
            state.userPreferences.calendarIntegrationEnabled

        val headerSection = SettingsSectionBlueprint(
            Empty,
            listOf(
                SettingsType.AllowCalendarAccess,
                SettingsType.CalendarPermissionInfo
            )
        )

        if (!calendarIntegrationEnabled) return listOf(headerSection)

        val availableCalendars = state.calendars.values
        val userCalendars = availableCalendars.getEnabledCalendars(state.userPreferences.calendarIds)

        val calendarSections = availableCalendars
            .groupBy { it.sourceName }
            .map { (groupName, calendars) ->
                SettingsSectionBlueprint(
                    Str(groupName),
                    calendars.map {
                        SettingsType.Calendar(it.name, it.id, userCalendars.contains(it))
                    }
                )
            }

        return listOf(headerSection) + calendarSections
    }

    companion object {
        val debugSection = if (BuildConfig.DEBUG) listOf(
            SettingsSectionBlueprint(
                Str("Debug"),
                listOf(
                    SettingsType.InsertMockData
                )
            )
        ) else listOf()

        val mainSections = listOf(
            SettingsSectionBlueprint(
                Res(R.string.your_profile),
                listOf(
                    SettingsType.TextSetting.Name,
                    SettingsType.TextSetting.Email,
                    SettingsType.Workspace
                )
            ),
            SettingsSectionBlueprint(
                Res(R.string.date_and_time),
                listOf(
                    SettingsType.DateFormat,
                    SettingsType.TwentyFourHourClock,
                    SettingsType.DurationFormat,
                    SettingsType.FirstDayOfTheWeek,
                    SettingsType.GroupSimilar
                )
            ),
            SettingsSectionBlueprint(
                Res(R.string.timer_defaults),
                listOf(
                    SettingsType.CellSwipe,
                    SettingsType.ManualMode
                )
            ),
            SettingsSectionBlueprint(
                Res(R.string.calendar_label),
                listOf(
                    SettingsType.CalendarSettings,
                    SettingsType.SmartAlert
                )
            ),
            SettingsSectionBlueprint(
                Res(R.string.general),
                listOf(
                    SettingsType.SubmitFeedback,
                    SettingsType.About,
                    SettingsType.Help
                )
            ),
            SettingsSectionBlueprint(
                Res(R.string.sync),
                listOf(
                    SettingsType.SignOut
                )
            )
        ) + debugSection

        val aboutSection =
            SettingsSectionBlueprint(
                Empty,
                listOf(
                    SettingsType.PrivacyPolicy,
                    SettingsType.TermsOfService,
                    SettingsType.Licenses
                )
            )
    }
}

data class SettingsSectionBlueprint(
    val title: ResOrStr,
    val settingsList: List<SettingsType>
)
