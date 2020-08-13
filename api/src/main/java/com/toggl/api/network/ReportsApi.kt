package com.toggl.api.network

import com.toggl.api.models.ApiProject
import com.toggl.api.models.ProjectSummary
import com.toggl.api.network.models.reports.ProjectsSummaryBody
import com.toggl.api.network.models.reports.SearchProjectsBody
import com.toggl.api.network.models.reports.TotalsBody
import com.toggl.api.network.models.reports.TotalsResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface ReportsApi {
    @POST("workspace/{workspaceId}/search/time_entries/totals")
    suspend fun totals(
        @Path("workspaceId") workspaceId: Long,
        @Body totalsBody: TotalsBody
    ): TotalsResponse

    @POST("workspace/{workspaceId}/projects/summary")
    suspend fun projectsSummary(
        @Path("workspaceId") workspaceId: Long,
        @Body projectsSummaryBody: ProjectsSummaryBody
    ): List<ProjectSummary>

    @POST("workspace/{workspaceId}/search/projects")
    suspend fun searchProjects(
        @Path("workspaceId") workspaceId: Long,
        @Body searchProjectsBody: SearchProjectsBody
    ): List<ApiProject>
}
