package com.toggl.api.network

import com.toggl.api.network.models.sync.PullResponse
import com.toggl.api.network.models.sync.PushBody
import com.toggl.api.network.models.sync.PushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncApi {
    @GET("/pull")
    suspend fun pull(@Query("since") unixSeconds: Long?): PullResponse

    @POST("/push/{identifier}")
    suspend fun push(identifier: String, @Body body: PushBody): PushResponse
}
