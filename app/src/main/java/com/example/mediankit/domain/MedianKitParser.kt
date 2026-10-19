package com.example.mediankit.domain

import java.math.BigDecimal

sealed interface ParseResult {
    data class Success(val values: List<BigDecimal>, val rawTokens: List<String>) : ParseResult
    data class Empty(val message: String = "Enter at least one number.") : ParseResult
    data class Error(val invalidToken: String, val message: String = "Invalid value: \"$invalidToken\"") : ParseResult
}

object MedianKitParser {
    /**
     * Parses the user input into a list of BigDecimal values.
     * Supports:
     * - Newlines (\n, \r\n)
     * - Comma separators
     * - Spaces and whitespace separators
     * - Negative numbers (-10, -5.5)
     * - Decimals (0.1, 5.25)
     * - Large numbers
     * Rejects any invalid tokens with an exact error message.
     */
    fun parse(input: String): ParseResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ParseResult.Empty()
        }

        // Split by commas, newlines, carriage returns, tabs, and spaces
        val tokens = trimmed
            .split(Regex("[,\\n\\r\\t ]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (tokens.isEmpty()) {
            return ParseResult.Empty()
        }

        val values = mutableListOf<BigDecimal>()
        for (token in tokens) {
            try {
                val value = BigDecimal(token)
                values.add(value)
            } catch (e: NumberFormatException) {
                return ParseResult.Error(invalidToken = token)
            }
        }

        return ParseResult.Success(values = values, rawTokens = tokens)
    }
}
