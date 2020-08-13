package com.toggl.reports.ui.composables

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.toRadians
import com.toggl.common.extensions.adjustForUserTheme
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.reports.R
import com.toggl.reports.domain.DonutSlice
import kotlin.math.cos
import kotlin.math.sin

private const val innerCircleFactor = 0.6F
private const val minimumSegmentPercentageToShowLabel = 0.04F
private const val sliceLabelCenterRadius = (1 + innerCircleFactor) / 2

@Composable
fun DonutChart(segments: List<DonutSlice>) {

    Column {
        GroupHeader(text = stringResource(R.string.projects))

        Box(gravity = ContentGravity.Center) {
            val innerCircleColor = MaterialTheme.colors.background
            val canvasModifier = Modifier.fillMaxWidth().aspectRatio(1F).padding(grid_3)

            val isInDarkTheme = isSystemInDarkTheme()
            val bounds = remember { Rect() }
            val textPaint = remember { Paint() }
            textPaint.textSize = 11.sp.value
            textPaint.color = MaterialTheme.colors.surface.toArgb()

            Canvas(modifier = canvasModifier) {
                val innerCircleRadius = this.size.width * innerCircleFactor / 2

                for (segment in segments) {

                    drawArc(
                        Color(segment.color.adjustForUserTheme(isInDarkTheme)),
                        segment.startAngle,
                        segment.sweepAngle,
                        true
                    )

                    if (segment.percentage < minimumSegmentPercentageToShowLabel)
                        continue

                    val labelAngle = (segment.startAngle + segment.sweepAngle / 2).toRadians()

                    val x = center.x * (1 + sliceLabelCenterRadius * cos(labelAngle))
                    val y = center.y * (1 + sliceLabelCenterRadius * sin(labelAngle))

                    val text = String.format("%.2f%%", segment.percentage)
                    drawCanvas { canvas, _ ->
                        textPaint.getTextBounds(text, 0, text.length, bounds)
                        val offsetY = bounds.height() / 2
                        canvas.nativeCanvas.drawText(text, x, y + offsetY, textPaint)
                    }
                }

                drawCircle(innerCircleColor, innerCircleRadius)
            }
        }
    }
}
