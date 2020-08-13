package com.toggl.settings.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.models.domain.SettingsType
import com.toggl.settings.domain.SettingsViewModel

@Composable
internal fun ActionSetting(
    model: SettingsViewModel.ActionRow
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = model.label,
        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary)
    )
}

internal val previewButton = SettingsViewModel.ActionRow("Sign Out", SettingsType.SignOut)

@Preview("Button light theme")
@Composable
fun PreviewButtonLight() {
    ThemedPreview {
        SettingsRow(previewButton) { }
    }
}

@Preview("Button dark theme")
@Composable
fun PreviewButtonDark() {
    ThemedPreview(darkTheme = true) {
        SettingsRow(previewButton) { }
    }
}
