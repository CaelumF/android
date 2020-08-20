package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.architecture.Loadable
import com.toggl.reports.common.CoroutineTest
import com.toggl.reports.models.ReportData
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The ReportLoaded action")
class ReportLoadedActionTests : CoroutineTest() {

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
    fun `sets the report data`() = runBlockingTest {
        val data = mockk<ReportData>()

        reducer.testReduceState(initialState, ReportsAction.ReportLoaded(data)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    reportData = Loadable.Loaded(data)
                )
            )
        }
    }

    @Test
    fun `returns no effects`() = runBlockingTest {
        val data = mockk<ReportData>()

        reducer.testReduceNoEffects(initialState, ReportsAction.ReportLoaded(data))
    }
}
