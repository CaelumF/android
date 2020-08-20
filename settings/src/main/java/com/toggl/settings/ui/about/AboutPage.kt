package com.toggl.settings.ui.about

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
fun AboutPage(
    sectionsState: Flow<List<SettingsSectionViewModel>>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val observableSectionState by sectionsState.collectAsState(listOf())
    TogglTheme {
        AboutPageContent(observableSectionState, statusBarHeight, navigationBarHeight, dispatcher)
    }
}

@Composable
fun AboutPageContent(
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
                title = { Text(text = stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = { dispatcher(SettingsAction.FinishedEditingSetting) }) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                }
            )
        },
        bodyContent = {
            SectionList(
                sectionsList = sectionsState,
                titleMode = SectionTitleMode.None,
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
        AboutPageContent(aboutPreviewData, 10.dp, 10.dp) { }
    }
}

@Composable
@Preview("Settings page dark theme")
fun PreviewCalendarSettingsPageDark() {
    ThemedPreview(true) {
        AboutPageContent(aboutPreviewData, 10.dp, 10.dp) { }
    }
}

val aboutPreviewData: List<SettingsSectionViewModel> = listOf(
    SettingsSectionViewModel(
        "Shouldn't be displayed",
        listOf(
            SettingsViewModel.SubPage("Terms of Service", SettingsType.TermsOfService),
            SettingsViewModel.SubPage("Privacy Policy", SettingsType.PrivacyPolicy),
            SettingsViewModel.SubPage("Licences", SettingsType.Licenses)
        )
    )
)
