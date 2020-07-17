package com.toggl.settings.ui.composables

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Dialog
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.material.RadioButton
import androidx.ui.material.RadioGroup
import androidx.ui.tooling.preview.Preview
import com.toggl.settings.compose.ThemedPreview
import com.toggl.settings.compose.theme.TogglTheme
import com.toggl.settings.compose.theme.grid_1
import com.toggl.settings.compose.theme.grid_2
import com.toggl.settings.domain.ChoiceListItem
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SingleChoiceSettingViewModel
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

@Composable
internal fun ChoiceListWithHeader(
    items: Flow<SingleChoiceSettingViewModel>,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val observableItems by items.collectAsState(initial = SingleChoiceSettingViewModel.Empty)

    if (observableItems == SingleChoiceSettingViewModel.Empty) return

    Dialog(onCloseRequest = {
        dispatcher.invoke(SettingsAction.DialogDismissed)
    }) {
        TogglTheme {

            // Surface { // with this on, the list is visible, but not aware of dark theme
            Box(backgroundColor = MaterialTheme.colors.background) {
                ChoiceListWithHeaderWrapper(
                    items = observableItems.items,
                    header = observableItems.header,
                    dispatcher = dispatcher
                )
            }
        }  // }
    }
}

@Composable
internal fun ChoiceListWithHeaderWrapper(
    items: List<ChoiceListItem>,
    header: String,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    Column(modifier = Modifier.padding(grid_2)) {
        Text(
            text = header,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(Modifier.preferredHeight(grid_2))
        RadioGroup {
            items.forEach { item ->
                RadioGroupItem(selected = item.isSelected, onSelect = { item.dispatchSelected(dispatcher) }) {
                    Row {
                        RadioButton(selected = item.isSelected, onSelect = { item.dispatchSelected(dispatcher) })
                        Spacer(modifier = Modifier.preferredWidth(grid_1))
                        Text(text = item.label, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground)
                    }
                    Spacer(modifier = Modifier.preferredHeight(grid_1))
                }
            }
        }
    }
}

internal val previewItems = (0..5).map {
    if (it == 1) ChoiceListItem(
        "Choice selected",
        isSelected = true
    ) else ChoiceListItem("Choice ${Random.nextInt() % 100}")
}
internal val previewHeader = "First day of week"

@Preview("ChoiceListWithHeader light theme")
@Composable
fun PreviewChoiceListWithHeaderLight() {
    ThemedPreview {
        ChoiceListWithHeaderWrapper(previewItems, previewHeader)
    }
}

@Preview("ChoiceListWithHeader dark theme")
@Composable
fun PreviewChoiceListWithHeaderDark() {
    ThemedPreview(darkTheme = true) {
        ChoiceListWithHeaderWrapper(previewItems, previewHeader)
    }
}