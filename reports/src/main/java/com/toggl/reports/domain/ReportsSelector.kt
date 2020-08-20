package com.toggl.reports.domain

import com.toggl.api.models.Resolution
import com.toggl.architecture.Failure
import com.toggl.architecture.Loadable
import com.toggl.architecture.core.Selector
import com.toggl.common.extensions.formatForDisplayingDate
import com.toggl.common.feature.extensions.totalHours
import com.toggl.common.feature.formatting.DurationFormatter
import com.toggl.reports.models.ReportData
import com.toggl.reports.models.TotalsReport
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsSelector @Inject constructor(
    private val durationFormatter: DurationFormatter
) : Selector<ReportsState, List<ReportsViewModel>> {
    override suspend fun select(state: ReportsState): List<ReportsViewModel> =
        datePicker(state) + when (val loadable = state.localState.reportData) {
            Loadable.Uninitialized,
            Loadable.Loading -> emptyList()
            is Loadable.Error -> createReportsViewModelsFromFailure(loadable.failure)
            is Loadable.Loaded -> createReportsViewModelsFromData(state, loadable.value)
        }

    private fun datePicker(state: ReportsState) = state.let {
        val dateRange = it.localState.dateRange
        val formatter = DateTimeFormatter.ofPattern(it.preferences.dateFormat.formatterPattern)
        val formattedStart = formatter.format(dateRange.start)

        listOf(
            ReportsViewModel.DatePicker(
                if (dateRange.end == null) formattedStart
                else "$formattedStart - ${formatter.format(dateRange.end)}"
            )
        )
    }

    private fun createReportsViewModelsFromFailure(failure: Failure): List<ReportsViewModel> =
        listOf(ReportsViewModel.Error(failure.errorMessage))

    private fun createReportsViewModelsFromData(state: ReportsState, reportData: ReportData): List<ReportsViewModel> = sequence {

        val totalsReport = reportData.totalsReport
        val projectSummaryReport = reportData.projectSummaryReport

        if (projectSummaryReport.totalSeconds == Duration.ZERO)
            return@sequence

        val preferredDurationFormat = state.preferences.durationFormat

        // Totals
        val totalDuration = projectSummaryReport.totalSeconds
        val formattedTotals = durationFormatter.format(totalDuration, preferredDurationFormat)
        yield(ReportsViewModel.Total(total = formattedTotals))

        // Billable
        val totalBillableDuration = projectSummaryReport.totalSeconds
        val billablePercentage = projectSummaryReport.billablePercentage
        val formattedBillableDuration = durationFormatter.format(totalBillableDuration, preferredDurationFormat)
        val billableData = BillableData(formattedBillableDuration, billablePercentage)
        yield(ReportsViewModel.Billable(billableData = billableData))

        // Amounts
        if (totalsReport.amounts.isNotEmpty()) {
            val amounts = totalsReport.amounts.map {
                val formattedRate = String.format("%.2f", it.amount)
                AmountsData(formattedRate, it.currency)
            }
            yield(ReportsViewModel.Amounts(amounts = amounts))
        }

        // Bar chart
        val upperBarLimit = totalsReport.groups.maxOf { it.total }.totalHours
        val bars = totalsReport.groups
            .map { group ->
                Bar(
                    group.billable.totalHours.toDouble() / upperBarLimit,
                    group.total.totalHours.toDouble() / upperBarLimit
                )
            }

        val startDate = state.localState.dateRange.start
        val endDate = state.localState.dateRange.end ?: startDate
        val xLabels = convertReportTimeEntriesToXAxisLabels(totalsReport, startDate, endDate)
        val yLabels = convertReportTimeEntriesToYAxisLabels(totalsReport, durationFormatter)

        yield(
            ReportsViewModel.BarChart(
                BarChartInfo(
                    bars,
                    bars.maxOf { bar -> bar.totalValue },
                    xLabels,
                    yLabels
                )
            )
        )

        // Donut Chart Slices
        val slices = mutableListOf<DonutSlice>()
        val legendInfo = mutableListOf<ReportsViewModel.DonutChartLegend>()

        var angleOffset = 0f
        for (segment in projectSummaryReport.segments) {
            val percentage = segment.percentage
            val sweepAngle = 3.6F * percentage

            slices.add(
                DonutSlice(
                    label = segment.projectName,
                    color = segment.color,
                    percentage = percentage,
                    startAngle = angleOffset,
                    sweepAngle = sweepAngle
                )
            )

            legendInfo.add(
                DonutChartLegendInfo(
                    projectName = segment.projectName,
                    projectColorHex = segment.color,
                    percentage = String.format("%.2f%%", segment.percentage),
                    duration = durationFormatter.format(segment.trackedTime, preferredDurationFormat)

                ).let(ReportsViewModel::DonutChartLegend)
            )

            angleOffset += sweepAngle
        }

        yield(ReportsViewModel.DonutChart(slices))

        yieldAll(legendInfo)

        yield(ReportsViewModel.AdvancedReportsOnWeb)
    }.toList()

    private fun convertReportTimeEntriesToXAxisLabels(
        report: TotalsReport,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): List<String> {
        if (report.resolution == Resolution.Day && report.groups.size <= 7)
            return (0L..report.groups.size).map {
                val date = startDate.plusDays(it)
                val dateString = date.formatForDisplayingDate()
                val dayOfWeekString = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                "$dateString\n$dayOfWeekString"
            }

        if (report.groups.size == 1)
            return listOf(startDate.formatForDisplayingDate())

        return listOf(startDate.formatForDisplayingDate(), endDate.formatForDisplayingDate())
    }

    private fun convertReportTimeEntriesToYAxisLabels(report: TotalsReport, durationFormatter: DurationFormatter): BarChartYLabels {
        fun createLabels(maxValue: Duration, unit: ChronoUnit) =
            BarChartYLabels(
                durationFormatter.formatRoundingWithTimeUnit(maxValue, unit),
                durationFormatter.formatRoundingWithTimeUnit(maxValue.dividedBy(2L), unit),
                durationFormatter.formatRoundingWithTimeUnit(Duration.ZERO, unit)
            )
        val maxTime = report.groups.maxOf { it.total }
        return when {
            maxTime.totalHours > 1 -> createLabels(maxTime, ChronoUnit.HOURS)
            maxTime.toMinutes() > 1 -> createLabels(maxTime, ChronoUnit.MINUTES)
            else -> createLabels(maxTime, ChronoUnit.SECONDS)
        }
    }
}
