package com.toggl.common.feature.di

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.toggl.api.di.ApiModule
import com.toggl.common.feature.R
import com.toggl.common.feature.formatting.DurationFormatter
import com.toggl.common.feature.services.analytics.AnalyticsService
import com.toggl.common.feature.services.analytics.AppCenterAnalyticsService
import com.toggl.common.feature.services.analytics.CompositeAnalyticsService
import com.toggl.common.feature.services.analytics.FirebaseAnalyticsService
import com.toggl.common.feature.services.calendar.CalendarService
import com.toggl.common.feature.services.calendar.CursorCalendarService
import com.toggl.common.services.permissions.LollipopPermissionRequesterService
import com.toggl.common.services.permissions.MarshmallowPermissionRequesterService
import com.toggl.common.services.permissions.PermissionCheckerService
import com.toggl.common.services.permissions.PermissionRequesterService
import com.toggl.common.services.time.JavaTimeService
import com.toggl.common.services.time.TimeService
import com.toggl.models.domain.InstallLocation
import com.toggl.models.domain.PlatformInfo
import com.toggl.repository.interfaces.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Singleton

@Module(includes = [ ApiModule::class ])
@InstallIn(ApplicationComponent::class)
object CommonFeatureModule {
    @Provides
    @Singleton
    fun timeService(): TimeService = JavaTimeService()

    @Provides
    @Singleton
    fun analyticsService(
        firebaseAnalyticsService: FirebaseAnalyticsService,
        appCenterAnalyticsService: AppCenterAnalyticsService
    ): AnalyticsService = CompositeAnalyticsService(
        firebaseAnalyticsService,
        appCenterAnalyticsService
    )

    @Provides
    @Singleton
    fun calendarService(@ApplicationContext context: Context, settingsRepository: SettingsRepository): CalendarService =
        CursorCalendarService(context, settingsRepository)

    @Provides
    @Singleton
    fun permissionCheckerService(@ApplicationContext context: Context): PermissionCheckerService =
        object : PermissionCheckerService {
            override fun hasCalendarPermission(): Boolean {
                val calendarPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                return calendarPermission == PackageManager.PERMISSION_GRANTED
            }
        }

    @Provides
    fun durationFormatterAssets(@ApplicationContext context: Context) =
        DurationFormatter.Assets(
            context.getString(R.string.unit_hour),
            context.getString(R.string.unit_minute),
            context.getString(R.string.unit_second)
        )

    @Provides
    fun platformInfo(@ApplicationContext context: Context): PlatformInfo {
        val language = Locale.getDefault().displayLanguage
        val timezone = TimeZone.getDefault()
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName

        @Suppress("DEPRECATION")
        val build = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) packageInfo.versionCode.toString()
        else packageInfo.longVersionCode.toString()
        val model = Build.MODEL
        val osVersion = Build.VERSION.RELEASE
        val installLocation = when {
            packageInfo.applicationInfo == null -> InstallLocation.Unknown
            packageInfo.applicationInfo.flags.and(ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE -> InstallLocation.External
            else -> InstallLocation.Internal
        }
        return PlatformInfo(
            language,
            timezone.id,
            versionName,
            build,
            model,
            osVersion,
            installLocation
        )
    }
}

@Module
@InstallIn(ActivityComponent::class)
object CommonFeatureActivityModule {
    @Provides
    fun permissionRequesterService(activity: Activity): PermissionRequesterService =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) LollipopPermissionRequesterService()
        else MarshmallowPermissionRequesterService(activity as AppCompatActivity)
}
