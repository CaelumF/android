package com.toggl.common.feature.compose

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.toggl.common.feature.compose.theme.TogglTheme

@Composable
fun ThemedPreview(
    darkTheme: Boolean = false,
    children: @Composable () -> Unit
) {
    TogglTheme(darkTheme = darkTheme) {
        Surface {
            children()
        }
    }
}
