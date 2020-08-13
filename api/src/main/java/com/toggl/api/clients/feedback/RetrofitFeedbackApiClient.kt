package com.toggl.api.clients.feedback

import com.toggl.api.network.FeedbackApi
import com.toggl.api.network.models.feedback.FeedbackBody
import com.toggl.api.network.models.feedback.toKeyValue
import com.toggl.models.domain.FeedbackData
import com.toggl.models.domain.PlatformInfo
import com.toggl.models.domain.User
import java.time.OffsetDateTime
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitFeedbackApiClient @Inject constructor(
    private val feedbackApi: FeedbackApi
) : FeedbackApiClient {
    private val unspecified = "unspecified"
    private val never = "never"

    override suspend fun sendFeedback(user: User, message: String, platformInfo: PlatformInfo, feedbackData: FeedbackData) {
        val data = mapOf(
            "PhoneModel" to platformInfo.phoneModel,
            "OperatingSystem" to "Android ${platformInfo.osVersion}",
            "AppNameAndVersion" to "Android Aurora ${platformInfo.version}",
            "DeviceTimeZone" to platformInfo.timezoneIdentifier,
            "AccountTimeZone" to feedbackData.accountTimeZone.toFeedbackString(),
            "NumberOfWorkspaces" to feedbackData.numberOfWorkspaces.toString(),
            "NumberOfTimeEntries" to feedbackData.numberOfTimeEntries.toString(),
            "NumberOfUnsyncedTimeEntries" to feedbackData.numberOfUnsyncedTimeEntries.toString(),
            "NumberOfUnsyncableTimeEntries" to feedbackData.numberOfUnsyncableTimeEntries.toString(),
            "LastSyncAttempt" to feedbackData.lastSyncAttempt.toFeedbackString(),
            "LastSuccessfulSync" to feedbackData.lastSuccessfulSync.toFeedbackString(),
            "DeviceTime" to feedbackData.deviceTime.toString(),
            "ManualModeIsOn" to feedbackData.manualModeIsOn.toFeedbackString(),
            "LastLogin" to feedbackData.lastLogin.toFeedbackString(),
            "UserId" to "${user.id}",
            "CurrentNativeLanguageCode" to platformInfo.currentNativeLanguageCode,
            "TimezoneIdentifier" to platformInfo.timezoneIdentifier,
            "BuildNumber" to platformInfo.buildNumber,
            "InstallLocation" to platformInfo.installLocation.name
        ).toKeyValue()

        feedbackApi.sendFeedback(
            feedbackBody = FeedbackBody(user.email.toString(), message, data)
        )
    }

    private fun Boolean.toFeedbackString(): String = if (this) "yes" else "no"
    private fun OffsetDateTime?.toFeedbackString(): String = this?.toString() ?: never
    private fun TimeZone?.toFeedbackString(): String = this?.displayName ?: unspecified
}
