package com.toggl.reports.domain

sealed class ReportsViewModel {
    data class Total(val total: String) : ReportsViewModel()
    data class Billable(val billableData: BillableData) : ReportsViewModel()
    data class Amounts(val amounts: List<AmountsData>) : ReportsViewModel()
    data class BarChart(val info: BarChartInfo) : ReportsViewModel()
    data class DonutChart(val slices: List<DonutSlice>) : ReportsViewModel()
    data class DonutChartLegend(val legend: DonutChartLegendInfo) : ReportsViewModel()
    data class Error(val errorMessage: String) : ReportsViewModel()
    object AdvancedReportsOnWeb : ReportsViewModel()
}

data class BillableData(
    val totalBillableTime: String,
    val billablePercentage: Float
)

data class AmountsData(
    val amount: String,
    val currency: String
)

data class Bar(
    val filledValue: Double,
    val totalValue: Double
)

data class BarChartYLabels(
    val top: String,
    val middle: String,
    val bottom: String
)

data class BarChartInfo(
    val bars: List<Bar>,
    val maxValue: Double,
    val xLabels: List<String>,
    val yLabels: BarChartYLabels
)

data class DonutSlice(
    val label: String,
    val color: String,
    val percentage: Float,
    val startAngle: Float,
    val sweepAngle: Float
)

data class DonutChartLegendInfo(
    val projectName: String,
    val projectColorHex: String,
    val percentage: String,
    val duration: String
)
