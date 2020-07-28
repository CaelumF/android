package com.toggl.domain.reducers

class FeatureAvailabilityReducerTests
//     : FreeSpec() {
//     init {
//
//         val allActions: List<AppAction> = listOf(
//             AppAction.Loading(LoadingAction.StartLoading),
//             AppAction.Timer(TimerAction.StartEditTimeEntry(StartEditAction.BillableTapped)),
//             AppAction.Onboarding(OnboardingAction.Login(LoginAction.LoginButtonTapped))
//         )
//
//         "The FeatureAvailabilityReducer" - {
//             "For non premium actions" - {
//                 val state = AppState()
//                 val mutableValue = state.toMutableValue { }
//                 val nonPremiumActions = allActions.filterNot(::actionIsPremium)
//
//                 "forwards the action to the child reducer" - {
//                     nonPremiumActions.forEach { appAction ->
//                         val spyReducer = spyk<Reducer<AppState, AppAction>>()
//                         val featureAvailabilityReducer = FeatureAvailabilityReducer(spyReducer)
//
//                         featureAvailabilityReducer.reduce(mutableValue, appAction)
//
//                         verify { spyReducer.reduce(mutableValue, appAction) }
//                     }
//                 }
//             }
//
//             "For the toggle billable action" - {
//                 val toggleBillableAction = AppAction.Timer(
//                     TimerAction.StartEditTimeEntry(
//                         StartEditAction.BillableTapped
//                     )
//                 )
//
//                 "forwards the action normally if the edited TE is in a premium workspace" - {
//                     val state = AppState().copy(
//                         workspaces = mapOf(1L to Workspace(1, "Auto created workspace", listOf(WorkspaceFeature.Pro)))
//                     )
//                     val mutableValue = state.toMutableValue { }
//                     val spyReducer = spyk<Reducer<AppState, AppAction>>()
//                     val featureAvailabilityReducer = FeatureAvailabilityReducer(spyReducer)
//
//                     featureAvailabilityReducer.reduce(mutableValue, toggleBillableAction)
//
//                     verify { spyReducer.reduce(mutableValue, toggleBillableAction) }
//                 }
//
//                 "does not forward the action when the edited TE is in a non-premium workspace" - {
//                     val state = AppState().copy(
//                         workspaces = mapOf(1L to Workspace(1, "Auto created workspace", listOf()))
//                     )
//                     val mutableValue = state.toMutableValue { }
//                     val spyReducer = spyk<Reducer<AppState, AppAction>>()
//                     val featureAvailabilityReducer = FeatureAvailabilityReducer(spyReducer)
//
//                     featureAvailabilityReducer.reduce(mutableValue, toggleBillableAction)
//
//                     verify(exactly = 0) { spyReducer.reduce(mutableValue, toggleBillableAction) }
//                 }
//             }
//         }
//     }
//
//     private fun actionIsPremium(appAction: AppAction) =
//         appAction.isOrWraps<StartEditAction.BillableTapped>()
// }