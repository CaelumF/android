package com.toggl.reports.domain

import com.toggl.architecture.Loadable
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.models.domain.Workspace
import com.toggl.reports.models.ReportData
import java.time.OffsetDateTime

data class ReportsState(
    val user: User,
    val preferences: UserPreferences,
    val clients: Map<Long, Client>,
    val projects: Map<Long, Project>,
    val workspaces: Map<Long, Workspace>,
    val localState: LocalState
) {
    data class LocalState(
        internal val startDate: OffsetDateTime,
        internal val endDate: OffsetDateTime?,
        internal val reportData: Loadable<ReportData>,
        internal val selectedWorkspaceId: Long?
    ) {
        constructor() : this(
            OffsetDateTime.now().minusDays(7),
            OffsetDateTime.now(),
            Loadable.Uninitialized,
            null
        )
    }
}
