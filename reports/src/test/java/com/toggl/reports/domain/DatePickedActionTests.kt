package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.common.Either
import com.toggl.models.common.DateRange
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

@DisplayName("The DatePicked action")
class DatePickedActionTests : CoroutineTest() {

    private val now = OffsetDateTime.now()
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
    fun `sets the selection to the picked date and null if there already are two selected dates`() = runBlockingTest {
        reducer.testReduceState(initialState, ReportsAction.DatePicked(now)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Left(DateRange(now, null))
                )
            )
        }
    }

    @Test
    fun `sets the selection to the picked date and null if the selection is null`() = runBlockingTest {
        val initialState = initialState.copy(localState = initialState.localState.copy(selection = null))

        reducer.testReduceState(initialState, ReportsAction.DatePicked(now)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Left(DateRange(now, null))
                )
            )
        }
    }

    @Test
    fun `sets the selection to the picked date and null if the current selection is a shortcut`() = runBlockingTest {
        val initialState = initialState.copy(
            localState = initialState.localState.copy(
                selection = Either.Right(ReportsShortcut.ThisYear)
            )
        )

        reducer.testReduceState(initialState, ReportsAction.DatePicked(now)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Left(DateRange(now, null))
                )
            )
        }
    }

    @Test
    fun `sets the selection to the existing date then the picked date if the picked data is bigger than the existing date`() = runBlockingTest {
        val initialState = initialState.copy(
            localState = initialState.localState.copy(
                selection = Either.Left(DateRange(now, null))
            )
        )

        val pickedDate = now.plusDays(7)
        reducer.testReduceState(initialState, ReportsAction.DatePicked(pickedDate)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Left(DateRange(now, pickedDate))
                )
            )
        }
    }

    @Test
    fun `sets the selection to the picked date then the existing date if the picked data is smaller than the existing date`() = runBlockingTest {
        val initialState = initialState.copy(
            localState = initialState.localState.copy(
                selection = Either.Left(DateRange(now, null))
            )
        )

        val pickedDate = now.minusDays(7)
        reducer.testReduceState(initialState, ReportsAction.DatePicked(pickedDate)) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = Either.Left(DateRange(pickedDate, now))
                )
            )
        }
    }

    @Test
    fun `does not return effects`() = runBlockingTest {
        reducer.testReduceNoEffects(initialState, ReportsAction.DatePicked(now))
    }
}
