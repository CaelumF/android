package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The ViewAppeared action")
class ViewAppearedActionTests : CoroutineTest() {

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
    fun `does not change the state`() = runBlockingTest {

        reducer.testReduceState(initialState, ReportsAction.ViewAppeared) { state ->
            state shouldBe initialState
        }
    }

    @Test
    fun `returns an effect for setting the date range with the current dates and using the initial source`() = runBlockingTest {
        reducer.testReduceEffects(initialState, ReportsAction.ViewAppeared) { effects ->
            effects.shouldBeSingleton()
            val effectAction = effects.single().execute()
            (effectAction as ReportsAction.DateRangeSelected).shouldNotBeNull()
            effectAction.dateRangeSelection shouldBe DateRangeSelection(
                initialState.localState.dateRange,
                SelectionSource.Initial
            )
        }
    }
}
