package com.toggl.settings.ui.composables.pages

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.toggl.models.domain.SettingsType
import com.toggl.settings.R
import com.toggl.settings.compose.ThemedPreview
import com.toggl.settings.compose.theme.TogglTheme
import com.toggl.settings.compose.theme.grid_2
import com.toggl.settings.domain.CalendarSettingsViewModel
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SettingsSectionViewModel
import com.toggl.settings.domain.SettingsViewModel
import com.toggl.settings.ui.composables.Section
import com.toggl.settings.ui.composables.SettingsRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun CalendarSettingsPage(
    calendarSettingsViewModels: Flow<List<CalendarSettingsViewModel>>,
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
    calendarSettingsViewModels: List<CalendarSettingsViewModel>,
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
            val lastItem = calendarSettingsViewModels.lastOrNull()
            LazyColumnItems(items = calendarSettingsViewModels) { viewModel ->
                val bottomPadding = if (viewModel == lastItem) navigationBarHeight else 0.dp
                when (viewModel) {
                    is CalendarSettingsViewModel.IntegrationEnabled ->
                        LinkCalendarsSection(viewModel.accessGranted, dispatcher)
                    is CalendarSettingsViewModel.CalendarSection ->
                        Section(
                            section = viewModel.section,
                            dispatcher = dispatcher,
                            modifier = Modifier.padding(bottom = bottomPadding)
                        )
                }
            }
        }
    )
}

@Composable
fun LinkCalendarsSection(
    accessGranted: Boolean,
    dispatcher: (SettingsAction) -> Unit
) {

    val setting = SettingsViewModel.Toggle(
        stringResource(R.string.allow_calendar_access),
        SettingsType.AllowCalendarAccess,
        accessGranted
    )
    Column {
        SettingsRow(setting, dispatcher)
        Text(
            text = stringResource(R.string.allow_calendar_message),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(grid_2)
        )
    }
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

val calendarSettingsPreviewData: List<CalendarSettingsViewModel> = listOf(
    CalendarSettingsViewModel.IntegrationEnabled(false),
    CalendarSettingsViewModel.CalendarSection(SettingsSectionViewModel("someone@toggl.com", listOf(
        SettingsViewModel.Toggle("Meetings", SettingsType.Calendar("123"), true),
        SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123"), false),
        SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123"), false)
    ))),
    CalendarSettingsViewModel.CalendarSection(SettingsSectionViewModel("team@toggl.com", listOf(
        SettingsViewModel.Toggle("Meetings", SettingsType.Calendar("123"), false),
        SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123"), true),
        SettingsViewModel.Toggle("Peer Reviews", SettingsType.Calendar("123"), false)
    )))
)