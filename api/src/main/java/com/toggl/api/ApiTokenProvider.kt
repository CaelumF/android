package com.toggl.api

import com.toggl.models.validation.ApiToken

interface ApiTokenProvider {
    fun getApiToken(): ApiToken

    companion object {
        const val apiToken = "apiToken"
    }
}
