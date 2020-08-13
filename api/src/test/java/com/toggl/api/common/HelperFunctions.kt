package com.toggl.api.common

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

object TestDataUtils {
    fun getPullResponse(): String {
        return readFromFile2("src/debug/assets/pull_response.json")
    }

    @Throws(IOException::class)
    private fun readFromFile2(filename: String): String =
        File(filename)
            .inputStream()
            .readBytes()
            .toString(Charsets.UTF_8)

    @Throws(IOException::class)
    private fun readFromFile(filename: String): String {
        val stream: InputStream = javaClass.getResourceAsStream(filename) ?: return ""
        val stringBuilder = StringBuilder()
        var i: Int
        val b = ByteArray(4096)
        while (stream.read(b).also { i = it } != -1) {
            stringBuilder.append(String(b, 0, i))
        }
        return stringBuilder.toString()
    }
}

fun <T> testJsonAdapter(@Language("JSON") originalJsonString: String, fromJson: (String) -> T, toJson: (T) -> String) {
    val deserialized: T = fromJson(originalJsonString)
    val serialized = toJson(deserialized)
    serialized shouldBe originalJsonString
}
