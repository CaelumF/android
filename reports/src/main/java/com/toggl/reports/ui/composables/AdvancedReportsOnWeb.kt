package com.toggl.reports.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toggl.common.feature.compose.theme.grid_2
import com.toggl.reports.R
import com.toggl.reports.domain.ReportsAction

@Composable
fun AdvancedReportsOnWeb(dispatcher: (ReportsAction) -> Unit) {

    ConstraintLayout(Modifier.fillMaxWidth()) {

        val (header, text, callToAction, image) = createRefs()

        GroupHeader(
            text = stringResource(R.string.advanced_reports),
            modifier = Modifier.padding(top = grid_2).constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Text(
            text = stringResource(R.string.advanced_reports_message),
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(top = grid_2).constrainAs(text) {
                start.linkTo(parent.start)
                top.linkTo(header.bottom)
            }
        )

        Text(
            text = stringResource(R.string.available_on_other_plans),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = { dispatcher(ReportsAction.AvailableOnOtherPlansTapped) })
                .padding(top = grid_2, bottom = grid_2)
                .constrainAs(callToAction) {
                    start.linkTo(parent.start)
                    top.linkTo(text.bottom)
                }
        )

        Image(
            asset = imageResource(id = R.drawable.illustration_graph),
            modifier = Modifier.width(120.dp).height(90.dp).constrainAs(image) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        )
    }
}
