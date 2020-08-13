package com.toggl.api.network.serializers

import com.squareup.moshi.Moshi
import com.toggl.api.network.models.feedback.FeedbackBody
import com.toggl.api.network.models.feedback.FeedbackBodyJsonAdapter
import com.toggl.api.network.models.feedback.toKeyValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("FeedbackBody serializer tests")
class FeedbackBodySerializerTest {
    @Test
    fun `the FeedbackBody is properly serialized`() {
        val expectedEmail = "such@email.com"
        val expectedMessage = "expected message"
        val data = mapOf(
            "device" to "SomePhone",
            "some random key" to "some also random value"
        )
        val feedbackBody = FeedbackBody(expectedEmail, expectedMessage, data.toKeyValue())
        val jsonSerializer = FeedbackBodyJsonAdapter(Moshi.Builder().build())

        val json = jsonSerializer.toJson(feedbackBody)

        json shouldBe """{"email":"$expectedEmail","message":"$expectedMessage","data":[{"key":"device","value":"${data["device"]}"},{"key":"some random key","value":"${data["some random key"]}"}]}"""
    }
}
