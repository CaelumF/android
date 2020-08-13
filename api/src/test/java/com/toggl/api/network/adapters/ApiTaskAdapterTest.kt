package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiTaskJsonAdapter
import org.junit.jupiter.api.Test

class ApiTaskAdapterTest {
    @Test
    fun `the ApiTask is properly serialized and deserialized`() {
        val adapter = ApiTaskJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":49385665,"name":"ab ut consequatur","active":true,"project_id":23123,"user_id":32323,"workspace_id":223}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
