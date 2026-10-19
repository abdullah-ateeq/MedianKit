package com.example.mediankit.domain

import java.math.BigDecimal

object RangeCalculator {
    /**
     * Calculates the range of a dataset.
     * Formula: maximum - minimum
     */
    fun calculateRange(minimum: BigDecimal, maximum: BigDecimal): BigDecimal {
        return maximum.subtract(minimum)
    }
}
