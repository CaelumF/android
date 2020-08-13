package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiTagJsonAdapter
import org.junit.jupiter.api.Test

class ApiTagAdapterTest {
    @Test
    fun `the ApiTag is properly serialized and deserialized`() {
        val adapter = ApiTagJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":49385665,"name":"ab ut consequatur","workspace_id":223}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
