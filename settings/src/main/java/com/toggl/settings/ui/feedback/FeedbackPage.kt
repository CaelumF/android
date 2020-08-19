package com.toggl.settings.ui.feedback

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.toggl.architecture.Loadable
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.TogglTheme
import com.toggl.common.feature.compose.theme.grid_1
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.settings.R
import com.toggl.settings.domain.SettingsAction
import kotlinx.coroutines.flow.Flow

@Composable
fun FeedbackPage(
    sendFeedbackRequest: Flow<Loadable<Unit>>,
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    val sendFeedbackRequestState by sendFeedbackRequest.collectAsState(Loadable.Uninitialized)
    TogglTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(top = statusBarHeight),
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface,
                    title = { Text(text = stringResource(R.string.submit_feedback)) },
                    navigationIcon = {
                        IconButton(onClick = { dispatcher(SettingsAction.FinishedEditingSetting) }) {
                            Icon(Icons.Filled.ArrowBack)
                        }
                    }
                )
            },
            bodyContent = {
                FeedbackForm(
                    sendFeedbackRequestState,
                    navigationBarHeight,
                    dispatcher
                )
            }
        )
    }
}

@Composable
fun FeedbackForm(
    sendFeedbackRequestState: Loadable<Unit>,
    bottomPadding: Dp,
    dispatcher: (SettingsAction) -> Unit = {}
) {
    Column(Modifier.padding(grid_1).fillMaxSize()) {
        Text(text = stringResource(R.string.feedback_note))
        Spacer(modifier = Modifier.height(grid_2))

        var textState by remember { mutableStateOf(TextFieldValue("")) }
        Stack(modifier = Modifier.fillMaxWidth().preferredHeight(140.dp)) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text(text = stringResource(R.string.submit_feedback)) },
                modifier = Modifier.matchParentSize()
            )
            if (sendFeedbackRequestState is Loadable.Loading) {
                CircularProgressIndicator(modifier = Modifier.gravity(Alignment.Center).preferredSize(60.dp, 60.dp))
            }
        }

        Spacer(modifier = Modifier.height(grid_2))
        OutlinedButton(
            onClick = { dispatcher(SettingsAction.SendFeedbackTapped(textState.text)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            enabled = sendFeedbackRequestState is Loadable.Uninitialized && textState.text.isNotBlank()
        ) {
            Text(text = stringResource(R.string.submit_feedback))
        }
    }
}

@Preview("Feedback form light theme")
@Composable
fun PreviewFeedbackFormLight() {
    ThemedPreview(darkTheme = false) {
        FeedbackForm(Loadable.Uninitialized, 10.dp) {}
    }
}

@Preview("Feedback form dark theme")
@Composable
fun PreviewFeedbackFormDark() {
    ThemedPreview(darkTheme = true) {
        FeedbackForm(Loadable.Uninitialized, 10.dp) {}
    }
}
