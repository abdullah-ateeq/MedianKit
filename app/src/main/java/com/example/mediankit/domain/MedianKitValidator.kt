package com.example.mediankit.domain

import java.math.BigDecimal

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val message: String, val invalidToken: String? = null) : ValidationResult
}

object MedianKitValidator {
    const val DEFAULT_MAX_COUNT = 1000

    /**
     * Validates parsed values against dataset constraints.
     */
    fun validate(values: List<BigDecimal>, maxAllowed: Int = DEFAULT_MAX_COUNT): ValidationResult {
        if (values.isEmpty()) {
            return ValidationResult.Invalid("Enter at least one number.")
        }
        if (values.size > maxAllowed) {
            return ValidationResult.Invalid("Maximum 1,000 numbers allowed.")
        }
        return ValidationResult.Valid
    }

    /**
     * Complete validation pipeline directly from raw text input.
     */
    fun validateInput(input: String, maxAllowed: Int = DEFAULT_MAX_COUNT): Pair<ValidationResult, List<BigDecimal>> {
        return when (val parseResult = MedianKitParser.parse(input)) {
            is ParseResult.Empty -> Pair(ValidationResult.Invalid(parseResult.message), emptyList())
            is ParseResult.Error -> Pair(ValidationResult.Invalid(parseResult.message, parseResult.invalidToken), emptyList())
            is ParseResult.Success -> {
                val valResult = validate(parseResult.values, maxAllowed)
                Pair(valResult, parseResult.values)
            }
        }
    }
}
