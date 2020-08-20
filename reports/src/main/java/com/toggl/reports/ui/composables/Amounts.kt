package com.toggl.reports.ui.composables

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.reports.R
import com.toggl.reports.domain.AmountsData

@Composable
fun Amounts(amounts: List<AmountsData>) {
    Column {

        SectionHeader(text = stringResource(R.string.amount))

        for (amount in amounts) {
            val padding = if (amounts.indexOf(amount) == 0) 0.dp else grid_3
            Row(
                verticalGravity = Alignment.Bottom,
                modifier = Modifier.padding(top = padding)
            ) {
                Text(
                    text = amount.amount,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = grid_3)
                )

                Text(
                    text = amount.currency,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        SectionDivider()
    }
}
