package com.toggl.reports.ui.composables

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.adjustForUserTheme
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.reports.domain.DonutChartLegendInfo

@Composable
fun DonutChartLegend(legend: DonutChartLegendInfo) {
    Row(
        modifier = Modifier.padding(top = grid_2, bottom = grid_2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalGravity = Alignment.CenterVertically
    ) {
        val projectColor = legend.projectColorHex.adjustForUserTheme()

        Box(
            shape = CircleShape,
            backgroundColor = projectColor,
            modifier = Modifier.size(10.dp)
        )

        Text(
            text = legend.projectName,
            modifier = Modifier.weight(8F, fill = true)
                .then(Modifier.padding(start = grid_2))
        )

        Text(
            text = legend.percentage,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(3F)
        )

        Text(
            text = legend.duration,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(4F)
        )
    }

    Divider()
}

private val data1 = DonutChartLegendInfo(
    projectName = "Time management",
    projectColorHex = "#06AAF5",
    percentage = "80%",
    duration = "16:12:00"
)

private val data2 = DonutChartLegendInfo(
    "Important timesheets",
    "#EA468D",
    "15%",
    "9:00:00"
)

private val data3 = DonutChartLegendInfo(
    "Mobile apps",
    "#F1C33F",
    "5%",
    "7:12:00"
)

@Preview("DonutChartLegend light theme")
@Composable
fun PreviewDonutChartLegendLight() {
    ThemedPreview {
        Column {
            DonutChartLegend(data1)
            DonutChartLegend(data2)
            DonutChartLegend(data3)
        }
    }
}

@Preview("DonutChartLegend dark theme")
@Composable
fun PreviewDonutChartLegendDark() {
    ThemedPreview(darkTheme = true) {
        Column {
            DonutChartLegend(data1)
            DonutChartLegend(data2)
            DonutChartLegend(data3)
        }
    }
}
