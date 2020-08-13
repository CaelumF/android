package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiProjectJsonAdapter
import org.junit.jupiter.api.Test

class ApiProjectAdapterTest {
    @Test
    fun `the ApiProject is properly serialized and deserialized`() {
        val adapter = ApiProjectJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":162411463,"workspace_id":4507009,"name":"Visionary systemic strategy","is_private":true,"active":true,"color":"#127e82","billable":true}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
