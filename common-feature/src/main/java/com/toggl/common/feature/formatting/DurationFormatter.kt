package com.toggl.common.feature.formatting

import com.toggl.common.feature.extensions.totalHours
import com.toggl.common.feature.extensions.totalMinutes
import com.toggl.models.domain.DurationFormat
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class DurationFormatter @Inject constructor(
    private val assets: Assets
) {
    // Seconds in an hour divided by 100
    private val decimalRestDivisionFactor = 36F

    fun format(duration: Duration, format: DurationFormat) =
        when (format) {
            DurationFormat.Classic -> convertToClassicFormat(duration)
            DurationFormat.Improved -> convertToImprovedFormat(duration)
            DurationFormat.Decimal -> convertToDecimalFormat(duration)
        }

    fun formatRoundingWithTimeUnit(value: Duration, timeUnit: ChronoUnit): String {
        fun Float.roundedWithUnit(unit: String) = String.format("%.0f $unit", this)
        if (timeUnit == ChronoUnit.HOURS)
            return value.totalHours.roundedWithUnit(assets.hourIndicator)
        if (timeUnit == ChronoUnit.MINUTES)
            return value.totalMinutes.roundedWithUnit(assets.minuteIndicator)
        return value.seconds.toFloat().roundedWithUnit(assets.secondsIndicator)
    }

    private fun convertToDecimalFormat(value: Duration): String {
        val hours = value.toHours()
        val rest = ceil(value.minusHours(hours).seconds / decimalRestDivisionFactor).toLong()

        return String.format("%d.%d %s", hours, rest, assets.hourIndicator)
    }

    private fun convertToImprovedFormat(duration: Duration): String {
        val totalHours = duration.toHours()
        val totalMinutes = duration.minusHours(totalHours).toMinutes()
        val totalSeconds = duration.minusHours(totalHours).minusMinutes(totalMinutes).seconds
        return String.format("%d:%02d:%02d", totalHours, totalMinutes, totalSeconds)
    }

    private fun convertToClassicFormat(duration: Duration): String {

        if (duration < Duration.ofMinutes(1))
            return String.format("%02d %s", duration.seconds, assets.secondsIndicator)

        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val seconds = duration.minusHours(hours).minusMinutes(minutes).seconds

        return if (hours == 0L) String.format("%02d:%02d %s", minutes, seconds, assets.minuteIndicator)
        else String.format("%d:%02d:%02d %s", hours, minutes, seconds, assets.hourIndicator)
    }

    data class Assets(
        val hourIndicator: String,
        val minuteIndicator: String,
        val secondsIndicator: String
    )
}
