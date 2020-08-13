package com.toggl.reports.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toggl.architecture.extensions.select
import com.toggl.common.feature.compose.extensions.createComposeView
import com.toggl.reports.domain.ReportsAction
import com.toggl.reports.domain.ReportsSelector
import com.toggl.reports.domain.ReportsState
import com.toggl.reports.ui.composables.ReportsPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    @Inject @JvmField var selector: ReportsSelector? = null

    private val store: ReportsStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = createComposeView { statusBarHeight, _ ->

        val viewModels = store.state
            .distinctUntilChanged(::reportsProperties)
            .select(selector!!)

        ReportsPage(viewModels, statusBarHeight, store::dispatch)
    }

    private fun reportsProperties(old: ReportsState, new: ReportsState): Boolean =
        old.localState.reportData == new.localState.reportData &&
            old.preferences == new.preferences

    override fun onResume() {
        super.onResume()
        store.dispatch(ReportsAction.ViewAppeared)
    }
}
