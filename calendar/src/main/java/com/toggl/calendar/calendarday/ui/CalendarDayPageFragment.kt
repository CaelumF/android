package com.toggl.calendar.calendarday.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.toggl.calendar.R
import com.toggl.calendar.calendarday.domain.CalendarDayAction
import com.toggl.calendar.calendarday.domain.CalendarItemsSelector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_calendarday_page.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.IllegalStateException
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CalendarDayPageFragment : Fragment(R.layout.fragment_calendarday_page) {
    private val store: CalendarDayStoreViewModel by activityViewModels()

    @Inject
    lateinit var calendarItemsSelector: CalendarItemsSelector

    private var dateOffset: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateOffset = arguments?.getLong(calendarDayPageDayOffsetKey, 0L) ?: throw IllegalStateException()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @FlowPreview
    @ExperimentalContracts
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = OffsetDateTime.now().minusDays(dateOffset)
        store.scrollOffset.value.let {
            calendar_widget.setScrollOffset(it)
        }

        store.hourHeight.value.let {
            if (it != 0f) {
                calendar_widget.setHourHeight(it)
            }
        }

        store.state
            .map { calendarItemsSelector.select(it).invoke(date) }
            .distinctUntilChanged()
            .onEach { calendar_widget.updateList(it) }
            .launchIn(lifecycleScope)

        calendar_widget.itemTappedFlow
            .onEach { store.dispatch(CalendarDayAction.ItemTapped(it)) }
            .launchIn(lifecycleScope)

        calendar_widget.emptySpaceLongPressedFlow
            .onEach { store.dispatch(CalendarDayAction.EmptyPositionLongPressed(it)) }
            .launchIn(lifecycleScope)

        calendar_widget.startTimeFlow
            .onEach { store.dispatch(CalendarDayAction.StartTimeDragged(it)) }
            .launchIn(lifecycleScope)

        calendar_widget.endTimeFlow
            .onEach { store.dispatch(CalendarDayAction.StopTimeDragged(it)) }
            .launchIn(lifecycleScope)

        calendar_widget.offsetFlow
            .onEach { store.dispatch(CalendarDayAction.TimeEntryDragged(it)) }
            .launchIn(lifecycleScope)

        calendar_widget.scrollOffsetFlow
            .onEach { store.scrollOffset.value = it }
            .launchIn(lifecycleScope)

        calendar_widget.hourHeightFlow
            .onEach { store.hourHeight.value = it }
            .launchIn(lifecycleScope)

        store.scrollOffset
            .onEach { calendar_widget.setScrollOffset(it) }
            .launchIn(lifecycleScope)

        store.hourHeight
            .filter { it != 0F }
            .onEach { calendar_widget.setHourHeight(it) }
            .launchIn(lifecycleScope)
    }

    companion object {
        const val calendarDayPageDayOffsetKey = "calendarDayPageDayOffsetKey"

        fun create(dayOffset: Long): CalendarDayPageFragment =
            CalendarDayPageFragment().apply {
                arguments = bundleOf(calendarDayPageDayOffsetKey to dayOffset)
            }
    }
}
