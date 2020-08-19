package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.api.exceptions.OfflineException
import com.toggl.api.exceptions.UnauthorizedException
import com.toggl.api.models.ProjectSummary
import com.toggl.api.models.Resolution
import com.toggl.api.network.models.reports.GraphItem
import com.toggl.api.network.models.reports.RateInfo
import com.toggl.api.network.models.reports.TotalsResponse
import com.toggl.models.common.DateRange
import com.toggl.models.domain.Project
import com.toggl.reports.common.CoroutineTest
import com.toggl.reports.models.Amount
import com.toggl.reports.models.ChartSegment
import com.toggl.reports.models.ProjectSummaryReport
import com.toggl.reports.models.ReportsTotalsGroup
import com.toggl.reports.models.TotalsReport
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("The LoadReportsEffect")
class LoadReportsEffectTests : CoroutineTest() {

    private val user = validUser
    private val workspaceId = 1L
    private val totalSeconds = 599500L
    private val clients = (1L until 60).map { createClient(it) }.associateBy { it.id }
    private val projectsInState = (1L until 100L).map {
        createProject(it, clientId = if (it % 2L == 0L) it / 2 else null)
    }.associateBy { it.id }

    private val assets = LoadReportsEffect.NeededAssets(
        "No Project",
        "#FF00FF",
        "offline",
        "error"
    )

    private val totalsResponse = TotalsResponse(
        seconds = 934948,
        resolution = Resolution.Day,
        rates = listOf(
            RateInfo(10, 10000, "USD"),
            RateInfo(20, 10000, "EUR")
        ),
        graph = listOf(
            GraphItem(100, mapOf("1" to 10L, "2" to 100L)),
            GraphItem(200, mapOf("1" to 20L, "2" to 200L)),
            GraphItem(300, mapOf("1" to 30L, "2" to 300L))
        )
    )

    private val projectSummaries = (1L until 110L).map {
        val trackedSeconds = it * 100
        val hasBillable = it % 2L == 0L
        val billableSeconds = if (hasBillable) trackedSeconds / 3 else null
        ProjectSummary(it, trackedSeconds, billableSeconds)
    }

    private val projectsNotInState = (100L until 110L).map {
        createProject(it, clientId = if (it % 2L == 0L) it / 2 else null)
    }

    private val reportsApiClient = mockk<ReportsApiClient> {
        coEvery { getTotals(validUser.id, workspaceId, any(), any()) } returns totalsResponse
        coEvery { getProjectSummary(workspaceId, any(), any()) } returns projectSummaries
        coEvery { searchProjects(workspaceId, any()) } returns projectsNotInState
    }

    private suspend fun callEffect(projects: Map<Long, Project> = projectsInState) =
        LoadReportsEffect(
            dispatcherProvider = dispatcherProvider,
            reportsApiClient = reportsApiClient,
            assets = assets,
            user = user,
            projects = projects,
            clients = clients,
            workspaceId = workspaceId,
            dateRangeSelection = DateRangeSelection(
                DateRange(OffsetDateTime.now(), OffsetDateTime.now().minusDays(10)),
                SelectionSource.Initial
            )
        ).execute()

    @Test
    fun `properly assembles the reports data if the api calls succeed`() = runBlockingTest {

        val action = callEffect() as ReportsAction.ReportLoaded

        val totalsReport = action.reportData.totalsReport
        val projectSummaryReport = action.reportData.projectSummaryReport

        projectSummaryReport shouldBe ProjectSummaryReport(
            totalSeconds = Duration.ofSeconds(totalSeconds),
            billableSeconds = Duration.ofSeconds(98982),
            billablePercentage = 16.510757F,
            segments = projectSummaries.map { summary ->
                val project = projectsInState[summary.projectId] ?: projectsNotInState.single { it.id == summary.projectId }
                val percentage = summary.trackedSeconds / totalSeconds.toFloat() * 100
                ChartSegment(
                    trackedTime = Duration.ofSeconds(summary.trackedSeconds),
                    billableSeconds = Duration.ofSeconds(summary.billableSeconds ?: 0),
                    percentage = percentage,
                    projectName = project.name,
                    color = project.color,
                    clientName = clients[project.clientId]?.name
                )
            }.sortedByDescending { it.percentage }
        )

        totalsReport shouldBe TotalsReport(
            resolution = Resolution.Day,
            amounts = listOf(
                Amount(16.666668F, "USD"),
                Amount(33.333336F, "EUR"),
            ),
            groups = listOf(
                ReportsTotalsGroup(
                    total = Duration.ofSeconds(100),
                    billable = Duration.ofSeconds(110)
                ),
                ReportsTotalsGroup(
                    total = Duration.ofSeconds(200),
                    billable = Duration.ofSeconds(220)
                ),
                ReportsTotalsGroup(
                    total = Duration.ofSeconds(300),
                    billable = Duration.ofSeconds(330)
                )
            )
        )
    }

    @Test
    fun `only calls the search api when projects are not found in the state`() = runBlockingTest {

        val allProjects = projectsInState + projectsNotInState.associateBy { it.id }

        callEffect(allProjects)

        coVerify(exactly = 0) {
            reportsApiClient.searchProjects(any(), any())
        }
    }

    @Test
    fun `returns an error action with an offline error message if any of the api calls fail due to connectivity`() = runBlockingTest {

        coEvery { reportsApiClient.getTotals(validUser.id, workspaceId, any(), any()) } throws OfflineException()
        coEvery { reportsApiClient.getProjectSummary(workspaceId, any(), any()) } throws OfflineException()

        val action = callEffect() as ReportsAction.ReportFailed

        action.failure.errorMessage shouldBe assets.offlineError
    }

    @Test
    fun `returns an error action with a generic error message if any of the api calls fail`() = runBlockingTest {

        coEvery { reportsApiClient.getTotals(validUser.id, workspaceId, any(), any()) } throws UnauthorizedException("")
        coEvery { reportsApiClient.getProjectSummary(workspaceId, any(), any()) } throws UnauthorizedException("")

        val action = callEffect() as ReportsAction.ReportFailed

        action.failure.errorMessage shouldBe assets.genericError
    }
}
