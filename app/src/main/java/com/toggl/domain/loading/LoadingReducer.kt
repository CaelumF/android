package com.toggl.domain.loading

import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.architecture.extensions.effect
import com.toggl.common.feature.extensions.mutateWithoutEffects
import com.toggl.common.feature.extensions.returnEffect
import com.toggl.common.feature.navigation.Route
import com.toggl.common.feature.navigation.backStackOf
import com.toggl.repository.interfaces.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingReducer @Inject constructor(
    private val userRepository: UserRepository,
    private val dispatcherProvider: DispatcherProvider
) : Reducer<LoadingState, LoadingAction> {

    override fun reduce(
        state: MutableValue<LoadingState>,
        action: LoadingAction
    ): List<Effect<LoadingAction>> =
        when (action) {
            LoadingAction.StartLoading -> state.mutate {
                copy(user = Loadable.Loading)
            } returnEffect effect(TryLoadingUserEffect(userRepository, dispatcherProvider))
            is LoadingAction.UserLoaded -> state.mutateWithoutEffects {
                if (action.user == null) {
                    copy(user = Loadable.Uninitialized, backStack = backStackOf(Route.Welcome))
                } else {
                    copy(user = Loadable.Loaded(action.user), backStack = backStackOf(Route.Timer))
                }
            }
            is LoadingAction.TimeEntriesLoaded -> state.mutateWithoutEffects { copy(timeEntries = action.timeEntries) }
            is LoadingAction.WorkspacesLoaded -> state.mutateWithoutEffects { copy(workspaces = action.workspaces) }
            is LoadingAction.ProjectsLoaded -> state.mutateWithoutEffects { copy(projects = action.projects) }
            is LoadingAction.ClientsLoaded -> state.mutateWithoutEffects { copy(clients = action.clients) }
            is LoadingAction.TagsLoaded -> state.mutateWithoutEffects { copy(tags = action.tags) }
            is LoadingAction.TasksLoaded -> state.mutateWithoutEffects { copy(tasks = action.tasks) }
            is LoadingAction.UserPreferencesLoaded -> state.mutateWithoutEffects { copy(userPreferences = action.userPreferences) }
            is LoadingAction.CalendarsLoaded -> state.mutateWithoutEffects { copy(calendars = action.calendars) }
            is LoadingAction.SuggestionsLoaded -> state.mutateWithoutEffects { copy(suggestions = action.suggestions) }
            is LoadingAction.CalendarEventsLoaded -> state.mutateWithoutEffects { copy(calendarEvents = action.calendarEvents) }
        }
}
