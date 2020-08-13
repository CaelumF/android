package com.toggl.settings.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.models.domain.SettingsType
import com.toggl.settings.domain.SettingsViewModel

@Composable
internal fun ToggleSetting(
    model: SettingsViewModel.Toggle,
    onClickAction: () -> Unit
) {
    Text(
        modifier = Modifier.padding(start = grid_2),
        text = model.label,
        style = MaterialTheme.typography.body2
    )
    Switch(
        modifier = Modifier.padding(end = grid_2),
        checked = model.toggled,
        color = MaterialTheme.colors.primary,
        onCheckedChange = { onClickAction() }
    )
}

internal val previewToggle = SettingsViewModel.Toggle("Toggle title", SettingsType.ManualMode, false)

@Preview("Toggle with light theme")
@Composable
fun PreviewToggleLight() {
    ThemedPreview {
        Column {
            SettingsRow(previewToggle) { }
            SettingsRow(previewToggle.copy(toggled = true)) { }
        }
    }
}

@Preview("Toggle with dark theme")
@Composable
fun PreviewToggleDark() {
    ThemedPreview(darkTheme = true) {
        Column {
            SettingsRow(previewToggle) { }
            SettingsRow(previewToggle.copy(toggled = true)) { }
        }
    }
}
