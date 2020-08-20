package com.toggl.common.feature.extensions

import com.toggl.common.feature.formatting.DurationFormatter
import com.toggl.models.domain.DurationFormat
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.stream.Stream

@DisplayName("The duration formatter")
class DurationFormatterTests {

    private val assets = DurationFormatter.Assets("h", "min", "s")
    private val formatter = DurationFormatter(assets)

    @ParameterizedTest
    @MethodSource
    fun `formats adequately respecting specified duration format`(testData: TestData) {
        formatter.format(testData.duration, testData.format) shouldBe testData.expectedOutput
    }

    @ParameterizedTest
    @MethodSource
    fun `formats rounding with the specified time unit`(testData: RoundTimeUnitTestData) {
        formatter.formatRoundingWithTimeUnit(testData.duration, testData.chronoUnit) shouldBe testData.expectedOutput
    }

    data class TestData(val duration: Duration, val format: DurationFormat, val expectedOutput: String)
    data class RoundTimeUnitTestData(val duration: Duration, val chronoUnit: ChronoUnit, val expectedOutput: String)

    companion object {
        private val durationFromSettings = Duration.ofMinutes(47).plusSeconds(6)

        @JvmStatic
        fun `formats adequately respecting specified duration format`() = Stream.of(
            TestData(durationFromSettings, DurationFormat.Classic, "47:06 min"),
            TestData(durationFromSettings, DurationFormat.Improved, "0:47:06"),
            TestData(durationFromSettings, DurationFormat.Decimal, "0.79 h"),
            TestData(durationFromSettings.plusHours(1), DurationFormat.Classic, "1:47:06 h"),
            TestData(durationFromSettings.minusMinutes(47), DurationFormat.Classic, "06 s")
        )

        @JvmStatic
        fun `formats rounding with the specified time unit`() = Stream.of(
            RoundTimeUnitTestData(Duration.ofHours(2), ChronoUnit.HOURS, "2 h"),
            RoundTimeUnitTestData(Duration.ofHours(2).plusMinutes(29), ChronoUnit.HOURS, "2 h"),
            RoundTimeUnitTestData(Duration.ofHours(2).plusMinutes(30), ChronoUnit.HOURS, "3 h"),
            RoundTimeUnitTestData(Duration.ofMinutes(30), ChronoUnit.MINUTES, "30 min"),
            RoundTimeUnitTestData(Duration.ofMinutes(30).plusSeconds(29), ChronoUnit.MINUTES, "30 min"),
            RoundTimeUnitTestData(Duration.ofMinutes(30).plusSeconds(30), ChronoUnit.MINUTES, "31 min"),
            RoundTimeUnitTestData(Duration.ofSeconds(10), ChronoUnit.SECONDS, "10 s"),
        )
    }
}
