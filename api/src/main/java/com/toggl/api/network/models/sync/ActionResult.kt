package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.internal.Util

interface ActionResult<out T>

@JsonClass(generateAdapter = true)
data class SuccessResult<out T>(val result: T?) : ActionResult<T>

@JsonClass(generateAdapter = false)
data class ErrorResult<out T>(val result: SyncError) : ActionResult<T>

@JsonClass(generateAdapter = true)
data class SyncError(
    val code: Long,
    @Json(name = "default_message")
    val defaultMessage: String
)

class ErrorResultJsonAdapter<T>(moshi: Moshi) : JsonAdapter<ErrorResult<T>>() {
    private val options: JsonReader.Options = JsonReader.Options.of("result")

    private val syncErrorAdapter: JsonAdapter<SyncError> = moshi.adapter(SyncError::class.java,
        emptySet(), "result")

    override fun toString(): String = buildString(33) {
        append("GeneratedJsonAdapter(").append("ErrorResult").append(')') }

    override fun fromJson(reader: JsonReader): ErrorResult<T> {
        var result: SyncError? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> result = syncErrorAdapter.fromJson(reader) ?: throw Util.unexpectedNull("result",
                    "result", reader)
                -1 -> {
                    // Unknown name, skip it.
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        return ErrorResult<T>(
            result = result ?: throw Util.missingProperty("result", "result", reader)
        )
    }

    override fun toJson(writer: JsonWriter, value: ErrorResult<T>?) {
        if (value == null) {
            throw NullPointerException("value was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name("result")
        syncErrorAdapter.toJson(writer, value.result)
        writer.endObject()
    }
}