package com.toggl.api.clients

import com.toggl.api.network.models.sync.PullResponse
import com.toggl.api.network.models.sync.PushResponse
import java.time.OffsetDateTime
import java.util.UUID

interface SyncApiClient {
    suspend fun pull(since: OffsetDateTime? = null): PullResponse
    suspend fun push(uuid: UUID): PushResponse
}
