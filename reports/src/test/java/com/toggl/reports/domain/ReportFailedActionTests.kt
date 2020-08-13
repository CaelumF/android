package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.api.exceptions.OfflineException
import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The ReportsFailed action")
class ReportFailedActionTests : CoroutineTest() {

    private val initialState = createInitialState()
    private val reportsApiClient = mockk<ReportsApiClient>()
    private val assets = LoadReportsEffect.NeededAssets(
        "No Project",
        "#FF00FF",
        "offline",
        "error"
    )
    private val reducer = ReportsReducer(
        dispatcherProvider = dispatcherProvider,
        reportsApiClient = reportsApiClient,
        assets = assets
    )

    @Test
    fun `sets the report data to error`() = runBlockingTest {
        val failure = Failure(OfflineException(), "")

        reducer.testReduceState(initialState, ReportsAction.ReportFailed(failure)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    reportData = Loadable.Error(failure)
                )
            )
        }
    }

    @Test
    fun `returns no effects`() = runBlockingTest {
        val failure = Failure(OfflineException(), "")

        reducer.testReduceNoEffects(initialState, ReportsAction.ReportFailed(failure))
    }
}
