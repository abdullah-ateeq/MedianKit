package com.example.mediankit.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.mediankit.model.ThemeMode

class SettingsRepository(
    private val prefs: SharedPreferences
) {
    companion object {
        const val PREFS_NAME = "mediankit_settings_prefs"
        const val KEY_THEME = "theme_mode"
        const val KEY_DYNAMIC_COLOR = "dynamic_color"
        const val KEY_HAPTICS = "haptics"
        const val KEY_ANIMATIONS = "animations"
        const val KEY_MAX_VALUES = "max_values"
        const val KEY_CONFIRM_HISTORY_DELETE = "confirm_history_delete"
        const val KEY_PRESERVE_LAST_DATASET = "preserve_last_dataset"
        const val KEY_DISPLAY_PRECISION = "display_precision"
        const val KEY_LAST_INPUT = "last_input"

        const val DEFAULT_SAMPLE_DATA = "10\n20\n20\n30\n40\n50"

        fun create(context: Context): SettingsRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return SettingsRepository(prefs)
        }
    }

    fun getThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    fun isDynamicColorEnabled(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    fun isHapticsEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTICS, true)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
    }

    fun isAnimationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_ANIMATIONS, true)
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
    }

    fun getMaxValues(): Int {
        return prefs.getInt(KEY_MAX_VALUES, 1000)
    }

    fun setMaxValues(max: Int) {
        prefs.edit().putInt(KEY_MAX_VALUES, max).apply()
    }

    fun isConfirmHistoryDelete(): Boolean {
        return prefs.getBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
    }

    fun setConfirmHistoryDelete(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_HISTORY_DELETE, enabled).apply()
    }

    fun isPreserveLastDataset(): Boolean {
        return prefs.getBoolean(KEY_PRESERVE_LAST_DATASET, true)
    }

    fun setPreserveLastDataset(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRESERVE_LAST_DATASET, enabled).apply()
    }

    fun getDisplayPrecision(): Int {
        return prefs.getInt(KEY_DISPLAY_PRECISION, 6)
    }

    fun setDisplayPrecision(precision: Int) {
        prefs.edit().putInt(KEY_DISPLAY_PRECISION, precision).apply()
    }

    fun getLastInput(): String {
        return prefs.getString(KEY_LAST_INPUT, DEFAULT_SAMPLE_DATA) ?: DEFAULT_SAMPLE_DATA
    }

    fun setLastInput(input: String) {
        prefs.edit().putString(KEY_LAST_INPUT, input).apply()
    }

    fun resetSettings() {
        prefs.edit()
            .putString(KEY_THEME, ThemeMode.SYSTEM.name)
            .putBoolean(KEY_DYNAMIC_COLOR, true)
            .putBoolean(KEY_HAPTICS, true)
            .putBoolean(KEY_ANIMATIONS, true)
            .putInt(KEY_MAX_VALUES, 1000)
            .putBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
            .putBoolean(KEY_PRESERVE_LAST_DATASET, true)
            .putInt(KEY_DISPLAY_PRECISION, 6)
            .putString(KEY_LAST_INPUT, DEFAULT_SAMPLE_DATA)
            .apply()
    }
}
