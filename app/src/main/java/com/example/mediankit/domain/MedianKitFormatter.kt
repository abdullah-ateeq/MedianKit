package com.example.mediankit.domain

import com.example.mediankit.model.MedianKitResult
import java.math.BigDecimal
import java.math.RoundingMode

object MedianKitFormatter {
    /**
     * Formats a BigDecimal number up to [decimalPlaces] places.
     * Removes unnecessary trailing zeros.
     */
    fun formatNumber(value: BigDecimal, decimalPlaces: Int = 6): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }
        val rounded = value.setScale(decimalPlaces, RoundingMode.HALF_UP)
        return rounded.stripTrailingZeros().toPlainString()
    }

    /**
     * Formats a list of mode BigDecimal values into a readable string.
     */
    fun formatModes(modes: List<BigDecimal>, decimalPlaces: Int = 6): String {
        return when {
            modes.isEmpty() -> "No mode"
            modes.size == 1 -> formatNumber(modes[0], decimalPlaces)
            else -> modes.joinToString(", ") { formatNumber(it, decimalPlaces) }
        }
    }

    /**
     * Formats the five-number summary: Min · Q1 · Median · Q3 · Max
     */
    fun formatFiveNumberSummary(result: MedianKitResult, decimalPlaces: Int = 6): String {
        val min = formatNumber(result.minimum, decimalPlaces)
        val q1 = formatNumber(result.q1, decimalPlaces)
        val med = formatNumber(result.median, decimalPlaces)
        val q3 = formatNumber(result.q3, decimalPlaces)
        val max = formatNumber(result.maximum, decimalPlaces)
        return "$min · $q1 · $med · $q3 · $max"
    }

    /**
     * Formats the result for clipboard copy.
     */
    fun formatCopyText(result: MedianKitResult, decimalPlaces: Int = 6): String {
        val valuesStr = result.sortedValues.joinToString(", ") { formatNumber(it, decimalPlaces) }
        val modeLine = when {
            result.modes.isEmpty() -> "Mode: No mode"
            result.modes.size == 1 -> "Mode: ${formatNumber(result.modes[0], decimalPlaces)}"
            else -> "Modes: ${result.modes.joinToString(", ") { formatNumber(it, decimalPlaces) }}"
        }

        return buildString {
            appendLine("MedianKit Result")
            appendLine()
            appendLine("Values: $valuesStr")
            appendLine()
            appendLine("Count: ${result.count}")
            appendLine("Median: ${formatNumber(result.median, decimalPlaces)}")
            appendLine(modeLine)
            appendLine("Minimum: ${formatNumber(result.minimum, decimalPlaces)}")
            appendLine("Maximum: ${formatNumber(result.maximum, decimalPlaces)}")
            appendLine("Range: ${formatNumber(result.range, decimalPlaces)}")
            appendLine()
            appendLine("Q1: ${formatNumber(result.q1, decimalPlaces)}")
            appendLine("Q2 / Median: ${formatNumber(result.median, decimalPlaces)}")
            append("Q3: ${formatNumber(result.q3, decimalPlaces)}")
        }
    }

    /**
     * Formats the result for sharing via Intent.ACTION_SEND.
     */
    fun formatShareText(result: MedianKitResult, decimalPlaces: Int = 6): String {
        val modeLine = when {
            result.modes.isEmpty() -> "Mode: No mode"
            result.modes.size == 1 -> "Mode: ${formatNumber(result.modes[0], decimalPlaces)}"
            else -> "Modes: ${result.modes.joinToString(", ") { formatNumber(it, decimalPlaces) }}"
        }

        return buildString {
            appendLine("MedianKit")
            appendLine()
            appendLine("${result.count} values analyzed")
            appendLine()
            appendLine("Median: ${formatNumber(result.median, decimalPlaces)}")
            appendLine(modeLine)
            appendLine("Range: ${formatNumber(result.range, decimalPlaces)}")
            appendLine()
            appendLine("Q1: ${formatNumber(result.q1, decimalPlaces)}")
            appendLine("Q2: ${formatNumber(result.median, decimalPlaces)}")
            append("Q3: ${formatNumber(result.q3, decimalPlaces)}")
        }
    }
}
