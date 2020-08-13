package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.DateFormat
import com.toggl.models.domain.User
import com.toggl.models.domain.UserPreferences
import com.toggl.repository.interfaces.SettingsRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The LoadUserPreferencesSubscription")
class LoadUserPreferencesSubscriptionTests : CoroutineTest() {
    private val repository = mockk<SettingsRepository>()
    private val subscription = LoadUserPreferencesSubscription(repository, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val defaultUserPreferences = UserPreferences.default
    private val notDefaultUserPreferences = UserPreferences.default.copy(
        manualModeEnabled = !defaultUserPreferences.manualModeEnabled,
        twentyFourHourClockEnabled = !defaultUserPreferences.twentyFourHourClockEnabled,
        groupSimilarTimeEntriesEnabled = !defaultUserPreferences.groupSimilarTimeEntriesEnabled,
        cellSwipeActionsEnabled = !defaultUserPreferences.cellSwipeActionsEnabled,
        calendarIntegrationEnabled = !defaultUserPreferences.calendarIntegrationEnabled
    )

    @Test
    fun `emits the default user preferences when user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            flowOf(notDefaultUserPreferences),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = loadingUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(defaultUserPreferences)
        )
    }

    @Test
    fun `emits the user preferences only after user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            flowOf(notDefaultUserPreferences),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                defaultUserPreferences,
                notDefaultUserPreferences
            )
        )
    }

    @Test
    fun `emits the default user preferences right after logout`() = runBlockingTest {
        subscription.testSubscribe(
            flowOf(notDefaultUserPreferences),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                notDefaultUserPreferences,
                defaultUserPreferences
            )
        )
    }

    @Test
    fun `re-emits the user preferences after repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            flowOf(notDefaultUserPreferences),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                notDefaultUserPreferences,
                defaultUserPreferences,
                notDefaultUserPreferences,
                defaultUserPreferences
            )
        )
    }

    @Test
    fun `always emits the latest user preferences`() = runBlockingTest {
        subscription.testSubscribe(
            flowOf(
                notDefaultUserPreferences,
                defaultUserPreferences.copy(dateFormat = DateFormat.DDMMYYYY_dot),
                defaultUserPreferences.copy(dateFormat = DateFormat.DDMMYYYY_slash)
            ),
            inputStateFlow = flowOf(
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                notDefaultUserPreferences,
                defaultUserPreferences.copy(dateFormat = DateFormat.DDMMYYYY_dot),
                defaultUserPreferences.copy(dateFormat = DateFormat.DDMMYYYY_slash)
            )
        )
    }

    private suspend fun LoadUserPreferencesSubscription.testSubscribe(
        inputUserPreferences: Flow<UserPreferences>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<UserPreferences>
    ) {
        every { repository.loadUserPreferences() } returns inputUserPreferences
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outUserPreferences = outputActions
            .map { it.action as LoadingAction.UserPreferencesLoaded }
            .map { it.userPreferences }
        outUserPreferences shouldBe expectedOutput
    }
}
