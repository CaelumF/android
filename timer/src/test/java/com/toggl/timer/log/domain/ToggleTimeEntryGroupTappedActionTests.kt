package com.toggl.timer.log.domain

import com.toggl.timer.common.FreeCoroutineSpec

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ToggleTimeEntryGroupTappedActionTests : FreeCoroutineSpec() {
    // init {
    //     val reducer = TimeEntriesLogReducer()
    //
    //     "The ToggleTimeEntryGroupTappedAction action" - {
    //         "should add groupId to expandedGroupId if it wasn't there before" {
    //             reducer.testReduce(
    //                 initialState = createInitialState(expandedGroupIds = setOf(2, 3)),
    //                 action = TimeEntriesLogAction.ToggleTimeEntryGroupTapped(1)
    //             ) { state, _ ->
    //                 state.expandedGroupIds shouldContain 1
    //             }
    //         }
    //         "should remove groupId from expandedGroupId if it was already there" {
    //             reducer.testReduce(
    //                 initialState = createInitialState(expandedGroupIds = setOf(1, 2, 3)),
    //                 action = TimeEntriesLogAction.ToggleTimeEntryGroupTapped(1)
    //             ) { state, _ ->
    //                 state.expandedGroupIds shouldNotContain 1
    //             }
    //         }
    //         "shouldn't return any effect" {
    //             reducer.testReduce(
    //                 initialState = createInitialState(expandedGroupIds = setOf(1, 2, 3)),
    //                 action = TimeEntriesLogAction.ToggleTimeEntryGroupTapped(1)
    //             ) { _, effect ->
    //                 assertThat(effect).isEmpty()
    //             }
    //         }
    //     }
    // }
}