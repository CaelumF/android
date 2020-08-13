package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.User
import com.toggl.models.domain.Workspace
import com.toggl.repository.interfaces.WorkspaceRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The LoadWorkspacesSubscription")
class LoadWorkspacesSubscriptionTests : CoroutineTest() {
    private val repository = mockk<WorkspaceRepository>()
    private val subscription = LoadWorkspacesSubscription(repository, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val workspace1 = Workspace(1, "Some name", emptyList())
    private val workspace2 = Workspace(2, "Other name", emptyList())

    @Test
    fun `emits an empty workspace list when the user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputWorkspacesFlow = flowOf(listOf(workspace1, workspace2)),
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
    fun `emits workspaces only after the user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputWorkspacesFlow = flowOf(listOf(workspace1, workspace2)),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(),
                listOf(workspace1, workspace2)
            )
        )
    }

    @Test
    fun `emits empty workspaces list right after logging out`() = runBlockingTest {
        subscription.testSubscribe(
            inputWorkspacesFlow = flowOf(listOf(workspace1, workspace2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                listOf(workspace1, workspace2),
                listOf()
            )
        )
    }

    @Test
    fun `re-emits workspaces after a repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            inputWorkspacesFlow = flowOf(listOf(workspace1, workspace2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                listOf(workspace1, workspace2),
                listOf(),
                listOf(workspace1, workspace2),
                listOf()
            )
        )
    }

    @Test
    fun `always emits the latest workspace list`() = runBlockingTest {
        subscription.testSubscribe(
            inputWorkspacesFlow = flowOf(
                listOf(workspace1),
                listOf(workspace2),
                listOf(workspace1, workspace2)
            ),
            inputStateFlow = flowOf(
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(workspace1),
                listOf(workspace2),
                listOf(workspace1, workspace2)
            )
        )
    }

    private suspend fun LoadWorkspacesSubscription.testSubscribe(
        inputWorkspacesFlow: Flow<List<Workspace>>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Workspace>>
    ) {
        every { repository.loadWorkspaces() } returns inputWorkspacesFlow
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outTimeEntries = outputActions
            .map { it.action as LoadingAction.WorkspacesLoaded }
            .map { it.workspaces }
        outTimeEntries shouldBe expectedOutput
    }
}
