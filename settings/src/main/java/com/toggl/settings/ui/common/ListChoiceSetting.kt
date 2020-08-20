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
internal fun ListChoiceSetting(
    model: SettingsViewModel.ListChoice
) {
    Text(
        modifier = Modifier.padding(start = grid_2),
        text = model.label,
        style = MaterialTheme.typography.body2
    )
    Text(
        modifier = Modifier.padding(end = grid_2),
        text = model.selectedValueTitle,
        style = MaterialTheme.typography.body2
    )
}

internal val previewListChoice = SettingsViewModel.ListChoice("First day of the week", SettingsType.FirstDayOfTheWeek, "Wednesday")

@Preview("ListChoice light theme")
@Composable
fun PreviewListChoiceLight() {
    ThemedPreview {
        SettingsRow(previewListChoice) { }
    }
}

@Preview("ListChoice dark theme")
@Composable
fun PreviewListChoiceDark() {
    ThemedPreview(darkTheme = true) {
        SettingsRow(previewListChoice) { }
    }
}
