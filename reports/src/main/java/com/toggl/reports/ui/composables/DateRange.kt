package com.toggl.reports.ui.composables

import androidx.compose.foundation.Border
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.toggl.common.feature.compose.theme.grid_0_5
import com.toggl.common.feature.compose.theme.grid_1
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.reports.domain.ReportsAction

@Composable
fun DateRange(formattedDate: String, dispatcher: (ReportsAction) -> Unit) {

    Box(
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            shape = RoundedCornerShape(16.dp),
            gravity = ContentGravity.Center,
            border = Border(1.dp, MaterialTheme.colors.onBackground),
            modifier = Modifier
                .padding(vertical = grid_3)
                .clickable(indication = RippleIndication()) { dispatcher(ReportsAction.OpenDateRangePickerButtonTapped) }
                .clipToBounds()
        ) {
            Row(Modifier.padding(horizontal = grid_1, vertical = grid_0_5)) {
                Text(text = formattedDate)
                Image(asset = Icons.Filled.KeyboardArrowDown)
            }
        }
    }
}
