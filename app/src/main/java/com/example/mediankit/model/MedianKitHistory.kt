package com.example.mediankit.model

data class MedianKitHistory(
    val id: String,
    val inputValues: List<String>,
    val count: Int,
    val median: String,
    val modes: List<String>,
    val modeFrequency: Int?,
    val minimum: String,
    val maximum: String,
    val range: String,
    val q1: String,
    val q3: String,
    val calculatedAt: Long
)
