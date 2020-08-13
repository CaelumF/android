package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createTask
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.Task
import com.toggl.models.domain.User
import com.toggl.repository.interfaces.TaskRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The LoadTasksSubscription")
class LoadTasksSubscriptionTests : CoroutineTest() {
    private val repository = mockk<TaskRepository>()
    private val subscription = LoadTasksSubscription(repository, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val task1 = createTask(1)
    private val task2 = createTask(2)

    @Test
    fun `emits an empty task list when the user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputTasksFlow = flowOf(listOf(task1, task2)),
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
    fun `emits tasks only after the user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputTasksFlow = flowOf(listOf(task1, task2)),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(),
                listOf(task1, task2)
            )
        )
    }

    @Test
    fun `emits empty tasks list right after logging out`() = runBlockingTest {
        subscription.testSubscribe(
            inputTasksFlow = flowOf(listOf(task1, task2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                listOf(task1, task2),
                listOf()
            )
        )
    }

    @Test
    fun `re-emits tasks after a repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            inputTasksFlow = flowOf(listOf(task1, task2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                listOf(task1, task2),
                listOf(),
                listOf(task1, task2),
                listOf()
            )
        )
    }

    @Test
    fun `always emits the latest task list`() = runBlockingTest {
        subscription.testSubscribe(
            inputTasksFlow = flowOf(
                listOf(task1),
                listOf(task2),
                listOf(task1, task2)
            ),
            inputStateFlow = flowOf(
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(task1),
                listOf(task2),
                listOf(task1, task2)
            )
        )
    }

    private suspend fun LoadTasksSubscription.testSubscribe(
        inputTasksFlow: Flow<List<Task>>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Task>>
    ) {
        every { repository.loadTasks() } returns inputTasksFlow
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outTimeEntries = outputActions
            .map { it.action as LoadingAction.TasksLoaded }
            .map { it.tasks }
        outTimeEntries shouldBe expectedOutput
    }
}
