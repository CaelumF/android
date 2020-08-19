package com.toggl.api.network.models.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.internal.Util
import java.lang.IllegalStateException
import java.lang.reflect.Type

@JsonClass(generateAdapter = false)
sealed class ActionResult<out T>

data class SuccessResult<out T>(val result: T?) : ActionResult<T>()

data class ErrorResult<out T>(val result: SyncError) : ActionResult<T>()

@JsonClass(generateAdapter = true)
data class SyncError(
    val code: Long,
    @Json(name = "default_message")
    val defaultMessage: String
)

class ActionResultJsonAdapter<T>(
    moshi: Moshi,
    types: Array<Type>
) : JsonAdapter<ActionResult<T>>() {
    private val successOptions = JsonReader.Options.of("success")
    private val options: JsonReader.Options = JsonReader.Options.of("result")
    private val syncErrorAdapter: JsonAdapter<SyncError> = moshi.adapter(SyncError::class.java, emptySet(), "result")
    private val payloadAdapter: JsonAdapter<T> = moshi.adapter(types[0], emptySet(), "result")

    override fun toString(): String = buildString(33) {
        append("GeneratedJsonAdapter(").append("ErrorResult").append(')') }

    override fun fromJson(reader: JsonReader): ActionResult<T> {
        reader.beginObject()

        val peeked = reader.peekJson()
        peeked.setFailOnUnknown(false)
        val success = peeked.findSuccessOption()

        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> return if (success) SuccessResult(result = payloadAdapter.fromJson(reader))
                            else ErrorResult(result = syncErrorAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "result",
                    "result", reader
                ))
                else -> continue
            }
        }

        throw IllegalStateException()
    }

    fun JsonReader.findSuccessOption(): Boolean {
        use {
            it.beginObject()
            while (it.hasNext()) {
                if (it.selectName(successOptions) == -1) {
                    it.skipName()
                    it.skipValue()
                    continue
                }


                return it.nextBoolean()
            }
        }

        throw IllegalStateException()
    }

    override fun toJson(writer: JsonWriter, value: ActionResult<T>?) {
        if (value == null) {
            throw NullPointerException("value was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name("result")

        when (value) {
            is SuccessResult -> payloadAdapter.toJson(writer, value.result)
            is ErrorResult -> syncErrorAdapter.toJson(writer, value.result)
        }

        writer.endObject()
    }
}