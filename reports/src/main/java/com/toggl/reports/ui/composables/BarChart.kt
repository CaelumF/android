@file:Suppress("UNUSED_PARAMETER")

package com.toggl.reports.ui.composables

import androidx.compose.runtime.Composable
import com.toggl.reports.domain.ReportsAction
import com.toggl.reports.domain.ReportsViewModel

@Composable
fun BarChart(
    viewModel: ReportsViewModel.BarChart,
    dispatcher: (ReportsAction) -> Unit
) {
}
