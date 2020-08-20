package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.architecture.extensions.effectOf
import com.toggl.architecture.extensions.noEffect
import com.toggl.common.Either
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.extensions.returnEffect
import com.toggl.common.services.time.TimeService
import com.toggl.models.common.DateRange
import com.toggl.reports.models.ReportData
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsReducer @Inject constructor(
    private val timeService: TimeService,
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
                val rangeSelection = DateRangeSelection(state().localState.dateRange, SelectionSource.Initial)
                effectOf(ReportsAction.DateRangeSelected(rangeSelection))
            }
            is ReportsAction.ReportLoaded -> state.setReportData(Loadable.Loaded(action.reportData))
            is ReportsAction.ReportFailed -> state.setReportData(Loadable.Error(action.failure))
            ReportsAction.AvailableOnOtherPlansTapped -> TODO()
            is ReportsAction.DatePicked -> state.setSelection(Either.Left(state().pickDate(action.date)))
            is ReportsAction.ShortcutPicked -> state.setSelection(Either.Right(action.shortcut))
            ReportsAction.OpenDateRangePickerButtonTapped -> state.setSelection(Either.Left(state().localState.dateRange))
            ReportsAction.DateRangePickerCloseButtonTapped -> state.setSelection(null)
            ReportsAction.DateRangePickerAcceptButtonTapped -> state.acceptCurrentSelection()
            is ReportsAction.DateRangeSelected ->
                if (state().localState.reportData is Loadable.Loading) noEffect()
                else state.setReportData(Loadable.Loading) returnEffect loadReportEffect(state(), action.dateRangeSelection)
        }

    private fun MutableValue<ReportsState>.acceptCurrentSelection(): List<Effect<ReportsAction>> {
        val selection = this().localState.selection
        return setSelection(null) returnEffect mapState {
            when (selection) {
                is Either.Left -> {
                    val rangeSelection = DateRangeSelection(selection.value, SelectionSource.Calendar)
                    effectOf(ReportsAction.DateRangeSelected(rangeSelection))
                }
                is Either.Right -> {
                    val dateRange = selection.value.dateRange(timeService.now(), preferences.firstDayOfTheWeek)
                    val rangeSelection = DateRangeSelection(dateRange, SelectionSource.Shortcut(selection.value))
                    effectOf(ReportsAction.DateRangeSelected(rangeSelection))
                }
                null -> noEffect()
            }
        }
    }

    private fun ReportsState.pickDate(selectedDate: OffsetDateTime): DateRange =
        when (val selection = localState.selection) {
            null, is Either.Right -> DateRange(selectedDate, null)
            is Either.Left -> {
                if (selection.value.end != null) DateRange(selectedDate, null)
                else {
                    val existingDate = selection.value.start

                    val (newStart, newEnd) =
                        if (existingDate > selectedDate) selectedDate to existingDate
                        else existingDate to selectedDate

                    DateRange(newStart, newEnd)
                }
            }
        }

    private fun MutableValue<ReportsState>.setSelection(selection: Either<DateRange, ReportsShortcut>? = null): List<Effect<ReportsAction>> =
        mutateWithoutEffects {
            copy(localState = localState.copy(selection = selection))
        }

    private fun MutableValue<ReportsState>.setReportData(reportData: Loadable<ReportData>): List<Effect<ReportsAction>> =
        mutateWithoutEffects {
            copy(localState = localState.copy(reportData = reportData))
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
