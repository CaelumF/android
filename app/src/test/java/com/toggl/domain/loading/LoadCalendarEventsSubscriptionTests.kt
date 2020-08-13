package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.common.feature.services.calendar.CalendarEvent
import com.toggl.common.services.time.TimeService
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.User
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("The LoadCalendarEventsSubscriptionTests")
class LoadCalendarEventsSubscriptionTests : CoroutineTest() {
    private val calendarEventsProvider = mockk<CalendarEventsProvider>()
    private val timeService = mockk<TimeService> { every { now() } returns OffsetDateTime.now() }
    private val subscription = LoadCalendarsEventsSubscription(
        timeService,
        { minusMonths(2) },
        { plusMonths(2) },
        calendarEventsProvider
    )

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val event1 = CalendarEvent(
        "id1",
        OffsetDateTime.MAX,
        Duration.ofSeconds(1),
        "description",
        null,
        "calId1",
        "calName"
    )
    private val event2 = event1.copy(id = "id2")
    private val calendarEvents = listOf(event1, event2)

    @Test
    fun `emits empty events list when user is not loaded or the permissions is not granted`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
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
    fun `emits events only after user is fully loaded and permission was granted`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser, calendarPermissionWasGranted = false),
                AppState(user = errorUser, calendarPermissionWasGranted = false),
                AppState(user = loadingUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
            ),
            expectedOutput = listOf(
                listOf(),
                calendarEvents
            )
        )
    }

    @Test
    fun `emits empty events right after logout`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadingUser, calendarPermissionWasGranted = true),
                AppState(user = uninitializedUser, calendarPermissionWasGranted = true),
                AppState(user = errorUser, calendarPermissionWasGranted = true)
            ),
            expectedOutput = listOf(
                calendarEvents,
                listOf()
            )
        )
    }

    @Test
    fun `emits empty events when permission was revoked`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false)
            ),
            expectedOutput = listOf(
                calendarEvents,
                listOf()
            )
        )
    }

    @Test
    fun `re-emits events after repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadingUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = uninitializedUser, calendarPermissionWasGranted = true)
            ),
            expectedOutput = listOf(
                calendarEvents,
                listOf(),
                calendarEvents,
                listOf()
            )
        )
    }

    @Test
    fun `re-emits events after repeated permission state change`() = runBlockingTest {
        subscription.testSubscribe(
            providedEvents = calendarEvents,
            inputStateFlow = flowOf(
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false),
                AppState(user = loadedUser, calendarPermissionWasGranted = true),
                AppState(user = loadedUser, calendarPermissionWasGranted = false)
            ),
            expectedOutput = listOf(
                calendarEvents,
                listOf(),
                calendarEvents,
                listOf()
            )
        )
    }

    private suspend fun LoadCalendarsEventsSubscription.testSubscribe(
        providedEvents: List<CalendarEvent>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<CalendarEvent>>
    ) {
        coEvery { calendarEventsProvider.toCalendarEventsFlow(any(), any()) } returns flowOf(providedEvents)
        coEvery { calendarEventsProvider.unregister() } returns Unit

        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outEvents = outputActions
            .map { it.action as LoadingAction.CalendarEventsLoaded }
            .map { it.calendarEvents }
        outEvents shouldBe expectedOutput
    }
}
