package com.toggl.reports.domain

import com.toggl.api.clients.ReportsApiClient
import com.toggl.common.Either
import com.toggl.models.common.DateRange
import com.toggl.reports.common.CoroutineTest
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

@DisplayName("The DateRangePickerAcceptButtonTapped action")
class DateRangePickerAcceptButtonTappedActionTests : CoroutineTest() {

    private val initialState = createInitialState()
    private val reportsApiClient = mockk<ReportsApiClient>()
    private val assets = LoadReportsEffect.NeededAssets(
        "No Project",
        "#FF00FF",
        "offline",
        "error"
    )
    private val now = OffsetDateTime.now()
    private val reducer = ReportsReducer(
        timeService = mockk { every { now() } returns now },
        dispatcherProvider = dispatcherProvider,
        reportsApiClient = reportsApiClient,
        assets = assets
    )

    @Test
    fun `sets the selection to null`() = runBlockingTest {
        reducer.testReduceState(initialState, ReportsAction.DateRangePickerAcceptButtonTapped) { state ->
            state shouldBe initialState.copy(
                localState = initialState.localState.copy(
                    selection = null
                )
            )
        }
    }

    @Test
    fun `returns an effect to set the date range selection using the current dates when a date range is selected`() = runBlockingTest {
        val expectedDateRangeSelection = DateRangeSelection(
            initialState.localState.dateRange,
            SelectionSource.Calendar
        )

        val initialState = initialState.copy(
            localState = initialState.localState.copy(
                selection = Either.Left(initialState.localState.dateRange)
            )
        )

        reducer.testReduceEffects(initialState, ReportsAction.DateRangePickerAcceptButtonTapped) { effects ->
            effects.shouldBeSingleton()
            val effectAction = effects.single().execute()
            (effectAction as ReportsAction.DateRangeSelected).shouldNotBeNull()
            effectAction.dateRangeSelection shouldBe expectedDateRangeSelection
        }
    }

    @Test
    fun `returns an effect to set the date range selection using the shortcut when a shortcut is selected`() = runBlockingTest {
        val expectedDateRangeSelection = DateRangeSelection(
            DateRange(now, null),
            SelectionSource.Shortcut(ReportsShortcut.Today)
        )

        val initialState = initialState.copy(
            localState = initialState.localState.copy(
                selection = Either.Right(ReportsShortcut.Today)
            )
        )

        reducer.testReduceEffects(initialState, ReportsAction.DateRangePickerAcceptButtonTapped) { effects ->
            effects.shouldBeSingleton()
            val effectAction = effects.single().execute()
            (effectAction as ReportsAction.DateRangeSelected).shouldNotBeNull()
            effectAction.dateRangeSelection shouldBe expectedDateRangeSelection
        }
    }
}
