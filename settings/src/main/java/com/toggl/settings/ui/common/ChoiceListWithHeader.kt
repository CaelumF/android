package com.toggl.settings.ui.common

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.Shapes
import com.toggl.common.feature.compose.theme.TogglTheme
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.common.feature.compose.theme.grid_4
import com.toggl.common.feature.compose.theme.grid_5
import com.toggl.common.feature.compose.theme.grid_6
import com.toggl.common.feature.compose.theme.grid_8
import com.toggl.settings.domain.ChoiceListItem
import com.toggl.settings.domain.SettingsAction
import com.toggl.settings.domain.SingleChoiceSettingViewModel
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

@Composable
internal fun SingleChoiceDialogWithHeader(
    items: Flow<SingleChoiceSettingViewModel>,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val observableItems by items.collectAsState(initial = SingleChoiceSettingViewModel.Empty)

    if (observableItems == SingleChoiceSettingViewModel.Empty) return

    Dialog(
        onCloseRequest = {
            observableItems.closeAction?.run(dispatcher)
        }
    ) {
        TogglTheme {
            Box(
                shape = Shapes.medium,
                backgroundColor = MaterialTheme.colors.background
            ) {
                SingleChoiceListWithHeader(
                    items = observableItems.items,
                    header = observableItems.header,
                    description = observableItems.description,
                    closeAction = observableItems.closeAction,
                    dispatcher = dispatcher
                )
            }
        }
    }
}

@Composable
internal fun SingleChoiceListWithHeader(
    items: List<ChoiceListItem>,
    header: String,
    description: String,
    closeAction: SettingsAction?,
    dispatcher: (SettingsAction) -> Unit = {}
) {

    fun dispatchOnSelected(item: ChoiceListItem) {
        item.dispatchSelected(dispatcher)
        closeAction?.run(dispatcher)
    }

    Column(modifier = Modifier.padding(grid_3).fillMaxWidth()) {
        Text(
            text = header,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            modifier = if (description.isEmpty()) Modifier.preferredHeight(grid_8) else Modifier.preferredHeight(
                grid_5
            )
        )
        if (description != "") {
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.preferredHeight(grid_8)
            )
        }
        RadioGroup {
            items.forEach { item ->
                Box(modifier = Modifier.clickable(onClick = { dispatchOnSelected(item) })) {
                    Row(
                        modifier = Modifier.preferredHeight(grid_6).fillMaxWidth().clickable { dispatchOnSelected(item) }
                    ) {
                        RadioButton(selected = item.isSelected, onClick = { dispatchOnSelected(item) })
                        Spacer(modifier = Modifier.preferredWidth(grid_4))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
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

@Preview("SingleChoiceListWithHeader light theme")
@Composable
fun PreviewSingleChoiceListWithHeaderLight() {
    ThemedPreview {
        SingleChoiceListWithHeader(
            previewItems,
            previewHeader,
            "",
            null
        )
    }
}

@Preview("SingleChoiceListWithHeader dark theme - with description")
@Composable
fun PreviewSingleChoiceListWithHeaderDark() {
    ThemedPreview(darkTheme = true) {
        SingleChoiceListWithHeader(
            previewItems,
            previewHeader,
            "A description description description description",
            null
        )
    }
}
