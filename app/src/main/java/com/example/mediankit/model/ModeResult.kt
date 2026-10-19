package com.example.mediankit.model

import java.math.BigDecimal

data class ModeResult(
    val modes: List<BigDecimal>,
    val frequency: Int?
)
