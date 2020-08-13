package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiClientJsonAdapter
import org.junit.jupiter.api.Test

class ApiClientAdapterTest {
    @Test
    fun `the ApiClient is properly serialized and deserialized`() {
        val adapter = ApiClientJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":49385665,"name":"Stanton-Stanton","wid":4507009}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
