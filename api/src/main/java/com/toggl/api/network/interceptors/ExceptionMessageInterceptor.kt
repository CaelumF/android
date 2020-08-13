package com.toggl.api.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionMessageInterceptor @Inject constructor() : Interceptor {

    private val maxBodyBytesToRead = 500 * 1000L

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.isSuccessful) return originalResponse

        val responseBody = originalResponse.peekBody(maxBodyBytesToRead).string()
        Log.e("xxaa", "got response body $responseBody")

        // return if (responseBody != null) originalResponse.newBuilder().message(responseBody).build() else originalResponse
        return originalResponse.newBuilder().message(responseBody).build()
    }
}
