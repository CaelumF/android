package com.toggl.reports.ui.composables

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.toggl.common.feature.compose.ThemedPreview
import com.toggl.common.feature.compose.theme.grid_0_5
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.reports.R
import com.toggl.reports.domain.BillableData

private const val billableChartWidth = 120

@Composable
fun Billable(billableData: BillableData) {

    ConstraintLayout {

        val (header, totals, percent, bar, filledBar, divider) = createRefs()

        SectionHeader(
            text = stringResource(R.string.billable),
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Text(
            text = billableData.totalBillableTime,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(top = grid_2).constrainAs(totals) {
                start.linkTo(parent.start)
                top.linkTo(header.bottom)
            }
        )

        Text(
            text = "${billableData.billablePercentage}%",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(top = grid_3).constrainAs(percent) {
                start.linkTo(bar.start)
                top.linkTo(totals.top)
                bottom.linkTo(bar.top)
            }
        )

        Box(
            backgroundColor = Color(0xFF999999),
            modifier = Modifier.padding(top = grid_0_5)
                .height(2.dp)
                .width(billableChartWidth.dp)
                .constrainAs(bar) {
                    end.linkTo(parent.end)
                    top.linkTo(percent.bottom)
                }
        )

        val filledWidth = (billableChartWidth / 100F) * billableData.billablePercentage

        Box(
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.height(4.dp).width(filledWidth.dp).constrainAs(filledBar) {
                top.linkTo(bar.top)
                start.linkTo(bar.start)
                bottom.linkTo(bar.bottom)
            }
        )

        SectionDivider(
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(totals.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

private val data = BillableData(
    totalBillableTime = "15:00:12",
    billablePercentage = 50.2F
)

@Preview("Billable light theme")
@Composable
fun PreviewBillableLight() {
    ThemedPreview {
        Billable(data)
    }
}

@Preview("Billable dark theme")
@Composable
fun PreviewBillableDark() {
    ThemedPreview(darkTheme = true) {
        Billable(data)
    }
}
