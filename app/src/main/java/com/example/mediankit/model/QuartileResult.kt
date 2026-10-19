package com.example.mediankit.model

import java.math.BigDecimal

data class QuartileResult(
    val q1: BigDecimal,
    val q2: BigDecimal,
    val q3: BigDecimal
)
