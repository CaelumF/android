package com.toggl.calendar.contextualmenu.domain

import com.toggl.calendar.common.testReduceEffects
import com.toggl.common.feature.models.SelectedCalendarItem
import com.toggl.common.services.time.TimeService
import com.toggl.models.domain.EditableTimeEntry
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts

@DisplayName("The CloseButtonTapped action")
internal class CloseButtonTappedActionTests {

    private val timeService = mockk<TimeService> { every { now() } returns OffsetDateTime.now() }
    private val reducer = ContextualMenuReducer(timeService)

    @Test
    fun `returns a close effect`() = runBlockingTest {
        val timeEntry = EditableTimeEntry.empty(1)
        val initialState = createInitialState(selectedItem = SelectedCalendarItem.SelectedTimeEntry(timeEntry))

        reducer.testReduceEffects(initialState, ContextualMenuAction.CloseButtonTapped) {
            it.single().execute().shouldBeInstanceOf<ContextualMenuAction.Close>()
        }
    }
}
