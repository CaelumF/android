package com.toggl.models.domain

import com.squareup.moshi.Json
import java.time.DayOfWeek

data class UserPreferences(
    val manualModeEnabled: Boolean,
    val twentyFourHourClockEnabled: Boolean,
    val groupSimilarTimeEntriesEnabled: Boolean,
    val cellSwipeActionsEnabled: Boolean,
    val calendarIntegrationEnabled: Boolean,
    val calendarIds: List<String>,
    val dateFormat: DateFormat,
    val durationFormat: DurationFormat,
    val firstDayOfTheWeek: DayOfWeek,
    val smartAlertsOption: SmartAlertsOption
) {
    companion object {
        val default = UserPreferences(
            manualModeEnabled = false,
            twentyFourHourClockEnabled = false,
            groupSimilarTimeEntriesEnabled = true,
            cellSwipeActionsEnabled = true,
            calendarIntegrationEnabled = false,
            calendarIds = emptyList(),
            dateFormat = DateFormat.DDMMYYYY_dash,
            durationFormat = DurationFormat.Improved,
            firstDayOfTheWeek = DayOfWeek.MONDAY,
            smartAlertsOption = SmartAlertsOption.Disabled
        )
    }
}

enum class DateFormat(val label: String) {
    @Json(name = "MM/DD/YYYY") MMDDYYYY_slash("MM/DD/YYYY"),
    @Json(name = "DD-MM-YYYY") DDMMYYYY_dash("DD-MM-YYYY"),
    @Json(name = "MM-DD-YYYY") MMDDYYYY_dash("MM-DD-YYYY"),
    @Json(name = "YYYY-MM-DD") YYYYMMDD_dash("YYYY-MM-DD"),
    @Json(name = "DD/MM/YYYY") DDMMYYYY_slash("DD/MM/YYYY"),
    @Json(name = "DD.MM.YYYY") DDMMYYYY_dot("DD.MM.YYYY")
}

enum class DurationFormat {
    @Json(name = "classic") Classic,
    @Json(name = "improved") Improved,
    @Json(name = "decimal") Decimal
}

enum class SmartAlertsOption {
    Disabled,
    WhenEventStarts,
    MinutesBefore5,
    MinutesBefore10,
    MinutesBefore15,
    MinutesBefore30,
    MinutesBefore60,
}

// debug
enum class MockDataSetSize(val label: String, val amount: Int) {
    Clean("Clean", 0),
    Percentile50("50% - 67 TEs", 67),
    Percentile80("80% - 162 TEs", 162),
    Percentile90("90% - 274 TEs", 274),
    Percentile99("99% - 866 TEs", 866),
    Percentile995("99.5% - 1140 TEs", 1140),
    Percentile999("99.9% - 2760 TEs", 2760),
}
