package com.toggl.api.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.toggl.api.models.ApiUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

internal interface AuthenticationApi {
    @GET("me")
    suspend fun login(@Header("Authorization") authHeader: String): ApiUser

    @POST("me/lost_passwords")
    suspend fun resetPassword(@Body passwordForgottenData: ResetPasswordBody): String

    @POST("signup")
    suspend fun signUp(@Body signUpData: SignUpBody): ApiUser
}

@JsonClass(generateAdapter = true)
data class ResetPasswordBody(val email: String)

@JsonClass(generateAdapter = true)
data class WorkspaceSignUpBody(
    val name: String? = null,
    @Json(name = "initial_pricing_plan") val initialPricingPlan: Int = 0
)

@JsonClass(generateAdapter = true)
data class SignUpBody(
    val email: String,
    val password: String,
    val timezone: String?,
    val workspace: WorkspaceSignUpBody = WorkspaceSignUpBody(),
    @Json(name = "tos_accepted") val tosAccepted: Boolean = true
)
