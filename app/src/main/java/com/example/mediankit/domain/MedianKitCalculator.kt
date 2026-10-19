package com.example.mediankit.domain

import com.example.mediankit.model.MedianKitResult
import java.math.BigDecimal

object MedianKitCalculator {
    /**
     * Sorts the dataset and calculates count, minimum, maximum, range, median, modes, Q1, and Q3.
     * All calculations are authoritative BigDecimal operations.
     */
    fun calculate(values: List<BigDecimal>): MedianKitResult {
        require(values.isNotEmpty()) { "Values cannot be empty" }
        // Sort ascending using BigDecimal.compareTo for exact numerical ordering
        val sortedValues = values.sortedWith { a, b -> a.compareTo(b) }
        val count = sortedValues.size
        val minimum = sortedValues.first()
        val maximum = sortedValues.last()
        val range = RangeCalculator.calculateRange(minimum, maximum)
        val quartiles = QuartileCalculator.calculateQuartiles(sortedValues)
        val modeResult = ModeCalculator.calculateModes(sortedValues)

        return MedianKitResult(
            count = count,
            median = quartiles.q2,
            modes = modeResult.modes,
            modeFrequency = modeResult.frequency,
            minimum = minimum,
            maximum = maximum,
            range = range,
            q1 = quartiles.q1,
            q3 = quartiles.q3,
            sortedValues = sortedValues
        )
    }
}
