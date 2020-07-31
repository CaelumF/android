package com.toggl.api.network.serializers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.toggl.api.network.models.feedback.FeedbackBody
import java.lang.reflect.Type

internal class FeedbackBodySerializer : JsonSerializer<FeedbackBody> {

    override fun serialize(src: FeedbackBody, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val feedbackBodyJson = JsonObject()
        feedbackBodyJson.addProperty("email", src.email)
        feedbackBodyJson.addProperty("message", src.message)
        val data = JsonArray()
        for (entry in src.data) {
            val keyValue = JsonObject().apply {
                addProperty("key", entry.key)
                addProperty("value", entry.value)
            }
            data.add(keyValue)
        }
        feedbackBodyJson.add("data", data)
        return feedbackBodyJson
    }
}
