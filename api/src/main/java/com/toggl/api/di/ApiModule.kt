package com.toggl.api.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.toggl.api.clients.ErrorHandlingProxyClient
import com.toggl.api.clients.ReportsApiClient
import com.toggl.api.clients.SyncApiClient
import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.api.clients.feedback.FeedbackApiClient
import com.toggl.api.extensions.AppBuildConfig
import com.toggl.api.network.AuthenticationApi
import com.toggl.api.network.FeedbackApi
import com.toggl.api.network.ReportsApi
import com.toggl.api.network.SyncApi
import com.toggl.api.network.adapters.DateAdapter
import com.toggl.api.network.adapters.OffsetDateTimeAdapter
import com.toggl.api.network.interceptors.AuthInterceptor
import com.toggl.api.network.interceptors.SyncInterceptor
import com.toggl.api.network.interceptors.UserAgentInterceptor
import com.toggl.api.network.models.sync.ActionResult
import com.toggl.api.network.models.sync.ErrorResult
import com.toggl.api.network.models.sync.SuccessResult
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Provides
    @Singleton
    @BaseUrl
    fun baseEndpointUrl(): String =
        if (AppBuildConfig.isBuildTypeRelease) "https://mobile.track.toggl.com"
        else "https://mobile.track.toggl.space"

    @Provides
    @Singleton
    @BaseApiUrl
    fun baseApiUrl(@BaseUrl baseUrl: String): String = "$baseUrl/api/v9/"

    @Provides
    @Singleton
    @BaseReportsUrl
    fun baseReportsUrl(@BaseUrl baseUrl: String): String = "$baseUrl/reports/api/v3/"

    @Provides
    @Singleton
    @BaseSyncUrl
    fun baseSyncUrl(): String =
        if (AppBuildConfig.isBuildTypeRelease) "https://sync.toggl.com"
        else "https://sync.toggl.space"

    @Provides
    @Singleton
    fun okHttpClient(
        userAgentInterceptor: UserAgentInterceptor,
        authInterceptor: AuthInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    fun moshi() = Moshi.Builder()
        .add(OffsetDateTimeAdapter())
        .add(DateAdapter())
        .add(
            PolymorphicJsonAdapterFactory.of(ActionResult::class.java, "success")
                .withSubtype(SuccessResult::class.java, "true")
                .withSubtype(ErrorResult::class.java, "false")
        )
        .build()

    @Provides
    @Singleton
    @ApiRetrofit
    fun apiRetrofit(
        @BaseApiUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @SyncRetrofit
    fun syncRetrofit(
        @BaseSyncUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        syncInterceptor: SyncInterceptor,
        moshi: Moshi
    ) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient.newBuilder().addInterceptor(syncInterceptor).build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @ReportsRetrofit
    fun reportsRetrofit(
        @BaseReportsUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    internal fun feedbackApi(@ApiRetrofit retrofit: Retrofit) =
        retrofit.create(FeedbackApi::class.java)

    @Provides
    @Singleton
    internal fun authenticationApi(@ApiRetrofit retrofit: Retrofit) =
        retrofit.create(AuthenticationApi::class.java)

    @Provides
    @Singleton
    internal fun reportsApi(@ReportsRetrofit retrofit: Retrofit) =
        retrofit.create(ReportsApi::class.java)

    @Provides
    @Singleton
    internal fun syncApi(@SyncRetrofit retrofit: Retrofit) =
        retrofit.create(SyncApi::class.java)
}

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class ApiClientModule {
    @Binds
    abstract fun feedbackApiClient(bind: ErrorHandlingProxyClient): FeedbackApiClient

    @Binds
    abstract fun authenticationApiClient(bind: ErrorHandlingProxyClient): AuthenticationApiClient

    @Binds
    abstract fun reportsApiClient(bind: ErrorHandlingProxyClient): ReportsApiClient

    @Binds
    abstract fun syncApiClient(bind: ErrorHandlingProxyClient): SyncApiClient
}
