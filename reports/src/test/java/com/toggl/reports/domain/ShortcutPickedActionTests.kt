package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.common.Either
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The ShortcutPicked action")
class ShortcutPickedActionTests : CoroutineTest() {

    private val initialState = createInitialState()
    private val reportsApiClient = mockk<ReportsApiClient>()
    private val assets = LoadReportsEffect.NeededAssets(
        "No Project",
        "#FF00FF",
        "offline",
        "error"
    )
    private val reducer = ReportsReducer(
        timeService = mockk(),
        dispatcherProvider = dispatcherProvider,
        reportsApiClient = reportsApiClient,
        assets = assets
    )

    @Test
    fun `sets the selection to null`() = runBlockingTest {
        reducer.testReduceState(initialState, ReportsAction.ShortcutPicked(ReportsShortcut.Today)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Right(ReportsShortcut.Today)
                )
            )
        }
    }

    @Test
    fun `does not return effects`() = runBlockingTest {
        reducer.testReduceNoEffects(initialState, ReportsAction.ShortcutPicked(ReportsShortcut.Today))
    }
}
