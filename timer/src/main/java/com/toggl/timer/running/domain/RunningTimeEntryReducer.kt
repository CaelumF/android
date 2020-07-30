package com.toggl.timer.running.domain

import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.architecture.extensions.noEffect
import com.toggl.architecture.extensions.toEffect
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.push
import com.toggl.common.feature.timeentry.TimeEntryAction.StartTimeEntry
import com.toggl.common.feature.timeentry.TimeEntryAction.StopRunningTimeEntry
import com.toggl.common.feature.timeentry.extensions.runningTimeEntryOrNull
import com.toggl.common.services.time.TimeService
import com.toggl.models.domain.EditableTimeEntry
import com.toggl.repository.extensions.toStartDto
import com.toggl.timer.running.domain.RunningTimeEntryAction.CardTapped
import com.toggl.timer.running.domain.RunningTimeEntryAction.StartButtonTapped
import com.toggl.timer.running.domain.RunningTimeEntryAction.StopButtonTapped
import com.toggl.timer.running.domain.RunningTimeEntryAction.TimeEntryHandling
import javax.inject.Inject

class RunningTimeEntryReducer @Inject constructor(val timeService: TimeService) : Reducer<RunningTimeEntryState, RunningTimeEntryAction> {

    override fun reduce(
        state: MutableValue<RunningTimeEntryState>,
        action: RunningTimeEntryAction
    ): List<Effect<RunningTimeEntryAction>> =
        when (action) {
            StartButtonTapped -> state.mapState {
                val dto = EditableTimeEntry
                    .empty(state().user.defaultWorkspaceId)
                    .toStartDto(timeService.now())

                val timeEntryAction = TimeEntryHandling(StartTimeEntry(dto))
                effect(timeEntryAction.toEffect())
            }
            StopButtonTapped -> effect(TimeEntryHandling(StopRunningTimeEntry).toEffect())
            CardTapped ->
                state.mutateWithoutEffects {
                    val entryToOpen = timeEntries.runningTimeEntryOrNull()
                        ?.run(EditableTimeEntry.Companion::fromSingle)
                        ?: EditableTimeEntry.empty(state().user.defaultWorkspaceId)

                    val route = Route.StartEdit(entryToOpen)
                    copy(backStack = backStack.push(route))
                }
            is TimeEntryHandling -> noEffect()
        }
}
