package com.example.mediankit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mediankit.data.repository.MedianKitHistoryRepository
import com.example.mediankit.data.repository.SettingsRepository
import com.example.mediankit.domain.MedianKitCalculator
import com.example.mediankit.domain.MedianKitFormatter
import com.example.mediankit.domain.MedianKitParser
import com.example.mediankit.domain.MedianKitValidator
import com.example.mediankit.domain.ParseResult
import com.example.mediankit.domain.ValidationResult
import com.example.mediankit.model.CalculationState
import com.example.mediankit.model.MedianKitHistory
import com.example.mediankit.model.MedianKitUiState
import com.example.mediankit.model.SettingsState
import com.example.mediankit.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MedianKitViewModel(
    private val historyRepository: MedianKitHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedianKitUiState())
    val uiState: StateFlow<MedianKitUiState> = _uiState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _calculationState = MutableStateFlow<CalculationState>(CalculationState.Idle)
    val calculationState: StateFlow<CalculationState> = _calculationState.asStateFlow()

    private var lastRawTokens: List<String> = emptyList()

    init {
        loadSettings()
        loadHistory()
        initInitialData()
    }

    private fun loadSettings() {
        _settingsState.value = SettingsState(
            themeMode = settingsRepository.getThemeMode(),
            dynamicColor = settingsRepository.isDynamicColorEnabled(),
            hapticsEnabled = settingsRepository.isHapticsEnabled(),
            animationsEnabled = settingsRepository.isAnimationsEnabled(),
            maxValues = settingsRepository.getMaxValues(),
            confirmHistoryDelete = settingsRepository.isConfirmHistoryDelete(),
            preserveLastDataset = settingsRepository.isPreserveLastDataset(),
            displayPrecision = settingsRepository.getDisplayPrecision()
        )
    }

    private fun loadHistory() {
        val historyList = historyRepository.getHistory()
        _uiState.update { it.copy(history = historyList) }
    }

    private fun initInitialData() {
        val initialInput = if (settingsRepository.isPreserveLastDataset()) {
            settingsRepository.getLastInput()
        } else {
            SettingsRepository.DEFAULT_SAMPLE_DATA
        }
        onInputChanged(initialInput)
    }

    fun onInputChanged(newText: String) {
        val currentMax = _settingsState.value.maxValues
        _uiState.update { it.copy(inputText = newText, isCalculating = true) }

        if (_settingsState.value.preserveLastDataset) {
            settingsRepository.setLastInput(newText)
        }

        when (val parseResult = MedianKitParser.parse(newText)) {
            is ParseResult.Empty -> {
                lastRawTokens = emptyList()
                _uiState.update {
                    it.copy(
                        result = null,
                        errorMessage = parseResult.message,
                        invalidToken = null,
                        isCalculating = false
                    )
                }
                _calculationState.value = CalculationState.Error(parseResult.message)
            }
            is ParseResult.Error -> {
                lastRawTokens = emptyList()
                _uiState.update {
                    it.copy(
                        result = null,
                        errorMessage = parseResult.message,
                        invalidToken = parseResult.invalidToken,
                        isCalculating = false
                    )
                }
                _calculationState.value = CalculationState.Error(parseResult.message)
            }
            is ParseResult.Success -> {
                lastRawTokens = parseResult.rawTokens
                when (val valResult = MedianKitValidator.validate(parseResult.values, currentMax)) {
                    is ValidationResult.Invalid -> {
                        _uiState.update {
                            it.copy(
                                result = null,
                                errorMessage = valResult.message,
                                invalidToken = valResult.invalidToken,
                                isCalculating = false
                            )
                        }
                        _calculationState.value = CalculationState.Error(valResult.message)
                    }
                    is ValidationResult.Valid -> {
                        try {
                            val result = MedianKitCalculator.calculate(parseResult.values)
                            _uiState.update {
                                it.copy(
                                    result = result,
                                    errorMessage = null,
                                    invalidToken = null,
                                    isCalculating = false
                                )
                            }
                            _calculationState.value = CalculationState.Calculated
                        } catch (e: Exception) {
                            val msg = e.message ?: "Calculation error"
                            _uiState.update {
                                it.copy(
                                    result = null,
                                    errorMessage = msg,
                                    invalidToken = null,
                                    isCalculating = false
                                )
                            }
                            _calculationState.value = CalculationState.Error(msg)
                        }
                    }
                }
            }
        }
    }

    fun analyze() {
        onInputChanged(_uiState.value.inputText)
    }

    fun loadSample() {
        onInputChanged(SettingsRepository.DEFAULT_SAMPLE_DATA)
    }

    fun loadPreset(presetText: String) {
        onInputChanged(presetText)
    }

    fun clearInput() {
        onInputChanged("")
    }

    fun saveToHistory(): Boolean {
        val result = _uiState.value.result ?: return false
        val tokensToSave = if (lastRawTokens.isNotEmpty()) {
            lastRawTokens
        } else {
            result.sortedValues.map { it.toPlainString() }
        }

        val item = MedianKitHistory(
            id = UUID.randomUUID().toString(),
            inputValues = tokensToSave,
            count = result.count,
            median = result.median.toPlainString(),
            modes = result.modes.map { it.toPlainString() },
            modeFrequency = result.modeFrequency,
            minimum = result.minimum.toPlainString(),
            maximum = result.maximum.toPlainString(),
            range = result.range.toPlainString(),
            q1 = result.q1.toPlainString(),
            q3 = result.q3.toPlainString(),
            calculatedAt = System.currentTimeMillis()
        )

        val updated = historyRepository.saveCalculation(item)
        _uiState.update { it.copy(history = updated) }
        return true
    }

    fun restoreHistory(item: MedianKitHistory) {
        val restoredText = item.inputValues.joinToString("\n")
        onInputChanged(restoredText)
    }

    fun deleteHistoryItem(id: String) {
        val updated = historyRepository.deleteCalculation(id)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearAllHistory() {
        historyRepository.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun updateTheme(theme: ThemeMode) {
        settingsRepository.setThemeMode(theme)
        _settingsState.update { it.copy(themeMode = theme) }
    }

    fun updateDynamicColor(enabled: Boolean) {
        settingsRepository.setDynamicColorEnabled(enabled)
        _settingsState.update { it.copy(dynamicColor = enabled) }
    }

    fun updateHaptics(enabled: Boolean) {
        settingsRepository.setHapticsEnabled(enabled)
        _settingsState.update { it.copy(hapticsEnabled = enabled) }
    }

    fun updateAnimations(enabled: Boolean) {
        settingsRepository.setAnimationsEnabled(enabled)
        _settingsState.update { it.copy(animationsEnabled = enabled) }
    }

    fun updateMaxValues(max: Int) {
        settingsRepository.setMaxValues(max)
        _settingsState.update { it.copy(maxValues = max) }
        // Revalidate with new maximum
        onInputChanged(_uiState.value.inputText)
    }

    fun updateConfirmHistoryDelete(enabled: Boolean) {
        settingsRepository.setConfirmHistoryDelete(enabled)
        _settingsState.update { it.copy(confirmHistoryDelete = enabled) }
    }

    fun updatePreserveLastDataset(enabled: Boolean) {
        settingsRepository.setPreserveLastDataset(enabled)
        _settingsState.update { it.copy(preserveLastDataset = enabled) }
    }

    fun updateDisplayPrecision(precision: Int) {
        settingsRepository.setDisplayPrecision(precision)
        _settingsState.update { it.copy(displayPrecision = precision) }
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
        loadSettings()
        loadSample()
    }

    fun getCopyText(): String? {
        val result = _uiState.value.result ?: return null
        return MedianKitFormatter.formatCopyText(result, _settingsState.value.displayPrecision)
    }

    fun getShareText(): String? {
        val result = _uiState.value.result ?: return null
        return MedianKitFormatter.formatShareText(result, _settingsState.value.displayPrecision)
    }

    companion object {
        fun provideFactory(
            historyRepository: MedianKitHistoryRepository,
            settingsRepository: SettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MedianKitViewModel(historyRepository, settingsRepository) as T
            }
        }
    }
}
