package com.toggl.reports.domain

import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.common.feature.timeentry.TimeEntryAction
import com.toggl.common.feature.timeentry.TimeEntryActionHolder
import com.toggl.models.domain.Client
import com.toggl.models.domain.Project
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.models.domain.Workspace
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions

suspend inline fun <reified Holder : TimeEntryActionHolder, reified TimeEntryActionType : TimeEntryAction> Effect<Any>.shouldEmitTimeEntryAction(additionalTestBlock: (TimeEntryActionType) -> Unit = {}) {
    this.execute().shouldBeTypeOf<Holder> {
        it.timeEntryAction.shouldBeTypeOf<TimeEntryActionType> { timeEntryAction ->
            additionalTestBlock(timeEntryAction)
        }
    }
}

fun <T> T.toMutableValue(setFunction: (T) -> Unit) =
    MutableValue({ this }, setFunction)

suspend fun <State, Action> Reducer<State, Action>.testReduce(
    initialState: State,
    action: Action,
    testCase: suspend (State, List<Effect<Action>>) -> Unit
) {
    var state = initialState
    val mutableValue = state.toMutableValue { state = it }
    val effect = reduce(mutableValue, action)
    testCase(state, effect)
}

fun <State, Action, EX : Exception> Reducer<State, Action>.testReduceException(
    initialState: State,
    action: Action,
    exception: Class<EX>
) {
    Assertions.assertThrows(exception) {
        runBlockingTest {
            testReduce(initialState, action) { _, _ -> }
        }
    }
}

suspend fun <State, Action> Reducer<State, Action>.testReduceState(
    initialState: State,
    action: Action,
    testCase: suspend (State) -> Unit
) = testReduce(initialState, action) { state, _ -> testCase(state) }

suspend fun <State, Action> Reducer<State, Action>.testReduceEffects(
    initialState: State,
    action: Action,
    testCase: suspend (List<Effect<Action>>) -> Unit
) = testReduce(initialState, action) { _, effects -> testCase(effects) }

suspend fun <State, Action> Reducer<State, Action>.testReduceNoEffects(
    initialState: State,
    action: Action
) = testReduce(initialState, action, ::assertNoEffectsWereReturned)

val validUser = User(
    id = 0,
    apiToken = ApiToken.from("12345678901234567890123456789012") as ApiToken.Valid,
    defaultWorkspaceId = 1,
    email = Email.from("valid.mail@toggl.com") as Email.Valid,
    name = "name"
)

fun createInitialState(
    user: User = validUser,
    userPreferences: UserPreferences = UserPreferences.default,
    clients: List<Client> = emptyList(),
    projects: List<Project> = emptyList(),
    workspaces: List<Workspace> = emptyList(),
    localState: ReportsState.LocalState = ReportsState.LocalState()
) = ReportsState(
    user,
    userPreferences,
    clients.associateBy { it.id },
    projects.associateBy { it.id },
    workspaces.associateBy { it.id },
    localState
)

@Suppress("UNUSED_PARAMETER")
suspend fun <State, Action> assertNoEffectsWereReturned(state: State, effect: List<Effect<Action>>) {
    effect.shouldBeEmpty()
}

fun createClient(id: Long) = Client(id, "name: $id", 1)

fun createProject(
    id: Long,
    name: String = "Project",
    color: String = "#1e1e1e",
    active: Boolean = true,
    isPrivate: Boolean = false,
    billable: Boolean? = null,
    workspaceId: Long = 1,
    clientId: Long? = null
) = Project(
    id,
    name,
    color,
    active,
    isPrivate,
    billable,
    workspaceId,
    clientId
)
