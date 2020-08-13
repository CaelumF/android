package com.toggl.api.clients

import com.toggl.api.models.ProjectSummary
import com.toggl.api.network.models.reports.TotalsResponse
import com.toggl.models.domain.Project
import java.time.OffsetDateTime

interface ReportsApiClient {
    suspend fun getTotals(
        userId: Long,
        workspaceId: Long,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime?
    ): TotalsResponse

    suspend fun getProjectSummary(
        workspaceId: Long,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime?
    ): List<ProjectSummary>

    suspend fun searchProjects(workspaceId: Long, idsToSearch: List<Long>): List<Project>
}
