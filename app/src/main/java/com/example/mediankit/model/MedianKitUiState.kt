package com.example.mediankit.model

data class MedianKitUiState(
    val inputText: String = "",
    val result: MedianKitResult? = null,
    val history: List<MedianKitHistory> = emptyList(),
    val errorMessage: String? = null,
    val invalidToken: String? = null,
    val isCalculating: Boolean = false
)
