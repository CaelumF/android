package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createTag
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.Tag
import com.toggl.models.domain.User
import com.toggl.repository.interfaces.TagRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The LoadTagsSubscription")
class LoadTagsSubscriptionTests : CoroutineTest() {
    private val repository = mockk<TagRepository>()
    private val subscription = LoadTagsSubscription(repository, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val tag1 = createTag(1)
    private val tag2 = createTag(2)

    @Test
    fun `emits an empty tag list when the user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputTagsFlow = flowOf(listOf(tag1, tag2)),
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
    fun `emits tags only after the user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputTagsFlow = flowOf(listOf(tag1, tag2)),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(),
                listOf(tag1, tag2)
            )
        )
    }

    @Test
    fun `emits empty tags list right after logging out`() = runBlockingTest {
        subscription.testSubscribe(
            inputTagsFlow = flowOf(listOf(tag1, tag2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                listOf(tag1, tag2),
                listOf()
            )
        )
    }

    @Test
    fun `re-emits tags after a repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            inputTagsFlow = flowOf(listOf(tag1, tag2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                listOf(tag1, tag2),
                listOf(),
                listOf(tag1, tag2),
                listOf()
            )
        )
    }

    @Test
    fun `always emits the latest tag list`() = runBlockingTest {
        subscription.testSubscribe(
            inputTagsFlow = flowOf(
                listOf(tag1),
                listOf(tag2),
                listOf(tag1, tag2)
            ),
            inputStateFlow = flowOf(
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(tag1),
                listOf(tag2),
                listOf(tag1, tag2)
            )
        )
    }

    private suspend fun LoadTagsSubscription.testSubscribe(
        inputTagsFlow: Flow<List<Tag>>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Tag>>
    ) {
        every { repository.loadTags() } returns inputTagsFlow
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outTimeEntries = outputActions
            .map { it.action as LoadingAction.TagsLoaded }
            .map { it.tags }
        outTimeEntries shouldBe expectedOutput
    }
}
