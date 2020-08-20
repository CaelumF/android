package com.toggl.reports.ui.composables

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.toggl.reports.R
import com.toggl.reports.domain.Bar
import com.toggl.reports.domain.BarChartYLabels
import com.toggl.reports.domain.ReportsViewModel
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private val maxWidth = 48.dp
private val barsRightMargin = 40.dp
private val barsLeftMargin = 12.dp
private val barsBottomMargin = 40.dp
private val barsTopMargin = 18.dp
private val textLeftMargin = 12.dp
private val textBottomMargin = 8.dp
private val bottomLabelMarginTop = 12.dp
private val dateTopPadding = 4.dp
private const val defaultBarSpacingRatio = 0.2f
private const val barDrawingYTranslationAdjustmentInPixels = 1f
private const val minHeightForBarsWithPercentages = 1f

@Composable
fun BarChart(
    viewModel: ReportsViewModel.BarChart
) {
    val willDrawIndividualLabels = viewModel.info.xLabels.size > 2
    val startDate = viewModel.info.xLabels.firstOrNull() ?: ""
    val endDate = viewModel.info.xLabels.lastOrNull() ?: ""
    val barsCount = viewModel.info.bars.size
    val context = ContextAmbient.current
    val emptyBarColor = ContextCompat.getColor(context, R.color.reports_bar_chart_empty_bar)
    val horizontalLineColor = ContextCompat.getColor(context, R.color.reports_bar_chart_horizontal_line)
    val hoursTextColor = ContextCompat.getColor(context, R.color.reports_bar_chart_hour_text)
    val xAxisLegendColor = ContextCompat.getColor(context, R.color.reports_bar_chart_x_legend)
    val filledBarColor = ContextCompat.getColor(context, R.color.reports_bar_chart_filled_bar)
    val regularBarColor = ContextCompat.getColor(context, R.color.reports_bar_chart_regular_bar)

    Column {
        val textPaint = remember {
            Paint().apply {
                color = Color.Blue.toArgb()
            }
        }
        val textBounds = remember {
            Rect()
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.End,
            verticalGravity = Alignment.CenterVertically,
        ) {
            Box(
                shape = CircleShape,
                backgroundColor = Color(filledBarColor),
                modifier = Modifier.size(6.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(context.getString(R.string.billable))
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                shape = CircleShape,
                backgroundColor = Color(regularBarColor),
                modifier = Modifier.size(6.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(context.getString(R.string.non_billable))
        }

        Box(
            gravity = Alignment.Center,
            modifier = Modifier.fillMaxWidth().padding(8.dp) then Modifier.height(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barsWidth = size.width - (barsLeftMargin + barsRightMargin).toPx()
                val barsHeight = size.height - (barsTopMargin + barsBottomMargin).toPx()
                val idealBarWidth = min(barsWidth / barsCount, maxWidth.toPx())
                val spaces = barsCount - 1
                val totalWidth = idealBarWidth * barsCount
                val remainingWidth = barsWidth - totalWidth
                val spacing = max(remainingWidth / barsCount, idealBarWidth * defaultBarSpacingRatio)
                val requiredWidth = totalWidth + spaces * spacing
                val actualBarWidth =
                    if (requiredWidth > barsWidth) idealBarWidth * (1 - defaultBarSpacingRatio) else idealBarWidth
                val middleHorizontalLineY = barsHeight / 2f + barsTopMargin.toPx()
                val barsBottom = size.height - barsBottomMargin.toPx()

                val hoursLabelsX = size.width - barsRightMargin.toPx() + textLeftMargin.toPx()
                val hoursBottomMargin = textBottomMargin.toPx()

                val startEndDatesY = barsBottom + bottomLabelMarginTop.toPx() * 2f
                val dayLabelsY = barsBottom + bottomLabelMarginTop.toPx() * 1.25f
                val barsStartingLeft =
                    barsLeftMargin.toPx() + (barsWidth - (actualBarWidth * barsCount + spaces * spacing)) / 2f

                drawHorizontalLines(
                    barsTopMargin = barsTopMargin.toPx(),
                    middleHorizontalLineY = middleHorizontalLineY,
                    barsBottom = barsBottom,
                    horizontalLineColor = horizontalLineColor
                )
                drawYAxisLegend(
                    hoursLabelX = hoursLabelsX,
                    barsBottom = barsBottom,
                    middleHorizontalLineY = middleHorizontalLineY,
                    barsTopMargin = barsTopMargin.toPx(),
                    hoursBottomMargin = hoursBottomMargin,
                    hoursTextColor = hoursTextColor,
                    textPaint = textPaint,
                    yLabels = viewModel.info.yLabels
                )
                drawXAxisLegendIfNotDrawingIndividualLabels(
                    startDate = startDate,
                    endDate = endDate,
                    barsLeftMargin = barsLeftMargin.toPx(),
                    barsRightMargin = barsRightMargin.toPx(),
                    startEndDatesY = startEndDatesY,
                    textPaint = textPaint,
                    willDrawIndividualLabels = willDrawIndividualLabels
                )
                drawBarsAndXAxisLegend(
                    barsStartingLeft = barsStartingLeft,
                    actualBarWidth = actualBarWidth,
                    emptyBarColor = emptyBarColor,
                    barsBottom = barsBottom,
                    barsHeight = barsHeight,
                    filledBarColor = filledBarColor,
                    regularBarColor = regularBarColor,
                    spacing = spacing,
                    willDrawIndividualLabels = willDrawIndividualLabels,
                    xAxisLegendColor = xAxisLegendColor,
                    dayLabelsY = dayLabelsY,
                    textSizeBounds = textBounds,
                    bars = viewModel.info.bars,
                    xLabels = viewModel.info.xLabels,
                    textPaint = textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawXAxisLegendIfNotDrawingIndividualLabels(
    startDate: String,
    endDate: String,
    barsLeftMargin: Float,
    barsRightMargin: Float,
    startEndDatesY: Float,
    textPaint: Paint,
    willDrawIndividualLabels: Boolean
) {
    if (willDrawIndividualLabels) return
    drawCanvas { canvas, size ->
        textPaint.textAlign = Paint.Align.LEFT
        canvas.nativeCanvas.drawText(startDate, barsLeftMargin, startEndDatesY, textPaint)
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.nativeCanvas.drawText(endDate, size.width - barsRightMargin, startEndDatesY, textPaint)
    }
}

private fun DrawScope.drawHorizontalLines(
    barsTopMargin: Float,
    middleHorizontalLineY: Float,
    barsBottom: Float,
    horizontalLineColor: Int
) {
    val color = Color(horizontalLineColor)
    val lineStrokeWidth = 1.dp.toPx()
    drawLine(color, Offset(0f, barsTopMargin), Offset(this.size.width, barsTopMargin), lineStrokeWidth)
    drawLine(color, Offset(0f, middleHorizontalLineY), Offset(this.size.width, middleHorizontalLineY), lineStrokeWidth)
    drawLine(color, Offset(0f, barsBottom), Offset(this.size.width, barsBottom), lineStrokeWidth)
}

private fun DrawScope.drawYAxisLegend(
    hoursLabelX: Float,
    barsBottom: Float,
    middleHorizontalLineY: Float,
    barsTopMargin: Float,
    hoursBottomMargin: Float,
    hoursTextColor: Int,
    textPaint: Paint,
    yLabels: BarChartYLabels
) {
    drawCanvas { canvas, _ ->
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 12.sp.toPx()
        textPaint.color = hoursTextColor
        val top = barsTopMargin - hoursBottomMargin
        val middle = middleHorizontalLineY - hoursBottomMargin
        val bottom = barsBottom - hoursBottomMargin
        canvas.nativeCanvas.run {
            drawText(yLabels.top, hoursLabelX, top, textPaint)
            drawText(yLabels.middle, hoursLabelX, middle, textPaint)
            drawText(yLabels.bottom, hoursLabelX, bottom, textPaint)
        }
    }
}

private fun DrawScope.drawBarsAndXAxisLegend(
    barsStartingLeft: Float,
    actualBarWidth: Float,
    emptyBarColor: Int,
    barsBottom: Float,
    barsHeight: Float,
    filledBarColor: Int,
    regularBarColor: Int,
    spacing: Float,
    willDrawIndividualLabels: Boolean,
    xAxisLegendColor: Int,
    dayLabelsY: Float,
    textSizeBounds: Rect,
    bars: List<Bar>,
    xLabels: List<String>,
    textPaint: Paint
) {
    val numberOfLabels = xLabels.size
    var left = barsStartingLeft
    val originalTextSize = textPaint.textSize
    textPaint.textAlign = Paint.Align.CENTER

    for (i in bars.indices) {
        val bar = bars[i]
        val barRight = left + actualBarWidth
        val barHasFilledPercentage = bar.filledValue > 0
        val barHasTransparentPercentage = bar.totalValue > bar.filledValue
        if (!barHasFilledPercentage && !barHasTransparentPercentage) {
            drawLine(Color(emptyBarColor), Offset(left, barsBottom), Offset(barRight, barsBottom))
        } else {
            val filledBarHeight = (barsHeight * bar.filledValue).toFloat()
            val filledTop = calculateFilledTop(barsBottom, filledBarHeight, barHasFilledPercentage)
            drawRect(
                Color(filledBarColor),
                Offset(left, filledTop),
                Size(actualBarWidth, filledBarHeight + barDrawingYTranslationAdjustmentInPixels)
            )

            val regularBarHeight = (barsHeight * (bar.totalValue - bar.filledValue)).toFloat()
            val regularTop = calculateRegularTop(filledTop, regularBarHeight, barHasTransparentPercentage)
            drawRect(
                Color(regularBarColor),
                Offset(left, regularTop),
                Size(actualBarWidth, regularBarHeight)
            )
        }

        if (willDrawIndividualLabels && i < numberOfLabels) {
            val horizontalLabelElements = xLabels[i].split("\n")
            if (horizontalLabelElements.size == 2) {
                textPaint.color = xAxisLegendColor
                val middleOfTheBar = left + (barRight - left) / 2f
                val dayOfWeekText = horizontalLabelElements[1]
                textPaint.textSize = originalTextSize
                drawCanvas { canvas, _ ->
                    canvas.nativeCanvas.drawText(dayOfWeekText, middleOfTheBar, dayLabelsY, textPaint)
                    val dateText = horizontalLabelElements[0]
                    textPaint.updatePaintForTextToFitWidth(dateText, actualBarWidth, textSizeBounds)
                    textPaint.getTextBounds(dateText, 0, dateText.length, textSizeBounds)
                    canvas.nativeCanvas.drawText(
                        dateText,
                        middleOfTheBar,
                        dayLabelsY + textSizeBounds.height() + dateTopPadding.toPx(),
                        textPaint
                    )
                }
            }
        }

        left += actualBarWidth + spacing
    }
}

private fun calculateFilledTop(barsBottom: Float, filledBarHeight: Float, barHasFilledPercentage: Boolean): Float {
    val filledTop = barsBottom - filledBarHeight + barDrawingYTranslationAdjustmentInPixels
    val barHasAtLeast1PixelInHeight = filledBarHeight >= minHeightForBarsWithPercentages

    return if (barHasFilledPercentage && !barHasAtLeast1PixelInHeight)
        filledTop - minHeightForBarsWithPercentages
    else
        filledTop
}

private fun calculateRegularTop(filledTop: Float, regularBarHeight: Float, barHasRegularPercentage: Boolean): Float {
    // Regular top doesn't need the extra Y translation because the filledTop accounts for it.
    val regularTop = filledTop - regularBarHeight
    val barHasAtLeast1PixelInHeight = regularBarHeight >= minHeightForBarsWithPercentages

    return if (barHasRegularPercentage && !barHasAtLeast1PixelInHeight)
        regularTop - minHeightForBarsWithPercentages
    else
        regularTop
}

private fun Paint.updatePaintForTextToFitWidth(text: String, allowedWidth: Float, textSizeBounds: Rect) {
    val sampleTextSize = 48f

    getTextBounds(text, 0, text.length, textSizeBounds)

    if (textSizeBounds.width() <= allowedWidth)
        return

    textSize = sampleTextSize
    getTextBounds(text, 0, text.length, textSizeBounds)
    textSize = floor(sampleTextSize * allowedWidth / textSizeBounds.width())
}
