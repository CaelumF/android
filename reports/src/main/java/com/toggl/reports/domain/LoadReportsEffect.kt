package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.api.exceptions.OfflineException
import com.toggl.api.models.ProjectSummary
import com.toggl.api.network.models.reports.TotalsResponse
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Failure
import com.toggl.architecture.core.Effect
import com.toggl.common.Either
import com.toggl.common.mapLeft
import com.toggl.common.mapRight
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.User
import com.toggl.reports.models.Amount
import com.toggl.reports.models.ChartSegment
import com.toggl.reports.models.ProjectSummaryReport
import com.toggl.reports.models.ReportData
import com.toggl.reports.models.ReportsTotalsGroup
import com.toggl.reports.models.TotalsReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.time.Duration

class LoadReportsEffect(
    private val dispatcherProvider: DispatcherProvider,
    private val reportsApiClient: ReportsApiClient,
    private val assets: NeededAssets,
    private val user: User,
    private val projects: Map<Long, Project>,
    private val clients: Map<Long, Client>,
    private val workspaceId: Long,
    private val dateRangeSelection: DateRangeSelection
) : Effect<ReportsAction> {
    override suspend fun execute(): ReportsAction? {
        val job = SupervisorJob()
        val scope = CoroutineScope(dispatcherProvider.io + job)

        val totals = scope.async {
            reportsApiClient.getTotals(
                user.id,
                workspaceId,
                dateRangeSelection.startDate,
                dateRangeSelection.endDate
            ).let { assembleTotalsReport(it) }
        }

        val summary = scope.async {
            reportsApiClient.getProjectSummary(
                workspaceId,
                dateRangeSelection.startDate,
                dateRangeSelection.endDate
            ).let { assembleProjectsReport(it) }
        }

        return try {
            val data = ReportData(totals.await(), summary.await())
            ReportsAction.ReportLoaded(data)
        } catch (ex: Exception) {
            val failure = Failure(
                ex,
                when (ex) {
                    is OfflineException -> assets.offlineError
                    else -> assets.genericError
                }
            )
            ReportsAction.ReportFailed(failure)
        }
    }

    private fun assembleTotalsReport(response: TotalsResponse) =
        TotalsReport(
            resolution = response.resolution,
            amounts = response.rates?.map {
                val billableHours = it.billableSeconds / 60F
                val amountInCents = billableHours * it.hourlyRateInCents
                val actualAmount = amountInCents / 100F
                Amount(actualAmount, it.currency)
            } ?: emptyList(),
            groups = response.graph.map { graphItem ->
                ReportsTotalsGroup(
                    total = Duration.ofSeconds(graphItem.seconds),
                    billable = Duration.ofSeconds(graphItem.byRate?.let { it.values.sum() } ?: 0)
                )
            }
        )

    private suspend fun assembleProjectsReport(projectsSummaries: List<ProjectSummary>): ProjectSummaryReport {
        val projectIds = projectsSummaries.mapNotNull { it.projectId }
        val totalSeconds = projectsSummaries.map { it.trackedSeconds }.sum()
        val billableSeconds = projectsSummaries.mapNotNull { it.billableSeconds }.sum()
        val billablePercentage = if (totalSeconds > 0) 100.0f / totalSeconds * billableSeconds else 0F

        val projectsInReport = searchProjects(workspaceId, projectIds)
        return ProjectSummaryReport(
            totalSeconds = Duration.ofSeconds(totalSeconds),
            billableSeconds = Duration.ofSeconds(billableSeconds),
            billablePercentage = billablePercentage,
            segments = projectsSummaries
                .map { toSegment(it, totalSeconds, projectsInReport) }
                .sortedByDescending { it.percentage }
        )
    }

    private fun toSegment(summary: ProjectSummary, totalSeconds: Long, projectsInReport: List<Project>): ChartSegment {
        val percentage = if (totalSeconds == 0L) 0F else (summary.trackedSeconds / totalSeconds.toFloat()) * 100
        val billableSeconds = summary.billableSeconds ?: 0

        val (projectName, projectColor, clientName) = projectsInReport
            .firstOrNull { p -> p.id == summary.projectId }
            .let {
                if (it == null) Triple(assets.noProject, assets.noProjectColorHex, null)
                else Triple(it.name, it.color, clients[it.clientId]?.name)
            }

        return ChartSegment(
            trackedTime = Duration.ofSeconds(summary.trackedSeconds),
            billableSeconds = Duration.ofSeconds(billableSeconds),
            percentage = percentage,
            projectName = projectName,
            color = projectColor,
            clientName = clientName
        )
    }

    private suspend fun searchProjects(
        workspaceId: Long,
        projectIds: List<Long>
    ): List<Project> {

        if (projectIds.isEmpty())
            return emptyList()

        val projectsEitherList = projectIds.map { projects.eitherProjectOrKey(it) }
        val idsNotInState = projectsEitherList.mapLeft()
        val projectsFoundInState = projectsEitherList.mapRight()

        return if (idsNotInState.isEmpty()) projectsFoundInState
        else projectsFoundInState + reportsApiClient.searchProjects(workspaceId, idsNotInState)
    }

    private fun Map<Long, Project>.eitherProjectOrKey(id: Long): Either<Long, Project> =
        if (contains(id)) Either.Right(getValue(id)) else Either.Left(id)

    data class NeededAssets(
        val noProject: String,
        val noProjectColorHex: String,
        val offlineError: String,
        val genericError: String
    )
}
