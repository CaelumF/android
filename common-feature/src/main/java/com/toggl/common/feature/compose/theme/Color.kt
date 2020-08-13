package com.toggl.common.feature.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.toggl.common.extensions.adjustForUserTheme

val Pink = Color(0xeee57cd8)
val PinkDark = Color(0xdddd6fd1)
val PurpleDeep = Color(0xff000000)
val PurpleDark = Color(0xff231d24)
val Yellow = Color(0xffffde91)
val RedLight = Color(0xffff453a)
val RedDark = Color(0xffe20505)
val White = Color(0xfffffbfa)
val WhiteAlpine = Color(0xfff6eef0)

@Composable
fun String.adjustForUserTheme(): Color =
    Color(adjustForUserTheme(isSystemInDarkTheme()))
