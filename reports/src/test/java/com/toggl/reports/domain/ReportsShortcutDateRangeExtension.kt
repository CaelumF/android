package com.toggl.reports.domain

import com.toggl.models.common.DateRange
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.stream.Stream

@DisplayName("The dateRange extensions")
class ReportsShortcutDateRangeExtension {

    private val now = OffsetDateTime.of(2020, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC)

    @MethodSource
    @ParameterizedTest
    fun `return the correct date range related to the current date`(testData: TestData) {
        testData.shortcut.dateRange(now, DayOfWeek.MONDAY) shouldBe testData.expectedRange
    }

    data class TestData(val shortcut: ReportsShortcut, val expectedRange: DateRange)

    companion object {
        @JvmStatic
        fun `return the correct date range related to the current date`() = Stream.of(
            TestData(ReportsShortcut.Today, DateRange(OffsetDateTime.of(2020, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC), null)),
            TestData(ReportsShortcut.Yesterday, DateRange(OffsetDateTime.of(2020, 3, 31, 0, 0, 0, 0, ZoneOffset.UTC), null)),
            TestData(ReportsShortcut.ThisWeek, DateRange(OffsetDateTime.of(2020, 3, 30, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 4, 5, 0, 0, 0, 0, ZoneOffset.UTC))),
            TestData(ReportsShortcut.LastWeek, DateRange(OffsetDateTime.of(2020, 3, 23, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 3, 29, 0, 0, 0, 0, ZoneOffset.UTC))),
            TestData(ReportsShortcut.ThisMonth, DateRange(OffsetDateTime.of(2020, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 4, 30, 0, 0, 0, 0, ZoneOffset.UTC))),
            TestData(ReportsShortcut.LastMonth, DateRange(OffsetDateTime.of(2020, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 3, 31, 0, 0, 0, 0, ZoneOffset.UTC))),
            TestData(ReportsShortcut.ThisYear, DateRange(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC))),
            TestData(ReportsShortcut.LastYear, DateRange(OffsetDateTime.of(2019, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2019, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)))
        )
    }
}
