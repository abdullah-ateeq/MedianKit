package com.example.mediankit.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mediankit.domain.MedianKitFormatter
import com.example.mediankit.model.MedianKitResult
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun BoxPlotVisual(
    result: MedianKitResult,
    decimalPlaces: Int = 6,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Box plot visualization: Minimum ${
                    MedianKitFormatter.formatNumber(result.minimum, decimalPlaces)
                }, Q1 ${
                    MedianKitFormatter.formatNumber(result.q1, decimalPlaces)
                }, Median ${
                    MedianKitFormatter.formatNumber(result.median, decimalPlaces)
                }, Q3 ${
                    MedianKitFormatter.formatNumber(result.q3, decimalPlaces)
                }, Maximum ${
                    MedianKitFormatter.formatNumber(result.maximum, decimalPlaces)
                }"
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BOX-AND-WHISKER VISUAL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tukey Method",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Compute ratios
            val range = result.range
            val isSinglePoint = range.compareTo(BigDecimal.ZERO) == 0

            val rawRatioQ1 = if (isSinglePoint) 0.5f else try {
                result.q1.subtract(result.minimum)
                    .divide(range, 4, RoundingMode.HALF_UP).toFloat().coerceIn(0f, 1f)
            } catch (_: Exception) { 0.25f }

            val rawRatioMed = if (isSinglePoint) 0.5f else try {
                result.median.subtract(result.minimum)
                    .divide(range, 4, RoundingMode.HALF_UP).toFloat().coerceIn(0f, 1f)
            } catch (_: Exception) { 0.5f }

            val rawRatioQ3 = if (isSinglePoint) 0.5f else try {
                result.q3.subtract(result.minimum)
                    .divide(range, 4, RoundingMode.HALF_UP).toFloat().coerceIn(0f, 1f)
            } catch (_: Exception) { 0.75f }

            val animDuration = if (animationsEnabled) 300 else 0
            val ratioQ1 by animateFloatAsState(targetValue = rawRatioQ1, animationSpec = tween(animDuration), label = "q1")
            val ratioMed by animateFloatAsState(targetValue = rawRatioMed, animationSpec = tween(animDuration), label = "med")
            val ratioQ3 by animateFloatAsState(targetValue = rawRatioQ3, animationSpec = tween(animDuration), label = "q3")

            val primaryColor = MaterialTheme.colorScheme.primary
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val outlineColor = MaterialTheme.colorScheme.outline
            val onSurfaceColor = MaterialTheme.colorScheme.onSurface

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                val padX = 24.dp.toPx()
                val availW = size.width - padX * 2
                val centerY = size.height / 2f
                val boxHeight = 36.dp.toPx()
                val boxTop = centerY - boxHeight / 2f

                if (isSinglePoint) {
                    val cx = padX + availW * 0.5f
                    // Draw a single prominent circle with pulse
                    drawCircle(
                        color = primaryContainer,
                        radius = 18.dp.toPx(),
                        center = Offset(cx, centerY)
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 8.dp.toPx(),
                        center = Offset(cx, centerY)
                    )
                } else {
                    val xMin = padX
                    val xMax = padX + availW
                    val xQ1 = padX + availW * ratioQ1
                    val xMed = padX + availW * ratioMed
                    val xQ3 = padX + availW * ratioQ3

                    val whiskerStroke = 3.dp.toPx()
                    val tickHeight = 20.dp.toPx()

                    // Whisker Min to Q1
                    drawLine(
                        color = outlineColor,
                        start = Offset(xMin, centerY),
                        end = Offset(xQ1, centerY),
                        strokeWidth = whiskerStroke
                    )
                    // Whisker Q3 to Max
                    drawLine(
                        color = outlineColor,
                        start = Offset(xQ3, centerY),
                        end = Offset(xMax, centerY),
                        strokeWidth = whiskerStroke
                    )

                    // Min tick
                    drawLine(
                        color = onSurfaceColor,
                        start = Offset(xMin, centerY - tickHeight / 2f),
                        end = Offset(xMin, centerY + tickHeight / 2f),
                        strokeWidth = whiskerStroke
                    )
                    // Max tick
                    drawLine(
                        color = onSurfaceColor,
                        start = Offset(xMax, centerY - tickHeight / 2f),
                        end = Offset(xMax, centerY + tickHeight / 2f),
                        strokeWidth = whiskerStroke
                    )

                    // Box (Q1 to Q3)
                    val boxW = (xQ3 - xQ1).coerceAtLeast(2.dp.toPx())
                    drawRoundRect(
                        color = primaryContainer,
                        topLeft = Offset(xQ1, boxTop),
                        size = Size(boxW, boxHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(xQ1, boxTop),
                        size = Size(boxW, boxHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Median Line (Accentuated)
                    drawLine(
                        color = tertiaryColor,
                        start = Offset(xMed, boxTop - 2.dp.toPx()),
                        end = Offset(xMed, boxTop + boxHeight + 2.dp.toPx()),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Values scale summary underneath
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryValueColumn(
                    label = "Min",
                    value = MedianKitFormatter.formatNumber(result.minimum, decimalPlaces),
                    align = Alignment.Start
                )
                SummaryValueColumn(
                    label = "Q1",
                    value = MedianKitFormatter.formatNumber(result.q1, decimalPlaces),
                    align = Alignment.CenterHorizontally
                )
                SummaryValueColumn(
                    label = "Median",
                    value = MedianKitFormatter.formatNumber(result.median, decimalPlaces),
                    align = Alignment.CenterHorizontally,
                    isMedian = true
                )
                SummaryValueColumn(
                    label = "Q3",
                    value = MedianKitFormatter.formatNumber(result.q3, decimalPlaces),
                    align = Alignment.CenterHorizontally
                )
                SummaryValueColumn(
                    label = "Max",
                    value = MedianKitFormatter.formatNumber(result.maximum, decimalPlaces),
                    align = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun SummaryValueColumn(
    label: String,
    value: String,
    align: Alignment.Horizontal,
    isMedian: Boolean = false
) {
    Column(horizontalAlignment = align) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isMedian) FontWeight.Bold else FontWeight.Medium,
            color = if (isMedian) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isMedian) FontWeight.Bold else FontWeight.Normal,
            color = if (isMedian) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
