package com.toggl.reports.ui.composables

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview

@Composable
fun Error(errorMessage: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalGravity = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview("Error light theme")
@Composable
fun PreviewErrorLight() {
    ThemedPreview {
        Error("Something went wrong")
    }
}

@Preview("Error dark theme")
@Composable
fun PreviewErrorDark() {
    ThemedPreview(darkTheme = true) {
        Error("Something went wrong")
    }
}
