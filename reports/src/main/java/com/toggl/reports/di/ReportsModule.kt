package com.toggl.reports.di

import android.content.Context
import androidx.core.content.ContextCompat
import com.toggl.common.extensions.toHex
import com.toggl.common.feature.R
import com.toggl.reports.domain.LoadReportsEffect
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ReportsModule {
    @Provides
    @Singleton
    fun loadReportEffectAssets(
        @ApplicationContext context: Context
    ) = LoadReportsEffect.NeededAssets(
        context.getString(R.string.no_project),
        ContextCompat.getColor(context, R.color.no_project_color).toHex(),
        context.getString(R.string.reports_error_offline),
        context.getString(R.string.reports_error_generic)
    )
}
