package com.toggl.common.feature.extensions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Duration

internal class DurationExtensionsKtTest {

    @Test
    fun totalHours() {
        Duration.ofHours(1).totalHours.shouldBe(1f)
        Duration.ofHours(2).totalHours.shouldBe(2f)
        Duration.ofHours(3).totalHours.shouldBe(3f)
        Duration.ofHours(1).plusMinutes(30).totalHours.shouldBe(1.5f)
        Duration.ofSeconds(360).totalHours.shouldBe(0.1f)
    }

    @Test
    fun totalMinutes() {
        Duration.ofHours(1).totalMinutes.shouldBe(60f)
        Duration.ofHours(2).totalMinutes.shouldBe(120f)
        Duration.ofHours(3).totalMinutes.shouldBe(180f)
        Duration.ofHours(1).plusMinutes(30).totalMinutes.shouldBe(90f)
        Duration.ofSeconds(360).totalMinutes.shouldBe(6f)
        Duration.ofSeconds(90).totalMinutes.shouldBe(1.5f)
    }
}
