package com.example.mediankit.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediankit.domain.MedianKitFormatter
import com.example.mediankit.model.MedianKitResult

@Composable
fun StatisticsCards(
    result: MedianKitResult,
    decimalPlaces: Int = 6,
    modifier: Modifier = Modifier
) {
    val formattedMedian = MedianKitFormatter.formatNumber(result.median, decimalPlaces)
    val formattedRange = MedianKitFormatter.formatNumber(result.range, decimalPlaces)

    val modeTitle: String
    val modeDisplay: String
    val modeSubtitle: String
    val modeContentDesc: String

    when {
        result.modes.isEmpty() -> {
            modeTitle = "MODE"
            modeDisplay = "No mode"
            modeSubtitle = "Every value occurs once"
            modeContentDesc = "No mode, every value occurs once."
        }
        result.modes.size == 1 -> {
            modeTitle = "MODE"
            val valStr = MedianKitFormatter.formatNumber(result.modes[0], decimalPlaces)
            modeDisplay = valStr
            val freq = result.modeFrequency ?: 1
            modeSubtitle = if (freq == 1) "Single item" else "Appears $freq times"
            modeContentDesc = "Mode value, $valStr, appears $freq times."
        }
        else -> {
            modeTitle = "MODES"
            val modesStr = result.modes.joinToString(" · ") { MedianKitFormatter.formatNumber(it, decimalPlaces) }
            modeDisplay = modesStr
            val freq = result.modeFrequency ?: 2
            modeSubtitle = "Each appears $freq times"
            modeContentDesc = "Modes, $modesStr, each appears $freq times."
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Median & Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // MEDIAN CARD (Hero)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Median value, $formattedMedian." },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "MEDIAN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AnimatedContent(
                        targetState = formattedMedian,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "median_val"
                    ) { target ->
                        Text(
                            text = target,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Middle value (Q2)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // MODE CARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = modeContentDesc },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = modeTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AnimatedContent(
                        targetState = modeDisplay,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "mode_val"
                    ) { target ->
                        Text(
                            text = target,
                            fontSize = if (target.length > 8) 22.sp else 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = modeSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Row 2: Range & Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // RANGE CARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Range, $formattedRange." },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "RANGE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AnimatedContent(
                        targetState = formattedRange,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "range_val"
                    ) { target ->
                        Text(
                            text = target,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Spread (Max − Min)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // COUNT CARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Count, ${result.count} values analyzed." },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "COUNT",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AnimatedContent(
                        targetState = result.count.toString(),
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "count_val"
                    ) { target ->
                        Text(
                            text = target,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${result.count} values analyzed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
