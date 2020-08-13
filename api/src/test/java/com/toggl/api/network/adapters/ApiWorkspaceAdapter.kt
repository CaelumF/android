package com.toggl.api.network.adapters

import com.squareup.moshi.Moshi
import com.toggl.api.common.testJsonAdapter
import com.toggl.api.models.ApiWorkspaceJsonAdapter
import org.junit.jupiter.api.Test

class ApiWorkspaceAdapter {
    @Test
    fun `the ApiWorkspace is properly serialized and deserialized`() {
        val adapter = ApiWorkspaceJsonAdapter(Moshi.Builder().build())
        testJsonAdapter(
            """{"id":49385665,"name":"My Workspace","admin":true,"only_admins_may_create_projects":true,"projects_billable_by_default":true}""",
            fromJson = adapter::fromJson,
            toJson = adapter::toJson
        )
    }
}
