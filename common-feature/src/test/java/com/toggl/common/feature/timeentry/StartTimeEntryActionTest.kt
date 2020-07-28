package com.toggl.common.feature.timeentry

import com.google.common.truth.Truth.assertThat
import com.toggl.common.feature.common.CoroutineTest
import com.toggl.common.feature.common.testReduceEffects
import com.toggl.common.feature.common.testReduceState
import com.toggl.repository.dto.StartTimeEntryDTO
import com.toggl.repository.interfaces.TimeEntryRepository

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

@ExperimentalCoroutinesApi
@DisplayName("The StartTimeEntryAction")
class StartTimeEntryActionTest : CoroutineTest() {

    private val repository = mockk<TimeEntryRepository>()
    private val reducer = TimeEntryReducer(repository, mockk(), dispatcherProvider)
    private val initialState = TimeEntryState(mapOf())
    private val startTimeEntryDTO = StartTimeEntryDTO("", OffsetDateTime.MAX, false, 1, 1, 1, listOf())

    @Test
    fun `Should return StartTimeEntryEffect`() = runBlockingTest {
        reducer.testReduceEffects(
            initialState,
            TimeEntryAction.StartTimeEntry(startTimeEntryDTO)
        ) { effects ->
            assertThat(effects).hasSize(1)
            assertThat(effects.first()).isInstanceOf(StartTimeEntryEffect::class.java)
        }
    }

    @Test
    fun `Shouldn't change the state`() = runBlockingTest {
        reducer.testReduceState(
            initialState,
            TimeEntryAction.StartTimeEntry(startTimeEntryDTO)
        ) { state -> assertThat(state).isEqualTo(initialState) }
    }
}
