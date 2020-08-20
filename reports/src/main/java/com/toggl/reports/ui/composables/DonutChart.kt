package com.toggl.reports.ui.composables

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
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
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.toRadians
import com.toggl.common.extensions.adjustForUserTheme
import com.toggl.common.feature.compose.theme.grid_3
import com.toggl.reports.R
import com.toggl.reports.domain.DonutSlice
import kotlin.math.cos
import kotlin.math.sin

private const val arcAngleCorrection = 90F
private const val labelAngleCorrection = 270F
private const val innerCircleFactor = 0.6F
private const val minimumSegmentPercentageToShowLabel = 4F
private const val sliceLabelCenterRadius = (1 + innerCircleFactor) / 2
private const val start = "start"
private const val end = "end"

@Composable
fun DonutChart(slices: List<DonutSlice>) {

    Column {
        GroupHeader(text = stringResource(R.string.projects))

        Box(gravity = ContentGravity.Center) {
            val context = ContextAmbient.current
            val bounds = remember { Rect() }
            val textPaint = remember {
                Paint().apply {
                    textSize = context.resources.getDimension(R.dimen.reports_donut_chat_percentage)
                    color = Color.White.toArgb()
                }
            }

            val angleProperty = remember { FloatPropKey() }
            val donutTransitionDefinition = remember {
                transitionDefinition<String> {
                    state(start) { this[angleProperty] = 0f }
                    state(end) { this[angleProperty] = 360f }

                    transition(start to end) { angleProperty using tween() }
                }
            }

            val isInDarkTheme = isSystemInDarkTheme()
            val transitionState = transition(definition = donutTransitionDefinition, initState = start, toState = end)
            val backgroundColor = MaterialTheme.colors.background
            val surfaceColor = MaterialTheme.colors.surface

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .padding(grid_3)
            ) {
                val transitionAngle = transitionState[angleProperty]

                drawCircle(surfaceColor, size.width / 2)

                for (slice in slices) {
                    val sweepAngleDiff = slice.endAngle - transitionAngle
                    val sliceIsTheLastInAnimation = sweepAngleDiff >= 0
                    val actualSweepAngle =
                        if (!sliceIsTheLastInAnimation) slice.sweepAngle
                        else slice.sweepAngle - sweepAngleDiff

                    drawArc(
                        Color(slice.color.adjustForUserTheme(isInDarkTheme)),
                        slice.startAngle - arcAngleCorrection,
                        actualSweepAngle,
                        true
                    )

                    if (sliceIsTheLastInAnimation)
                        break
                }

                for (slice in slices.filterNot { it.isTooSmallToShowLabel() }) {
                    val text = String.format("%.2f%%", slice.percentage)
                    val labelAngle = (labelAngleCorrection + slice.startAngle + slice.sweepAngle / 2).toRadians()
                    val x = center.x * (1 + sliceLabelCenterRadius * cos(labelAngle))
                    val y = center.y * (1 + sliceLabelCenterRadius * sin(labelAngle))

                    drawCanvas { canvas, _ ->
                        textPaint.getTextBounds(text, 0, text.length, bounds)
                        val offsetX = bounds.width() / 2
                        val offsetY = bounds.height() / 2
                        canvas.nativeCanvas.drawText(text, x - offsetX, y + offsetY, textPaint)
                    }
                }

                drawCircle(backgroundColor, size.width * innerCircleFactor / 2)
            }
        }
    }
}

private val DonutSlice.endAngle get() = startAngle + sweepAngle

private fun DonutSlice.isTooSmallToShowLabel() =
    percentage < minimumSegmentPercentageToShowLabel
