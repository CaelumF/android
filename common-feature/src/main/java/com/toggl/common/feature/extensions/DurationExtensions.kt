package com.toggl.common.feature.extensions

import com.toggl.common.Constants
import java.time.Duration
import java.util.concurrent.TimeUnit

fun Duration.formatForDisplaying(): String {
    val durationMillis = toMillis()
    return String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(durationMillis),
        TimeUnit.MILLISECONDS.toMinutes(durationMillis) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(durationMillis) % TimeUnit.MINUTES.toSeconds(1)
    )
}

val Duration.totalHours: Float
    get() = (seconds.toFloat() / Constants.ClockMath.secondsInAnHour)

val Duration.totalMinutes: Float
    get() = (seconds.toFloat() / Constants.ClockMath.secondsInAMinute)