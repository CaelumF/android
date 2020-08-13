package com.toggl.api.clients

import com.toggl.api.network.models.sync.PullResponse
import java.time.OffsetDateTime

interface SyncApiClient {
    suspend fun pull(since: OffsetDateTime? = null): PullResponse
}
