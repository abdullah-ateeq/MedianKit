package com.example.mediankit.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mediankit.model.MedianKitUiState
import com.example.mediankit.model.SettingsState
import com.example.mediankit.ui.MedianKitViewModel
import com.example.mediankit.ui.home.components.BoxPlotVisual
import com.example.mediankit.ui.home.components.FiveNumberSummaryView
import com.example.mediankit.ui.home.components.InputSection
import com.example.mediankit.ui.home.components.QuartilesSection
import com.example.mediankit.ui.home.components.SortedValuesSection
import com.example.mediankit.ui.home.components.StatisticsCards
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    uiState: MedianKitUiState,
    settingsState: SettingsState,
    viewModel: MedianKitViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val performHaptic = {
        if (settingsState.hapticsEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Input Area
            InputSection(
                inputText = uiState.inputText,
                errorMessage = uiState.errorMessage,
                onInputChanged = viewModel::onInputChanged,
                onAnalyze = {
                    performHaptic()
                    viewModel.analyze()
                },
                onLoadSample = {
                    performHaptic()
                    viewModel.loadSample()
                },
                onLoadPreset = { preset ->
                    performHaptic()
                    viewModel.loadPreset(preset)
                },
                onClear = {
                    performHaptic()
                    viewModel.clearInput()
                }
            )

            // Results Section
            AnimatedVisibility(
                visible = uiState.result != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.result?.let { result ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Primary Cards (Median, Mode, Range, Count)
                        StatisticsCards(
                            result = result,
                            decimalPlaces = settingsState.displayPrecision
                        )

                        // 2. Dedicated Quartiles Section
                        QuartilesSection(
                            result = result,
                            decimalPlaces = settingsState.displayPrecision
                        )

                        // 3. Five-Number Summary
                        FiveNumberSummaryView(
                            result = result,
                            decimalPlaces = settingsState.displayPrecision
                        )

                        // 4. Box-Plot-Style Visual
                        BoxPlotVisual(
                            result = result,
                            decimalPlaces = settingsState.displayPrecision,
                            animationsEnabled = settingsState.animationsEnabled
                        )

                        // 5. Sorted Values Section
                        SortedValuesSection(
                            result = result,
                            decimalPlaces = settingsState.displayPrecision
                        )

                        // 6. Action Bar: Save, Copy, Share, Clear
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    performHaptic()
                                    val saved = viewModel.saveToHistory()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (saved) "Saved to history" else "Already saved"
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Save", maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    performHaptic()
                                    val copyText = viewModel.getCopyText()
                                    if (copyText != null) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("MedianKit Result", copyText)
                                        clipboard.setPrimaryClip(clip)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Result copied to clipboard")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Copy", maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    performHaptic()
                                    val shareText = viewModel.getShareText()
                                    if (shareText != null) {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share MedianKit Result")
                                        context.startActivity(shareIntent)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Share", maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    performHaptic()
                                    viewModel.clearInput()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Clear", maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Empty state placeholder if no result yet
            if (uiState.result == null && uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
