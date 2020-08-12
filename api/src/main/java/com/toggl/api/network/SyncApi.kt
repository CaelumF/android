package com.toggl.api.network

import com.toggl.api.network.models.pull.PullResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SyncApi {
    @GET("/pull")
    suspend fun pull(@Query("since") unixSeconds: Long?): PullResponse
}
