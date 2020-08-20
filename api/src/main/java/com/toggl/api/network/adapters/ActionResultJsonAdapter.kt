package com.toggl.api.network.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.internal.Util
import com.toggl.api.network.models.sync.ActionResult
import com.toggl.api.network.models.sync.SyncError
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ActionResultJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (rawType != ActionResult::class.java)
            return null

        val parameterizedType = type as? ParameterizedType ?: return null

        @Suppress("UNCHECKED_CAST")
        val adapterClass: Class<out JsonAdapter<*>?> = Class.forName(
            ActionResultJsonAdapter::class.java.name,
            true,
            rawType.classLoader
        ) as Class<out JsonAdapter<*>?>

        val typeArgs = parameterizedType.actualTypeArguments
        val constructor = adapterClass.getDeclaredConstructor(Moshi::class.java, Array<Type>::class.java)
        val args = arrayOf(moshi, typeArgs)

        constructor.isAccessible = true
        return constructor.newInstance(*args)?.nullSafe()
    }
}

class ActionResultJsonAdapter<T>(
    moshi: Moshi,
    types: Array<Type>
) : JsonAdapter<ActionResult<T>>() {
    private val successOptions = JsonReader.Options.of("success")
    private val options: JsonReader.Options = JsonReader.Options.of("result")
    private val syncErrorAdapter: JsonAdapter<SyncError> = moshi.adapter(SyncError::class.java, emptySet(), "result")
    private val payloadAdapter: JsonAdapter<T> = moshi.adapter(types[0], emptySet(), "result")

    override fun toString(): String = buildString(33) {
        append("GeneratedJsonAdapter(").append("ErrorResult").append(')')
    }

    override fun fromJson(reader: JsonReader): ActionResult<T> {
        val peeked = reader.peekJson()
        peeked.setFailOnUnknown(false)
        val success = peeked.findSuccessOption()

        var actionResult: ActionResult<T>? = null
        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    actionResult =
                        if (success) ActionResult.EntitySuccessResult(result = payloadAdapter.fromJson(reader))
                        else ActionResult.ErrorResult(
                            result = syncErrorAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                                "result",
                                "result", reader
                            )
                        )
                }
                else -> {
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        return actionResult ?: if (success) ActionResult.SuccessResult() else throw IllegalStateException()
    }

    private fun JsonReader.findSuccessOption(): Boolean {
        var result: Boolean? = null
        use {
            it.beginObject()

            while (it.hasNext()) {
                if (it.selectName(successOptions) == -1) {
                    it.skipName()
                    it.skipValue()
                    continue
                }

                result = it.nextBoolean()
            }

            it.endObject()
        }

        return result ?: throw IllegalStateException()
    }

    override fun toJson(writer: JsonWriter, value: ActionResult<T>?) =
        throw IllegalStateException("Can't serialize ActionResults")
}
