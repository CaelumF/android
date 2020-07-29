package com.toggl.settings.domain

import com.toggl.models.domain.DurationFormat
import com.toggl.settings.common.CoroutineTest
import com.toggl.settings.common.createSettingsReducer
import com.toggl.settings.common.createSettingsState
import com.toggl.settings.common.testReduceEffects
import com.toggl.settings.common.testReduceState
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@DisplayName("The DurationFormatSelected action")
class DurationFormatSelectedActionTests : CoroutineTest() {
    private val initialState = createSettingsState()
    private val reducer = createSettingsReducer(dispatcherProvider = dispatcherProvider)

    @Test
    fun `Should return correct effect`() = runBlockingTest {
        reducer.testReduceEffects(
            initialState,
            SettingsAction.DurationFormatSelected(durationFormat = DurationFormat.Classic)
        ) { effects ->
            effects.shouldBeSingleton()
            effects.first().shouldBeInstanceOf<UpdateUserPreferencesEffect>()
        }
    }

    @Test
    fun `Shouldn't change the state`() = runBlockingTest {
        reducer.testReduceState(
            initialState,
            SettingsAction.DurationFormatSelected(durationFormat = DurationFormat.Classic)
        ) { state -> state shouldBe initialState }
    }
}