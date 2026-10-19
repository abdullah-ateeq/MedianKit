package com.example.mediankit.model

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val maxValues: Int = 1000,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastDataset: Boolean = true,
    val displayPrecision: Int = 6
)
