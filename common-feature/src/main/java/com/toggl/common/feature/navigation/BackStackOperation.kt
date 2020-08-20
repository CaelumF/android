package com.toggl.common.feature.navigation

import android.net.Uri
import androidx.navigation.NavOptions

internal sealed class BackStackOperation {
    data class Push(val deepLink: Uri, val navOptions: NavOptions?) : BackStackOperation()
    object Pop : BackStackOperation()
}
