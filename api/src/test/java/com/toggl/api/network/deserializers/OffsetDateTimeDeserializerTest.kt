package com.toggl.api.network.deserializers

import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeDeserializerTest {
    @Test
    fun `the OffsetDateTime is properly deserialized`() {
        val deserializedTime = OffsetDateTimeAdapter().fromJson("2020-07-28T16:03:16+00:00")
        deserializedTime shouldBe OffsetDateTime.of(2020, 7, 28, 16, 3, 16, 0, ZoneOffset.UTC)
    }
}
