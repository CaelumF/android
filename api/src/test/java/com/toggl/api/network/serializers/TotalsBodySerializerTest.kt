package com.toggl.api.network.serializers

import com.squareup.moshi.Moshi
import com.toggl.api.network.adapters.DateAdapter
import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import com.toggl.api.network.models.reports.TotalsBody
import com.toggl.api.network.models.reports.TotalsBodyJsonAdapter
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DisplayName("The TotalsBody serializer")
class TotalsBodySerializerTest {
    @Test
    fun `turns the TotalsBody object into the json the api expects`() {

        val expectedJson =
            """{"start_date":"2020-06-29","end_date":"2020-07-05","user_ids":[4674715],"with_graph":true}"""
        val totalsBody = TotalsBody(
            startDate = OffsetDateTime.of(2020, 6, 29, 0, 0, 0, 0, ZoneOffset.UTC),
            endDate = OffsetDateTime.of(2020, 7, 5, 3, 0, 0, 0, ZoneOffset.UTC),
            userIds = listOf(4674715),
            withGraph = true
        )
        val moshi = Moshi.Builder().add(DateAdapter()).add(OffsetDateTimeAdapter()).build()
        val jsonSerializer = TotalsBodyJsonAdapter(moshi)

        val json = jsonSerializer.toJson(totalsBody)

        json shouldBe expectedJson
    }
}
