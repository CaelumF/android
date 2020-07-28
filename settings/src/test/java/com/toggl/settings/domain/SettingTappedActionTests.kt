package com.toggl.settings.domain

import com.google.common.truth.Truth.assertThat
import com.toggl.models.domain.SettingsType
import com.toggl.settings.common.CoroutineTest
import com.toggl.settings.common.createSettingsReducer
import com.toggl.settings.common.createSettingsState
import com.toggl.settings.common.testReduceEffects
import com.toggl.settings.common.testReduceNoEffects
import com.toggl.settings.common.testReduceState

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalCoroutinesApi
@DisplayName("The SettingTapped action")
class SettingTappedActionTests : CoroutineTest() {
    private val initialState = createSettingsState()
    private val reducer = createSettingsReducer(dispatcherProvider = dispatcherProvider)

    @Test
    fun `Should return some effect`() = runBlockingTest {
        reducer.testReduceEffects(
            initialState,
            SettingsAction.SettingTapped(SettingsType.ManualMode)
        ) { effect -> assertThat(effect).isNotEmpty() }
    }

    @ParameterizedTest
    @MethodSource("settingTypes")
    fun `Should set the local state to the setting type for single-choice settings`(settingsType: SettingsType) =
        runBlockingTest {
            reducer.testReduceState(initialState, SettingsAction.SettingTapped(settingsType)) {
                assertThat(it.localState.singleChoiceSettingShowing).isEqualTo(settingsType)
            }
        }

    @ParameterizedTest
    @MethodSource("settingTypes")
    fun `Should produce no effects for single-choice settings`(settingsType: SettingsType) =
        runBlockingTest {
            reducer.testReduceNoEffects(initialState, SettingsAction.SettingTapped(settingsType))
        }

    @Test
    fun `Should not update the local state with setting type for other settings`() = runBlockingTest {
        reducer.testReduceState(initialState, SettingsAction.SettingTapped(SettingsType.TwentyFourHourClock)) {
            assertThat(it.localState.singleChoiceSettingShowing).isEqualTo(null)
        }
    }

    companion object {
        @JvmStatic
        fun settingTypes(): Stream<SettingsType> = Stream.of(
            SettingsType.DurationFormat,
            SettingsType.FirstDayOfTheWeek,
            SettingsType.DateFormat,
            SettingsType.Workspace
        )
    }
}