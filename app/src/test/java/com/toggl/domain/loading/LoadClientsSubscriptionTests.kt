package com.toggl.domain.loading

import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.common.CoroutineTest
import com.toggl.domain.AppState
import com.toggl.domain.extensions.createClient
import com.toggl.domain.extensions.createUser
import com.toggl.models.domain.Client
import com.toggl.models.domain.User
import com.toggl.repository.interfaces.ClientRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The LoadClientsSubscription")
class LoadClientsSubscriptionTests : CoroutineTest() {
    private val repository = mockk<ClientRepository>()
    private val subscription = LoadClientsSubscription(repository, dispatcherProvider)

    private val uninitializedUser = Loadable.Uninitialized
    private val loadingUser = Loadable.Loading
    private val errorUser = Loadable.Error<User>(Failure(IllegalAccessError(), ""))
    private val loadedUser = Loadable.Loaded(createUser(1))

    private val client1 = createClient(1)
    private val client2 = createClient(2)

    @Test
    fun `emits an empty client list when the user is not loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputClientsFlow = flowOf(listOf(client1, client2)),
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
    fun `emits clients only after the user is fully loaded`() = runBlockingTest {
        subscription.testSubscribe(
            inputClientsFlow = flowOf(listOf(client1, client2)),
            inputStateFlow = flowOf(
                AppState(user = uninitializedUser),
                AppState(user = errorUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(),
                listOf(client1, client2)
            )
        )
    }

    @Test
    fun `emits empty clients list right after logging out`() = runBlockingTest {
        subscription.testSubscribe(
            inputClientsFlow = flowOf(listOf(client1, client2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = uninitializedUser),
                AppState(user = errorUser)
            ),
            expectedOutput = listOf(
                listOf(client1, client2),
                listOf()
            )
        )
    }

    @Test
    fun `re-emits clients after a repeated login`() = runBlockingTest {
        subscription.testSubscribe(
            inputClientsFlow = flowOf(listOf(client1, client2)),
            inputStateFlow = flowOf(
                AppState(user = loadedUser),
                AppState(user = loadingUser),
                AppState(user = loadedUser),
                AppState(user = uninitializedUser)
            ),
            expectedOutput = listOf(
                listOf(client1, client2),
                listOf(),
                listOf(client1, client2),
                listOf()
            )
        )
    }

    @Test
    fun `always emits the latest client list`() = runBlockingTest {
        subscription.testSubscribe(
            inputClientsFlow = flowOf(
                listOf(client1),
                listOf(client2),
                listOf(client1, client2)
            ),
            inputStateFlow = flowOf(
                AppState(user = loadedUser)
            ),
            expectedOutput = listOf(
                listOf(client1),
                listOf(client2),
                listOf(client1, client2)
            )
        )
    }

    private suspend fun LoadClientsSubscription.testSubscribe(
        inputClientsFlow: Flow<List<Client>>,
        inputStateFlow: Flow<AppState>,
        expectedOutput: List<List<Client>>
    ) {
        every { repository.loadClients() } returns inputClientsFlow
        val outputActionFlow = this.subscribe(inputStateFlow)
        val outputActions = outputActionFlow.toList(mutableListOf())
        val outTimeEntries = outputActions
            .map { it.action as LoadingAction.ClientsLoaded }
            .map { it.clients }
        outTimeEntries shouldBe expectedOutput
    }
}
