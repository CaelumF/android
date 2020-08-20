package com.toggl.common

import java.time.Duration

object Constants {
    const val timeEntryDeletionDelayMs = 5000L
    const val elapsedTimeIndicatorUpdateDelayMs = 1000L

    object Vibration {
        const val defaultDurationInMillis = 10L
        val oldApisVibrationPattern = longArrayOf(0, 10)

        const val tickDurationInMillis = 5L
        const val tickAmplitude = 5
        val oldApisTickVibrationPattern = longArrayOf(0, 5)
    }

    object TimeEntry {
        const val maxDurationInHours = 999L
        val defaultTimeEntryDuration: Duration = Duration.ofMinutes(30)
    }

    object AutoCompleteSuggestions {
        const val projectToken: Char = '@'
        const val tagToken: Char = '#'
    }

    object Suggestions {
        const val maxNumberOfSuggestions = 3
        const val maxNumberOfCalendarSuggestions = 1
        const val maxNumberOfMostUsedSuggestions = 3
    }

    object ClockMath {
        const val quarterOfCircle: Double = 0.5f * kotlin.math.PI
        const val fullCircle: Double = 2 * kotlin.math.PI
        const val hoursOnTheClock: Int = 12
        const val hoursInTheDay: Int = 24
        const val minutesInAnHour: Int = 60
        const val secondsInAMinute: Int = 60
        const val secondsInAnHour: Int = 3600
    }

    object DefaultCustomColor {
        const val hue = 210F
        const val saturation = .74F
        const val value = .75F
        const val hex = "#3178BE"
    }

    object ColorValue {
        const val min = 0.3F
        const val max = 1F
    }

    object Calendar {
        const val datePickerRowItemsCount = 7L
        object Events {
            const val numberOfMonthsBack = 2L
            const val numberOfMonthsAhead = 2L
        }
    }

    object Reports {
        const val maximumRangeInDays = 365L
    }
}
