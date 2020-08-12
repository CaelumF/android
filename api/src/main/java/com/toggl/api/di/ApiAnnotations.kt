package com.toggl.api.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class BaseUrl

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class BaseApiUrl

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class BaseSyncUrl

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class BaseReportsUrl

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class ApiRetrofit

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class SyncRetrofit

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class ReportsRetrofit
