package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.common.feature.services.calendar.Calendar
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.User
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@DisplayName("The LoadCalendarsSubscription")
class LoadCalendarsSubscriptionTests : CoroutineTest() {
    private val calendarProvider = mockk<CalendarProvider>()
    private val subscription = LoadCalendarsSubscription(calendarProvider, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val cal1 = Calendar(
        "id1",
        "name",
        "name"
    )
    private val cal2 = cal1.copy(id = "id2")
    private val calendars = listOf(cal1, cal2)

    @Test
    fun `emits empty calendars list when user is not loaded or the permissions is not granted`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser, calendarPermissionWasGranted = false),
                AppState(user = loadingUser, calendarPermissionWasGranted = true),
                AppState(user = errorUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false),
            ),
            expectedOutput = listOf(
                emptyList()
            )
        )
    }

    @Test
    fun `emits calendars only after user is fully loaded and permission was granted`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser, calendarPermissionWasGranted = false),
                AppState(user = errorUser, calendarPermissionWasGranted = false),
                AppState(user = loadingUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
            ),
            expectedOutput = listOf(
                listOf(),
                calendars
            )
        )
    }

    @Test
    fun `emits empty calendars right after logout`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadingUser, calendarPermissionWasGranted = true),
                AppState(user = uninitializedUser, calendarPermissionWasGranted = true),
                AppState(user = errorUser, calendarPermissionWasGranted = true)
            ),
            expectedOutput = listOf(
                calendars,
                listOf()
            )
        )
    }

    @Test
    fun `emits empty calendars when permission was revoked`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false)
            ),
            expectedOutput = listOf(
                calendars,
                listOf()
            )
        )
    }

    @Test
    fun `re-emits calendars after repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadingUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = uninitializedUser, calendarPermissionWasGranted = true)
            ),
            expectedOutput = listOf(
                calendars,
                listOf(),
                calendars,
                listOf()
            )
        )
    }

    @Test
    fun `re-emits calendars after repeated permission state change`() = runBlockingTest {
        subscription.testSubscribe(
            providedCalendars = calendars,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false)
            ),
            expectedOutput = listOf(
                calendars,
                listOf(),
                calendars,
                listOf()
            )
        )
    }

    private suspend fun LoadCalendarsSubscription.testSubscribe(
        providedCalendars: List<Calendar>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Calendar>>
    ) {
        coEvery { calendarProvider.calendarFlow() } returns flowOf(providedCalendars)
        coEvery { calendarProvider.unregister() } returns Unit

        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outEvents = outputActions
            .map { it.action as LoadingAction.CalendarsLoaded }
            .map { it.calendars }
        outEvents shouldBe expectedOutput
    }
}
