package com.toggl.common.extensions

import android.graphics.drawable.Drawable

fun Drawable.scaleToLineHeight(lineHeight: Int) =
    (lineHeight.toDouble() / this.intrinsicHeight.toDouble()).let {
        this.setBounds(
            0,
            0,
            (this.intrinsicWidth * it).toInt(),
            (this.intrinsicHeight * it).toInt()
        )
    }
