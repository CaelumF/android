package com.toggl.settings.ui.common

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.Shapes
import com.toggl.common.feature.compose.theme.TogglTheme
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.common.feature.compose.theme.grid_8
import com.toggl.models.domain.SettingsType
import com.toggl.models.domain.User
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email
import com.toggl.settings.R
import com.toggl.settings.domain.SettingsAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Composable
internal fun TextPickerDialogWithHeader(
    setting: Flow<SettingsType.TextSetting>,
    user: Flow<User>,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    Dialog(
        onCloseRequest = { dispatcher(SettingsAction.FinishedEditingSetting) }
    ) {
        TogglTheme {
            Box(shape = Shapes.medium, backgroundColor = MaterialTheme.colors.background) {
                TextPickerContent(setting, user, dispatcher)
            }
        }
    }
}

@Composable
internal fun TextPickerContent(
    settingFlow: Flow<SettingsType.TextSetting>,
    user: Flow<User>,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val setting by settingFlow.collectAsState(initial = SettingsType.TextSetting.Name)

    val validateText: (String) -> Boolean = { text ->
        when (setting) {
            SettingsType.TextSetting.Name -> text.isNotBlank()
            SettingsType.TextSetting.Email -> Email.from(text) is Email.Valid
        }
    }

    val initialText by user.map {
        when (setting) {
            SettingsType.TextSetting.Name -> it.name
            SettingsType.TextSetting.Email -> it.email.toString()
        }
    }.collectAsState(initial = "")

    var textState by state { TextFieldValue(initialText) }
    Column(modifier = Modifier.padding(grid_3).fillMaxWidth()) {

        val label = stringResource(
            when (setting) {
                SettingsType.TextSetting.Name -> R.string.name
                SettingsType.TextSetting.Email -> R.string.email
            }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.preferredHeight(grid_8)
        )
        TextField(
            value = textState,
            label = { Text(text = label) },
            onValueChange = { textState = it },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.gravity(Alignment.End)) {
            TextButton(onClick = { dispatcher(SettingsAction.FinishedEditingSetting) }) {
                Text(text = stringResource(R.string.cancel))
            }
            TextButton(
                onClick = {
                    if (validateText(textState.text)) {
                        dispatcher(SettingsAction.UpdateName(textState.text))
                    }
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    }
}

private val validUser = User(
    id = 0,
    apiToken = ApiToken.from("12345678901234567890123456789012") as ApiToken.Valid,
    defaultWorkspaceId = 1,
    email = Email.from("valid.mail@toggl.com") as Email.Valid,
    name = "User name"
)

@Preview("Text picker light theme")
@Composable
fun PreviewTextPickerDialogWithHeaderLight() {
    ThemedPreview {
        TextPickerContent(
            flowOf(SettingsType.TextSetting.Email),
            flowOf(validUser)
        ) { }
    }
}

@Preview("Text picker dark theme")
@Composable
fun PreviewTextPickerDialogWithHeaderDark() {
    ThemedPreview(darkTheme = true) {
        TextPickerContent(
            flowOf(SettingsType.TextSetting.Name),
            flowOf(validUser)
        ) { }
    }
}
