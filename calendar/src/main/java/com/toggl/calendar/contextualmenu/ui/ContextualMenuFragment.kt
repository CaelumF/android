package com.toggl.calendar.contextualmenu.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.toggl.architecture.extensions.select
import com.toggl.calendar.R
import com.toggl.calendar.contextualmenu.domain.ContextualMenuAction
import com.toggl.calendar.contextualmenu.domain.ContextualMenuDisplaySelector
import com.toggl.calendar.contextualmenu.domain.ContextualMenuViewModel
import com.toggl.common.feature.domain.formatForDisplay
import com.toggl.common.feature.navigation.BottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_contextualmenu.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.absoluteValue

@AndroidEntryPoint
class ContextualMenuFragment : Fragment(R.layout.fragment_contextualmenu) {

    @Inject lateinit var contextualMenuDisplaySelector: ContextualMenuDisplaySelector
    @Inject lateinit var bottomSheetNavigator: BottomSheetNavigator

    private val store: ContextualMenuStoreViewModel by activityViewModels()

    private lateinit var behavior: BottomSheetBehavior<*>
    private val contextualMenuAdapter = ContextualMenuActionsAdapter { store.dispatch(it) }

    private var bottomSheetSlideOffsetUpdateCallback: BottomSheetBehavior.BottomSheetCallback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions_recycler_view.adapter = contextualMenuAdapter
        actions_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        store.state
            .select(contextualMenuDisplaySelector)
            .onEach { updateContextualMenu(it) }
            .launchIn(lifecycleScope)

        cancel_button.setOnClickListener {
            store.dispatch(ContextualMenuAction.CloseButtonTapped)
        }
        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetNavigator.setupWithBehaviour(behavior, viewLifecycleOwner) {
            store.dispatch(ContextualMenuAction.DialogDismissed)
        }
        val slideOffsetUpdateCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // The slideOffset sometimes is +-Infinity and NaN
                val corrected = slideOffset.coerceIn(-1f, 1f)
                val nanFixed = if (corrected.isNaN()) 0f else corrected
                val visibleHeight = (1 - nanFixed.absoluteValue) * bottomSheet.height
                store.slideOffsetInPixels.value = visibleHeight.toInt()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        }
        behavior.addBottomSheetCallback(slideOffsetUpdateCallback)
        bottomSheetSlideOffsetUpdateCallback = slideOffsetUpdateCallback
    }

    override fun onDestroyView() {
        bottomSheetSlideOffsetUpdateCallback?.let { behavior.removeBottomSheetCallback(it) }
        bottomSheetSlideOffsetUpdateCallback = null
        super.onDestroyView()
    }

    private fun updateContextualMenu(contextualMenuViewModel: ContextualMenuViewModel) {
        description.text = contextualMenuViewModel.description
        period_label.text = contextualMenuViewModel.periodLabel
        contextualMenuAdapter.submitList(contextualMenuViewModel.contextualMenuActions.actions)

        when (contextualMenuViewModel) {
            is ContextualMenuViewModel.TimeEntryContextualMenu -> updateProjectInfo(contextualMenuViewModel)
            is ContextualMenuViewModel.CalendarEventContextualMenu -> updateCalendarInfo(contextualMenuViewModel)
        }
    }

    private fun updateCalendarInfo(viewModel: ContextualMenuViewModel.CalendarEventContextualMenu) {
        if (viewModel.calendarColor != null && viewModel.calendarName != null) {
            project_dot.visibility = View.VISIBLE
            project_label.visibility = View.VISIBLE
            project_label.text = viewModel.calendarName
            project_dot.setColorFilter(Color.parseColor(viewModel.calendarColor), PorterDuff.Mode.SRC_IN)
        } else {
            project_dot.visibility = View.GONE
            project_label.visibility = View.GONE
        }
    }

    private fun updateProjectInfo(viewModel: ContextualMenuViewModel.TimeEntryContextualMenu) {
        if (viewModel.projectViewModel != null) {
            project_dot.visibility = View.VISIBLE
            project_label.visibility = View.VISIBLE
            project_label.text = viewModel.projectViewModel.formatForDisplay()
            project_dot.setColorFilter(Color.parseColor(viewModel.projectViewModel.color), PorterDuff.Mode.SRC_IN)
        } else {
            project_dot.visibility = View.GONE
            project_label.visibility = View.GONE
        }
    }
}
