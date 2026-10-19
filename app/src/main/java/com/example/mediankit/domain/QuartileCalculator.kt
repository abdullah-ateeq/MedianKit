package com.example.mediankit.domain

import com.example.mediankit.model.QuartileResult
import java.math.BigDecimal

object QuartileCalculator {
    /**
     * Calculates Q1, Q2 (Median), and Q3 using the Tukey / median-of-halves quartile method.
     * - Q2 is the overall median.
     * - If n == 1: Q1 = Q2 = Q3 = sortedValues[0]
     * - Even count: splits the dataset into equal halves.
     * - Odd count:
     *   - For datasets of form 4k + 3 (e.g. 3, 7, 11): excludes the overall median from both halves.
     *   - For datasets of form 4k + 1 (e.g. 5, 9, 13): includes the median in both halves (Tukey's hinges).
     */
    fun calculateQuartiles(sortedValues: List<BigDecimal>): QuartileResult {
        require(sortedValues.isNotEmpty()) { "Values cannot be empty" }
        val n = sortedValues.size
        val q2 = MedianCalculator.calculateMedian(sortedValues)

        if (n == 1) {
            return QuartileResult(q1 = sortedValues[0], q2 = q2, q3 = sortedValues[0])
        }

        val (lowerHalf, upperHalf) = when {
            n % 2 == 0 -> {
                val midIndex = n / 2
                Pair(
                    sortedValues.subList(0, midIndex),
                    sortedValues.subList(midIndex, n)
                )
            }
            n % 4 == 3 -> {
                // Exclude median from both halves (e.g. n = 3, 7)
                val midIndex = n / 2
                Pair(
                    sortedValues.subList(0, midIndex),
                    sortedValues.subList(midIndex + 1, n)
                )
            }
            else -> {
                // n % 4 == 1 (e.g. n = 5, 9): Tukey hinges include median in both halves
                val midIndex = n / 2
                Pair(
                    sortedValues.subList(0, midIndex + 1),
                    sortedValues.subList(midIndex, n)
                )
            }
        }

        val q1 = MedianCalculator.calculateMedian(lowerHalf)
        val q3 = MedianCalculator.calculateMedian(upperHalf)

        return QuartileResult(q1 = q1, q2 = q2, q3 = q3)
    }
}
