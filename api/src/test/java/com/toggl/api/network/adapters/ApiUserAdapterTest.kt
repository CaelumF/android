package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiUserJsonAdapter
import org.junit.jupiter.api.Test

class ApiUserAdapterTest {
    @Test
    fun `the ApiUser is properly serialized and deserialized`() {
        val adapter = ApiUserJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":49385665,"api_token":"TbmWVAtFNIjFGTnG","beginning_of_week":1,"default_workspace_id":4507009,"email":"Jovani.Romaguera.BvF@toggl.com","fullname":"Jovani Romaguera"}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
