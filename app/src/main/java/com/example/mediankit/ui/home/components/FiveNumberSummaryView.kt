package com.example.mediankit.ui.home.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediankit.domain.MedianKitFormatter
import com.example.mediankit.model.MedianKitResult

@Composable
fun FiveNumberSummaryView(
    result: MedianKitResult,
    decimalPlaces: Int = 6,
    modifier: Modifier = Modifier
) {
    val minStr = MedianKitFormatter.formatNumber(result.minimum, decimalPlaces)
    val q1Str = MedianKitFormatter.formatNumber(result.q1, decimalPlaces)
    val medStr = MedianKitFormatter.formatNumber(result.median, decimalPlaces)
    val q3Str = MedianKitFormatter.formatNumber(result.q3, decimalPlaces)
    val maxStr = MedianKitFormatter.formatNumber(result.maximum, decimalPlaces)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Five-number summary: Minimum $minStr, First Quartile $q1Str, Median $medStr, Third Quartile $q3Str, Maximum $maxStr."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "FIVE-NUMBER SUMMARY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryNode(name = "MIN", value = minStr, isAccent = false)
                SummaryConnector()
                SummaryNode(name = "Q1", value = q1Str, isAccent = false)
                SummaryConnector()
                SummaryNode(name = "MEDIAN", value = medStr, isAccent = true)
                SummaryConnector()
                SummaryNode(name = "Q3", value = q3Str, isAccent = false)
                SummaryConnector()
                SummaryNode(name = "MAX", value = maxStr, isAccent = false)
            }
        }
    }
}

@Composable
private fun SummaryNode(
    name: String,
    value: String,
    isAccent: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isAccent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAccent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isAccent) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isAccent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SummaryConnector() {
    Box(
        modifier = Modifier
            .height(2.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            .padding(horizontal = 2.dp)
    )
}
