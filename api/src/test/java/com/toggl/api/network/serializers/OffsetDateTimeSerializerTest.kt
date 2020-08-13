package com.toggl.api.network.serializers

import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeSerializerTest {
    @Test
    fun `the OffsetDateTime is properly serialized`() {
        val time = OffsetDateTime.of(2012, 1, 2, 3, 4, 5, 0, ZoneOffset.MAX)
        val serializedTime = OffsetDateTimeAdapter().toJson(time)
        serializedTime shouldBe "2012-01-02T03:04:05+18:00"
    }
}
