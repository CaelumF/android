package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiTimeEntryJsonAdapter
import org.junit.jupiter.api.Test

class ApiTimeEntryAdapterTest {
    @Test
    fun `the ApiTimeEntry is properly serialized and deserialized`() {
        val adapter = ApiTimeEntryJsonAdapter(Moshi.Builder().add(OffsetDateTimeAdapter()).build())
        testJsonAdapter(
            """{"id":49385665,"billable":true,"description":"Description","duration":123,"project_id":34343,"start":"2020-04-29T23:57:36Z","tag_ids":[1,2,3],"task_id":1,"user_id":10,"workspace_id":100}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
