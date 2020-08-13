package com.toggl.settings.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.TogglTheme
import com.toggl.models.domain.SettingsType
import com.toggl.settings.R
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SettingsSectionViewModel
import com.toggl.settings.domain.SettingsViewModel
import com.toggl.settings.ui.common.SectionList
import com.toggl.settings.ui.common.SectionTitleMode
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingsPage(
    sectionsState: Flow<List<SettingsSectionViewModel>>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val observableSectionState by sectionsState.collectAsState(listOf())
    TogglTheme {
        SettingsPageContent(observableSectionState, statusBarHeight, navigationBarHeight, dispatcher)
    }
}

@Composable
fun SettingsPageContent(
    sectionsState: List<SettingsSectionViewModel>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = statusBarHeight),
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                title = { Text(text = stringResource(R.string.settings)) }
            )
        },
        bodyContent = {
            SectionList(
                sectionsList = sectionsState,
                titleMode = SectionTitleMode.All,
                dispatcher = dispatcher,
                navigationBarHeight = navigationBarHeight
            )
        }
    )
}

@Preview("Settings page light theme")
@Composable
fun PreviewSettingsPageLight() {
    ThemedPreview(false) {
        SettingsPageContent(
            settingsListPreviewData,
            10.dp,
            10.dp
        )
    }
}

@Preview("Settings page dark theme")
@Composable
fun PreviewSettingsPageDark() {
    ThemedPreview(true) {
        SettingsPageContent(
            settingsListPreviewData,
            10.dp,
            10.dp
        )
    }
}

val settingsListPreviewData: List<SettingsSectionViewModel> = listOf(
    SettingsSectionViewModel(
        title = "Your Profile",
        settingsOptions = listOf(
            SettingsViewModel.ListChoice("Name", SettingsType.TextSetting.Name, "Semanticer"),
            SettingsViewModel.ListChoice("Email Address", SettingsType.TextSetting.Email, "test@test.com"),
            SettingsViewModel.ListChoice("Workspace", SettingsType.Workspace, "Semanticer's workspace")
        )
    ),
    SettingsSectionViewModel(
        title = "Date and Time",
        settingsOptions = listOf(
            SettingsViewModel.ListChoice("Date format", SettingsType.DateFormat, "DD/MM/YYYY"),
            SettingsViewModel.Toggle("Use 24-hour clock", SettingsType.TwentyFourHourClock, false),
            SettingsViewModel.ListChoice("Duration format", SettingsType.DurationFormat, "Improved"),
            SettingsViewModel.ListChoice("First day of the week", SettingsType.FirstDayOfTheWeek, "Wednesday"),
            SettingsViewModel.Toggle("Group Similar time entries", SettingsType.GroupSimilar, false)
        )
    ),
    SettingsSectionViewModel(
        title = "Timer Defaults",
        settingsOptions = listOf(
            SettingsViewModel.Toggle("Cell Swipe Actions", SettingsType.CellSwipe, false),
            SettingsViewModel.Toggle("Manual mode", SettingsType.ManualMode, false)
        )
    ),
    SettingsSectionViewModel(
        title = "Calendar",
        settingsOptions = listOf(
            SettingsViewModel.SubPage("Calendar Settings", SettingsType.CalendarSettings),
            SettingsViewModel.SubPage("Smart alerts", SettingsType.SmartAlert)
        )
    ),
    SettingsSectionViewModel(
        title = "General",
        settingsOptions = listOf(
            SettingsViewModel.SubPage("Submit Feedback", SettingsType.SubmitFeedback),
            SettingsViewModel.SubPage("About", SettingsType.About),
            SettingsViewModel.SubPage("Help", SettingsType.Help)

        )
    ),
    SettingsSectionViewModel(
        title = "Sync",
        settingsOptions = listOf(
            SettingsViewModel.ActionRow("Sign Out", SettingsType.SignOut)
        )
    )
)
