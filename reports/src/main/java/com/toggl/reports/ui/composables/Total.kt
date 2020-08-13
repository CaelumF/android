package com.toggl.reports.ui.composables

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.reports.R

@Composable
fun Total(total: String) {
    Column {
        GroupHeader(text = stringResource(R.string.tracked_hours))

        SectionHeader(text = stringResource(R.string.total))

        Text(
            text = total,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.fillMaxWidth()
        )
        SectionDivider()
    }
}

@Preview("Total light theme")
@Composable
fun PreviewTotalLight() {
    ThemedPreview {
        Total("30:00:12")
    }
}

@Preview("Total dark theme")
@Composable
fun PreviewTotalDark() {
    ThemedPreview(darkTheme = true) {
        Total("30:00:12")
    }
}
