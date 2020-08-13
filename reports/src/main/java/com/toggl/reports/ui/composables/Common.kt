package com.toggl.reports.ui.composables

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.common.feature.compose.theme.grid_3

@Composable
internal fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
internal fun GroupHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = grid_2) then modifier
    )
}

@Composable
internal fun SectionDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier.padding(
            top = grid_3,
            bottom = grid_2
        )
    )
}
