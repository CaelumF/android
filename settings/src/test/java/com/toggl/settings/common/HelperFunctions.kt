package com.toggl.settings.common

import com.toggl.api.clients.feedback.FeedbackApiClient
import com.toggl.architecture.DispatcherProvider
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Effect
import com.toggl.architecture.core.MutableValue
import com.toggl.architecture.core.Reducer
import com.toggl.common.feature.navigation.BackStack
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.common.feature.timeentry.TimeEntryAction
import com.toggl.common.feature.timeentry.TimeEntryActionHolder
import com.toggl.common.services.permissions.PermissionCheckerService
import com.toggl.models.domain.DateFormat
import com.toggl.models.domain.DurationFormat
import com.toggl.models.domain.PlatformInfo
import com.toggl.models.domain.SmartAlertsOption
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.models.validation.ApiToken
import com.toggl.models.validation.Email
import com.toggl.repository.interfaces.SettingsRepository
import com.toggl.repository.interfaces.UserRepository
import com.toggl.settings.domain.FeedbackDataBuilder
import com.toggl.settings.domain.SettingsReducer
import com.toggl.settings.domain.SettingsState
import com.toggl.settings.domain.SignOutEffect
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import java.time.DayOfWeek

fun createSettingsState(
    userPreferences: UserPreferences = createUserPreferences(),
    user: User = User(
        id = 0,
        apiToken = ApiToken.from("12345678901234567890123456789012") as ApiToken.Valid,
        defaultWorkspaceId = 1L,
        email = Email.from("email@valid.com") as Email.Valid,
        name = ""
    ),
    shouldRequestCalendarPermission: Boolean = false,
    sendFeedbackRequest: Loadable<Unit> = Loadable.Uninitialized,
    backStack: BackStack = emptyList(),
    calendars: Map<String, Calendar> = emptyMap()
) = SettingsState(
    user = user,
    userPreferences = userPreferences,
    workspaces = mapOf(),
    shouldRequestCalendarPermission = shouldRequestCalendarPermission,
    externalLocationToShow = null,
    backStack = backStack,
    localState = SettingsState.LocalState(sendFeedbackRequest),
    calendars = calendars
)

fun createUserPreferences(
    manualModeEnabled: Boolean = false,
    twentyFourHourClockEnabled: Boolean = false,
    cellSwipeActionsEnabled: Boolean = false,
    groupSimilarTimeEntriesEnabled: Boolean = false,
    calendarIntegrationEnabled: Boolean = false,
    calendarIds: List<String> = emptyList(),
    dateFormat: DateFormat = DateFormat.DDMMYYYY_dash,
    durationFormat: DurationFormat = DurationFormat.Classic,
    firstDayOfTheWeek: DayOfWeek = DayOfWeek.WEDNESDAY,
    smartAlertsOption: SmartAlertsOption = SmartAlertsOption.Disabled
) = UserPreferences(
    manualModeEnabled = manualModeEnabled,
    twentyFourHourClockEnabled = twentyFourHourClockEnabled,
    cellSwipeActionsEnabled = cellSwipeActionsEnabled,
    groupSimilarTimeEntriesEnabled = groupSimilarTimeEntriesEnabled,
    calendarIntegrationEnabled = calendarIntegrationEnabled,
    dateFormat = dateFormat,
    durationFormat = durationFormat,
    firstDayOfTheWeek = firstDayOfTheWeek,
    smartAlertsOption = smartAlertsOption,
    calendarIds = calendarIds
)

fun createSettingsReducer(
    settingsRepository: SettingsRepository = mockk(),
    permissionCheckerService: PermissionCheckerService = mockk(relaxed = true),
    platformInfo: PlatformInfo = mockk(),
    feedbackDataBuilder: FeedbackDataBuilder = mockk(),
    feedbackApiClient: FeedbackApiClient = mockk(),
    signOutEffect: SignOutEffect = mockk(),
    userRepository: UserRepository = mockk(),
    dispatcherProvider: DispatcherProvider
) = SettingsReducer(
    settingsRepository = settingsRepository,
    permissionCheckerService = permissionCheckerService,
    platformInfo = platformInfo,
    signOutEffect = signOutEffect,
    feedbackApiClient = feedbackApiClient,
    feedbackDataBuilder = feedbackDataBuilder,
    userRepository = userRepository,
    mockDatabaseInitializer = mockk(),
    dispatcherProvider = dispatcherProvider
)

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

@Suppress("UNUSED_PARAMETER")
suspend fun <State, Action> assertNoEffectsWereReturned(state: State, effect: List<Effect<Action>>) {
    effect.shouldBeEmpty()
}

suspend inline fun <reified Holder : TimeEntryActionHolder, reified TimeEntryActionType : TimeEntryAction> Effect<Any>.shouldEmitTimeEntryAction(
    additionalTestBlock: (TimeEntryActionType) -> Unit = {}
) {
    this.execute().shouldBeTypeOf<Holder> {
        it.timeEntryAction.shouldBeTypeOf<TimeEntryActionType> { timeEntryAction ->
            additionalTestBlock(timeEntryAction)
        }
    }
}
