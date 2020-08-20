package com.toggl.settings.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toggl.common.feature.compose.theme.grid_1
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.models.domain.SettingsType
import com.toggl.settings.R
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SettingsSectionViewModel
import com.toggl.settings.domain.SettingsViewModel

@Composable
fun Section(
    section: SettingsSectionViewModel,
    withTitle: Boolean,
    modifier: Modifier = Modifier,
    dispatcher: (SettingsAction) -> Unit
) {
    val columnModifier = Modifier.padding(top = grid_1, bottom = grid_1) then modifier

    Column(modifier = columnModifier) {
        if (withTitle) {
            Text(
                text = section.title,
                modifier = Modifier.fillMaxWidth().padding(grid_2),
                style = MaterialTheme.typography.subtitle1
            )
        }
        for (settingsOption in section.settingsOptions) {
            SettingsRow(settingsOption, dispatcher)
        }
    }
}

@Composable
internal fun SettingsRow(
    setting: SettingsViewModel,
    dispatcher: (SettingsAction) -> Unit
) {

    val tapAction = when (val settingsType = setting.settingsType) {
        SettingsType.Name -> null
        SettingsType.Email -> null
        SettingsType.TwentyFourHourClock -> SettingsAction.Use24HourClockToggled
        SettingsType.GroupSimilar -> SettingsAction.GroupSimilarTimeEntriesToggled
        SettingsType.CellSwipe -> SettingsAction.CellSwipeActionsToggled
        SettingsType.ManualMode -> SettingsAction.ManualModeToggled
        SettingsType.CalendarSettings -> SettingsAction.OpenCalendarSettingsTapped
        SettingsType.SubmitFeedback -> SettingsAction.OpenSubmitFeedbackTapped
        SettingsType.About -> SettingsAction.OpenAboutTapped
        SettingsType.PrivacyPolicy -> SettingsAction.OpenPrivacyPolicyTapped
        SettingsType.TermsOfService -> SettingsAction.OpenTermsOfServiceTapped
        SettingsType.Licenses -> SettingsAction.OpenLicencesTapped
        SettingsType.Help -> SettingsAction.OpenHelpTapped
        SettingsType.SignOut -> SettingsAction.SignOutTapped
        SettingsType.AllowCalendarAccess -> SettingsAction.AllowCalendarAccessToggled
        is SettingsType.Calendar -> SettingsAction.UserCalendarIntegrationToggled(settingsType.id)
        SettingsType.CalendarPermissionInfo -> null
        is SettingsType.SingleChoiceSetting -> SettingsAction.OpenSelectionDialog(settingsType)
        // debug
        SettingsType.InsertMockData -> SettingsAction.OpenSelectionDialog(settingsType)
    }

    val onClick = tapAction?.let { { dispatcher(it) } }

    val clickModifier =
        if (onClick != null)
            Modifier.clickable(
                indication = RippleIndication(),
                onClick = onClick
            )
        else Modifier

    val rowModifier = Modifier.height(48.dp).fillMaxWidth() then clickModifier

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalGravity = Alignment.CenterVertically
    ) {
        when (setting) {
            is SettingsViewModel.Toggle -> ToggleSetting(setting)
            is SettingsViewModel.ListChoice -> ListChoiceSetting(setting)
            is SettingsViewModel.SubPage -> SubPageSetting(setting)
            is SettingsViewModel.ActionRow -> ActionSetting(setting)
            is SettingsViewModel.InfoText -> Text(
                text = stringResource(R.string.allow_calendar_message),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(grid_2)
            )
        }
    }
}
