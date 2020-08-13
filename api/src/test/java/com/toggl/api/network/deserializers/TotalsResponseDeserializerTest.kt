package com.toggl.api.network.deserializers

import com.squareup.moshi.Moshi
import com.toggl.api.models.Resolution
import com.toggl.api.network.models.reports.GraphItem
import com.toggl.api.network.models.reports.RateInfo
import com.toggl.api.network.models.reports.TotalsResponse
import com.toggl.api.network.models.reports.TotalsResponseJsonAdapter
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The TotalsResponse deserializer")
class TotalsResponseDeserializerTest {
    @Test
    fun `properly deserializes the response from the totals endpoint`() {
        val jsonSerializer = TotalsResponseJsonAdapter(Moshi.Builder().build())
        jsonSerializer.fromJson(validResponseJson) shouldBe TotalsResponse(
            seconds = 568,
            resolution = Resolution.Day,
            graph = listOf(
                GraphItem(12, mapOf("0" to 2108L, "1" to 28561L)),
                GraphItem(269, mapOf("0" to 548L, "1" to 285L))
            ),
            rates = listOf(
                RateInfo(
                    billableSeconds = 410,
                    hourlyRateInCents = 1000,
                    currency = "USD"
                ),
                RateInfo(
                    billableSeconds = 97841,
                    hourlyRateInCents = 654,
                    currency = "EUR"
                ),
            )
        )
    }

    companion object {
        @Language("JSON")
        private const val validResponseJson =
            """{"seconds":568,"rates":[{"billable_seconds": 410, "hourly_rate_in_cents":1000, "currency": "USD"},{"billable_seconds": 97841, "hourly_rate_in_cents":654, "currency": "EUR"}],"resolution":"day","graph":[{"seconds":12,"by_rate":{"0":2108,"1":28561}},{"seconds":269,"by_rate":{"0":548,"1":285}}]}"""
    }
}
