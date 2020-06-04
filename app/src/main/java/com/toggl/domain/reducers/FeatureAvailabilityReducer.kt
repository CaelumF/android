package com.toggl.domain.reducers

import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.HigherOrderReducer
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.extensions.noEffect
import com.toggl.domain.AppAction
import com.toggl.domain.AppState
import com.toggl.models.extensions.isPro
import com.toggl.timer.common.domain.TimerAction
import com.toggl.timer.startedit.domain.StartEditAction

class FeatureAvailabilityReducer(override val innerReducer: AppReducer)
    : HigherOrderReducer<AppState, AppAction> {
    override fun reduce(
        state: MutableValue<AppState>,
        action: AppAction
    ): List<Effect<AppAction>> =
        when {
            action.isToggleBillableAction() -> state.mapState {
                val workspaceId = editableTimeEntry?.workspaceId ?: return@mapState noEffect()
                val workspace = workspaces[workspaceId]
                    ?: return@mapState noEffect()

                if (workspace.isPro()) innerReducer.reduce(state, action)
                else noEffect()
            }
            else -> innerReducer.reduce(state, action)
    }
}

fun AppAction.isToggleBillableAction() =
    this is AppAction.Timer &&
        this.timer is TimerAction.StartEditTimeEntry &&
        this.timer.action is StartEditAction.BillableTapped