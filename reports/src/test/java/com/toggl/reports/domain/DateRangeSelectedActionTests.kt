package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.architecture.Loadable
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The DateRangeSelected action")
class DateRangeSelectedActionTests : CoroutineTest() {

    private val initialState = createInitialState()
    private val dateRangeSelection = DateRangeSelection(initialState.localState.dateRange, SelectionSource.Initial)
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
    fun `sets the report data to loading`() = runBlockingTest {
        reducer.testReduceState(initialState, ReportsAction.DateRangeSelected(dateRangeSelection)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    reportData = Loadable.Loading
                )
            )
        }
    }

    @Test
    fun `returns an effect to load the report`() = runBlockingTest {
        reducer.testReduceEffects(initialState, ReportsAction.DateRangeSelected(dateRangeSelection)) { effects ->
            effects.shouldBeSingleton()
            effects.single().shouldBeTypeOf<LoadReportsEffect>()
        }
    }

    @Test
    fun `returns no effects if there already is a report loading`() = runBlockingTest {
        val firstState = initialState.copy(localState = initialState.localState.copy(reportData = Loadable.Loading))
        reducer.testReduceNoEffects(firstState, ReportsAction.DateRangeSelected(dateRangeSelection))
    }
}
