package com.toggl.api.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncInterceptor @Inject constructor() : Interceptor {
    // TODO: Change this to mobile once the issues in the sync server are fixed
    private val syncClientName = "web"

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader("X-Toggl-Client", syncClientName)
                .build()
        )
}
