package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.extensions.returnEffect
import com.toggl.reports.models.ReportData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsReducer @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val reportsApiClient: ReportsApiClient,
    private val assets: LoadReportsEffect.NeededAssets
) : Reducer<ReportsState, ReportsAction> {

    override fun reduce(
        state: MutableValue<ReportsState>,
        action: ReportsAction
    ): List<Effect<ReportsAction>> =
        when (action) {
            ReportsAction.ViewAppeared -> {
                if (state().localState.reportData is Loadable.Loading) {
                    noEffect()
                } else {
                    val rangeSelection = state.mapState {
                        DateRangeSelection(localState.startDate, localState.endDate, SelectionSource.Initial)
                    }

                    state.setReportData(Loadable.Loading) returnEffect loadReportEffect(state(), rangeSelection)
                }
            }
            is ReportsAction.ReportLoaded -> state.setReportData(Loadable.Loaded(action.reportData))
            is ReportsAction.ReportFailed -> state.setReportData(Loadable.Error(action.failure))
            ReportsAction.AvailableOnOtherPlansTapped -> TODO()
        }

    private fun MutableValue<ReportsState>.setReportData(reportData: Loadable<ReportData>): List<Effect<ReportsAction>> =
        mutateWithoutEffects {
            copy(
                localState = localState.copy(
                    reportData = reportData
                )
            )
        }

    private fun loadReportEffect(
        state: ReportsState,
        rangeSelection: DateRangeSelection
    ): List<Effect<ReportsAction>> = effect(
        LoadReportsEffect(
            dispatcherProvider,
            reportsApiClient,
            assets,
            state.user,
            state.projects,
            state.clients,
            state.localState.selectedWorkspaceId ?: state.user.defaultWorkspaceId,
            rangeSelection
        )
    )
}
