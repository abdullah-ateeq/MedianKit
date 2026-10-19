package com.example.mediankit.model

import java.math.BigDecimal

data class MedianKitResult(
    val count: Int,
    val median: BigDecimal,
    val modes: List<BigDecimal>,
    val modeFrequency: Int?,
    val minimum: BigDecimal,
    val maximum: BigDecimal,
    val range: BigDecimal,
    val q1: BigDecimal,
    val q3: BigDecimal,
    val sortedValues: List<BigDecimal>
)
