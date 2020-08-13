package com.toggl.settings.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.models.domain.SettingsType
import com.toggl.settings.domain.SettingsViewModel

@Composable
internal fun SubPageSetting(
    model: SettingsViewModel.SubPage
) {
    Text(
        modifier = Modifier.padding(start = grid_2),
        text = model.label,
        style = MaterialTheme.typography.body2
    )
}

internal val previewSubPage = SettingsViewModel.SubPage("Help", SettingsType.Help)

@Preview("SubPage light theme")
@Composable
fun PreviewSubPageLight() {
    ThemedPreview {
        SettingsRow(previewSubPage) { }
    }
}

@Preview("SubPage dark theme")
@Composable
fun PreviewSubPageDark() {
    ThemedPreview(darkTheme = true) {
        SettingsRow(previewSubPage) { }
    }
}
