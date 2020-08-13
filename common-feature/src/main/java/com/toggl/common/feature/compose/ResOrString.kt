package com.toggl.common.feature.compose

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class ResOrStr {
    data class Res(@StringRes val stringRes: Int) : ResOrStr()
    data class Str(val string: String) : ResOrStr()
    object Empty : ResOrStr()
}

fun ResOrStr.toStr(context: Context) =
    when (this) {
        is ResOrStr.Res -> context.getString(this.stringRes)
        is ResOrStr.Str -> this.string
        ResOrStr.Empty -> ""
    }

@Composable
fun toStr(resOrStr: ResOrStr): String =
    when (resOrStr) {
        is ResOrStr.Res -> stringResource(resOrStr.stringRes)
        is ResOrStr.Str -> resOrStr.string
        ResOrStr.Empty -> ""
    }
