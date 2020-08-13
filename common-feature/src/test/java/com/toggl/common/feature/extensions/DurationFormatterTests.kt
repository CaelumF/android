package com.toggl.common.feature.extensions

import com.toggl.common.feature.formatting.DurationFormatter
import com.toggl.models.domain.DurationFormat
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
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

    data class TestData(val duration: Duration, val format: DurationFormat, val expectedOutput: String)

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
    }
}
