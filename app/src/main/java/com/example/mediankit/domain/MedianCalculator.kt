package com.example.mediankit.domain

import java.math.BigDecimal
import java.math.RoundingMode

object MedianCalculator {
    /**
     * Calculates the median of a sorted list of BigDecimal values.
     * Odd count -> exact middle value.
     * Even count -> arithmetic mean of the two middle values.
     */
    fun calculateMedian(sortedValues: List<BigDecimal>): BigDecimal {
        require(sortedValues.isNotEmpty()) { "Values cannot be empty" }
        val n = sortedValues.size
        return if (n % 2 == 1) {
            sortedValues[n / 2]
        } else {
            val mid1 = sortedValues[n / 2 - 1]
            val mid2 = sortedValues[n / 2]
            val sum = mid1.add(mid2)
            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                val maxScale = maxOf(mid1.scale(), mid2.scale()) + 1
                sum.divide(BigDecimal(2), maxScale, RoundingMode.HALF_UP).stripTrailingZeros()
            }
        }
    }
}
