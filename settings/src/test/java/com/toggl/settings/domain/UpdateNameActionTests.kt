package com.toggl.settings.domain

import com.toggl.settings.common.CoroutineTest
import com.toggl.settings.common.createSettingsReducer
import com.toggl.settings.common.createSettingsState
import com.toggl.settings.common.testReduceEffects
import com.toggl.settings.common.testReduceState
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The UpdateName action")
class UpdateNameActionTests : CoroutineTest() {
    private val initialState = createSettingsState()
    private val reducer = createSettingsReducer(dispatcherProvider = dispatcherProvider)

    @Test
    fun `returns an effect to update the user`() = runBlockingTest {
        reducer.testReduceEffects(
            initialState,
            SettingsAction.UpdateName("new")
        ) { effects ->
            effects.shouldBeSingleton()
            effects.first().shouldBeInstanceOf<UpdateUserEffect>()
        }
    }

    @Test
    fun `shouldn't change the state`() = runBlockingTest {
        reducer.testReduceState(initialState, SettingsAction.UpdateName("new")) { state -> state shouldBe initialState }
    }
}
