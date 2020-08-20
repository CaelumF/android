package com.toggl.settings.ui.calendar

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun CalendarSettingsPage(
    calendarSettingsViewModels: Flow<List<SettingsSectionViewModel>>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit
) {
    val observableState by calendarSettingsViewModels.collectAsState(listOf())
    TogglTheme {
        CalendarSettingsPageContent(
            observableState,
            statusBarHeight,
            navigationBarHeight,
            dispatcher
        )
    }
}

@Composable
fun CalendarSettingsPageContent(
    settingsViewModels: List<SettingsSectionViewModel>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = statusBarHeight),
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { dispatcher.invoke(SettingsAction.FinishedEditingSetting) }) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                }
            )
        },
        bodyContent = {
            SectionList(
                sectionsList = settingsViewModels,
                titleMode = SectionTitleMode.AllButFirst,
                dispatcher = dispatcher,
                navigationBarHeight = navigationBarHeight
            )
        }
    )
}

@Composable
@Preview("Settings page light theme")
fun PreviewCalendarSettingsPageLight() {
    ThemedPreview(false) {
        CalendarSettingsPageContent(calendarSettingsPreviewData, 10.dp, 10.dp) { }
    }
}

@Composable
@Preview("Settings page dark theme")
fun PreviewCalendarSettingsPageDark() {
    ThemedPreview(true) {
        CalendarSettingsPageContent(calendarSettingsPreviewData, 10.dp, 10.dp) { }
    }
}

val calendarSettingsPreviewData: List<SettingsSectionViewModel> = listOf(
    SettingsSectionViewModel(
        "",
        listOf(
            SettingsViewModel.Toggle("Link calendars", SettingsType.AllowCalendarAccess, true),
            SettingsViewModel.InfoText("Toggl needs access to your calendar in order to display events. Events are visible to you only and won’t appear in Reports.", SettingsType.CalendarPermissionInfo)
        )
    ),
    SettingsSectionViewModel(
        "someone@toggl.com",
        listOf(
            SettingsViewModel.Toggle("Meetings", SettingsType.Calendar("123", "123", true), true),
            SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123", "123", true), false),
            SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123", "123", true), false)
        )
    ),
    SettingsSectionViewModel(
        "team@toggl.com",
        listOf(
            SettingsViewModel.Toggle("Meetings", SettingsType.Calendar("123", "123", true), false),
            SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123", "123", true), true),
            SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123", "123", true), false)
        )
    )
)
