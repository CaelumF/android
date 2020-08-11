package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createTimeEntry
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.User
import com.toggl.timer.suggestions.domain.Suggestion
import com.toggl.timer.suggestions.domain.SuggestionProvider
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
@DisplayName("The LoadSuggestionsSubscription")
class LoadSuggestionsSubscriptionTests : CoroutineTest() {
    private val suggestionProvider = mockk<SuggestionProvider>()
    private val subscription = LoadSuggestionsSubscription(suggestionProvider, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val calendarSuggestion = Suggestion.Calendar(mockk(), 1)
    private val mostUsedSuggestion = Suggestion.MostUsed(mockk())

    private val te1 = createTimeEntry(1)
    private val te2 = createTimeEntry(2)

    @Test
    fun `emits empty suggestion list when user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            providedSuggestions = listOf(calendarSuggestion, mostUsedSuggestion),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = loadingUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                emptyList()
            )
        )
    }

    @Test
    fun `emits suggestions only after user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            providedSuggestions = listOf(calendarSuggestion, mostUsedSuggestion),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(),
                listOf(calendarSuggestion, mostUsedSuggestion)
            )
        )
    }

    @Test
    fun `emits empty suggestions right after logout`() = runBlockingTest {
        subscription.testSubscribe(
            providedSuggestions = listOf(calendarSuggestion, mostUsedSuggestion),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                listOf(calendarSuggestion, mostUsedSuggestion),
                listOf()
            )
        )
    }

    @Test
    fun `re-emits suggestions after repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            providedSuggestions = listOf(calendarSuggestion, mostUsedSuggestion),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                listOf(calendarSuggestion, mostUsedSuggestion),
                listOf(),
                listOf(calendarSuggestion, mostUsedSuggestion),
                listOf()
            )
        )
    }

    @Test
    fun `do not re-emits the same suggestions`() = runBlockingTest {
        subscription.testSubscribe(
            providedSuggestions = listOf(calendarSuggestion, mostUsedSuggestion),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadedUser, timeEntries = mapOf(te1.id to te1)),
                AppState(user = loadedUser, timeEntries = mapOf(te2.id to te2)),
                AppState(user = uninitializedUser),
                AppState(user = loadedUser, timeEntries = mapOf(te2.id to te2)),
            ),
            expectedOutput = listOf(
                listOf(calendarSuggestion, mostUsedSuggestion),
                listOf(),
                listOf(calendarSuggestion, mostUsedSuggestion),
            )
        )
    }

    private suspend fun LoadSuggestionsSubscription.testSubscribe(
        providedSuggestions: List<Suggestion>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Suggestion>>
    ) {
        coEvery { suggestionProvider.getSuggestions(any()) } returns providedSuggestions
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outSuggestions = outputActions
            .map { it.action as LoadingAction.SuggestionsLoaded }
            .map { it.suggestions }
        outSuggestions shouldBe expectedOutput
    }
}
