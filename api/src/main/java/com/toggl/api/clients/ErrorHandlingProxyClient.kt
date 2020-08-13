package com.toggl.api.clients

import com.toggl.api.clients.authentication.AuthenticationApiClient
import com.toggl.api.clients.authentication.RetrofitAuthenticationApiClient
import com.toggl.api.clients.feedback.FeedbackApiClient
import com.toggl.api.clients.feedback.RetrofitFeedbackApiClient
import com.toggl.api.exceptions.ApiException
import com.toggl.api.exceptions.ForbiddenException.Companion.remainingLoginAttemptsHeaderName
import com.toggl.api.exceptions.OfflineException
import com.toggl.api.exceptions.ReportsRangeTooLongException
import com.toggl.api.models.ProjectSummary
import com.toggl.api.models.SyncApiPreferences
import com.toggl.api.models.SyncApiProject
import com.toggl.api.models.SyncApiTag
import com.toggl.api.models.SyncApiTask
import com.toggl.api.models.SyncApiUser
import com.toggl.api.models.SyncApiWorkspace
import com.toggl.api.network.ReportsApi
import com.toggl.api.network.SyncApi
import com.toggl.api.network.models.sync.PullResponse
import com.toggl.api.network.models.reports.ProjectsSummaryBody
import com.toggl.api.network.models.reports.SearchProjectsBody
import com.toggl.api.network.models.reports.TotalsBody
import com.toggl.api.network.models.reports.TotalsResponse
import com.toggl.api.network.models.sync.PushAction
import com.toggl.api.network.models.sync.PushBody
import com.toggl.api.network.models.sync.PushResponse
import com.toggl.common.Constants
import com.toggl.models.domain.FeedbackData
import com.toggl.models.domain.PlatformInfo
import com.toggl.models.domain.Project
import com.toggl.models.domain.User
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import retrofit2.HttpException
import java.net.UnknownHostException
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ErrorHandlingProxyClient @Inject constructor(
    private val authenticationApiClient: RetrofitAuthenticationApiClient,
    private val feedbackApiClient: RetrofitFeedbackApiClient,
    private val reportsApi: ReportsApi,
    private val syncApi: SyncApi
) : AuthenticationApiClient, FeedbackApiClient, ReportsApiClient, SyncApiClient {
    override suspend fun login(email: Email.Valid, password: Password.Valid): User {
        try {
            return authenticationApiClient.login(email, password)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun signUp(email: Email.Valid, password: Password.Strong): User {
        try {
            return authenticationApiClient.signUp(email, password)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun resetPassword(email: Email.Valid): String {
        try {
            return authenticationApiClient.resetPassword(email)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun sendFeedback(user: User, message: String, platformInfo: PlatformInfo, feedbackData: FeedbackData) {
        try {
            return feedbackApiClient.sendFeedback(user, message, platformInfo, feedbackData)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun getTotals(
        userId: Long,
        workspaceId: Long,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime?
    ): TotalsResponse {
        try {
            if (endDate != null && endDate.minusDays(Constants.Reports.maximumRangeInDays) > startDate)
                throw ReportsRangeTooLongException()

            val body = TotalsBody(
                startDate = startDate,
                endDate = endDate,
                userIds = listOf(userId),
                withGraph = true
            )
            return reportsApi.totals(workspaceId, body)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun getProjectSummary(
        workspaceId: Long,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime?
    ): List<ProjectSummary> {
        try {
            val body = ProjectsSummaryBody(startDate, endDate)
            return reportsApi.projectsSummary(workspaceId, body)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun searchProjects(workspaceId: Long, idsToSearch: List<Long>): List<Project> {
        try {
            val body = SearchProjectsBody(idsToSearch)
            return reportsApi.searchProjects(workspaceId, body)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun pull(since: OffsetDateTime?): PullResponse {
        try {
            return syncApi.pull(since?.toEpochSecond())
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    override suspend fun push(uuid: UUID): PushResponse {
        try {
            // TODO: Add actual data
            val body = PushBody(
                timeEntries = null,
                workspaces = null,
                projects = null,
                clients = null,
                tasks = null,
                tags = null,
                preferences = null,
                user = null,
            )

            return syncApi.push(uuid.toString(), body)
        } catch (exception: Exception) {
            throw handledException(exception)
        }
    }

    private fun handledException(exception: Exception) =
        when (exception) {
            is UnknownHostException -> OfflineException()
            is HttpException -> {
                ApiException.from(
                    exception.code(),
                    exception.response()?.errorBody()?.string(),
                    exception.tryParsingNumberOfAttemptsBeforeAccountBlock()
                )
            }
            else -> exception
        }

    private fun HttpException.tryParsingNumberOfAttemptsBeforeAccountBlock(): Int? {
        val headers = response()?.headers() ?: return null
        if (headers.size() == 0) return null
        val remainingAttemptsHeader = headers.get(remainingLoginAttemptsHeaderName) ?: return null
        return remainingAttemptsHeader.toIntOrNull()
    }
}
