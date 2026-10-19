package com.example.mediankit.domain

import com.example.mediankit.model.ModeResult
import java.math.BigDecimal
import java.util.TreeMap

object ModeCalculator {
    /**
     * Calculates the mode(s) and frequency of a list of values.
     * Uses BigDecimal.compareTo for exact numerical equality (e.g. 1.5 == 1.50).
     * If every value occurs exactly once in a dataset with > 1 items, returns No Mode (emptyList).
     * Preserves sorted mode order.
     */
    fun calculateModes(values: List<BigDecimal>): ModeResult {
        if (values.isEmpty()) {
            return ModeResult(modes = emptyList(), frequency = null)
        }
        if (values.size == 1) {
            return ModeResult(modes = listOf(values[0]), frequency = 1)
        }

        val frequencyMap = TreeMap<BigDecimal, Int> { a, b -> a.compareTo(b) }
        for (v in values) {
            val count = frequencyMap[v] ?: 0
            frequencyMap[v] = count + 1
        }

        val maxFrequency = frequencyMap.values.maxOrNull() ?: 1

        // If every value occurs exactly once (and dataset size > 1): No mode
        if (maxFrequency == 1) {
            return ModeResult(modes = emptyList(), frequency = null)
        }

        val modes = frequencyMap.filter { it.value == maxFrequency }.keys.toList()
        return ModeResult(modes = modes, frequency = maxFrequency)
    }
}
