package com.example.mediankit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mediankit.data.repository.MedianKitHistoryRepository
import com.example.mediankit.data.repository.SettingsRepository
import com.example.mediankit.ui.MedianKitViewModel
import com.example.mediankit.ui.navigation.MedianKitNavHost
import com.example.mediankit.ui.theme.MedianKitTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MedianKitViewModel by viewModels {
        val historyRepo = MedianKitHistoryRepository.create(applicationContext)
        val settingsRepo = SettingsRepository.create(applicationContext)
        MedianKitViewModel.provideFactory(historyRepo, settingsRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsState by viewModel.settingsState.collectAsState()
            MedianKitTheme(
                themeMode = settingsState.themeMode,
                dynamicColor = settingsState.dynamicColor
            ) {
                MedianKitNavHost(viewModel = viewModel)
            }
        }
    }
}