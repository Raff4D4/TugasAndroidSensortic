package com.zenspace.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zenspace.app.model.FocusCategory
import com.zenspace.app.viewmodel.ZenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: ZenViewModel,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSaveDialog by remember { mutableStateOf(false) }
    var sessionTitle by remember { mutableStateOf("") }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ZenSpace",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Teman Fokus & Belajar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262C36))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Cahaya", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    val lightText = if (!uiState.sensorData.isLightAvailable) "N/A"
                    else if (!uiState.sensorData.hasLightReading) "Menunggu..."
                    else String.format("%.0f lx", uiState.sensorData.lux)
                    
                    Text(
                        text = lightText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    val badgeText = if (!uiState.sensorData.isLightAvailable) "Tidak Tersedia" else if (!uiState.sensorData.hasLightReading) "Menunggu" else uiState.sensorData.lightStatus.label
                    val badgeColor = if (!uiState.sensorData.isLightAvailable || !uiState.sensorData.hasLightReading) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                    val badgeTextColor = if (!uiState.sensorData.isLightAvailable || !uiState.sensorData.hasLightReading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                    
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = badgeTextColor
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262C36))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Stabilitas", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    val accelText = if (!uiState.sensorData.isAccelAvailable) "N/A"
                    else if (!uiState.sensorData.hasAccelReading) "Menunggu..."
                    else String.format("%.2f m/s²", uiState.sensorData.accelDelta)
                    
                    Text(
                        text = accelText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    val isStable = uiState.sensorData.isStable
                    val sBadgeText = if (!uiState.sensorData.isAccelAvailable) "Tidak Tersedia" else if (!uiState.sensorData.hasAccelReading) "Menunggu" else if (isStable) "Tenang" else "Bergerak"
                    val sBadgeColor = if (!uiState.sensorData.isAccelAvailable || !uiState.sensorData.hasAccelReading) MaterialTheme.colorScheme.surfaceVariant 
                                      else if (isStable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    val sBadgeTextColor = if (!uiState.sensorData.isAccelAvailable || !uiState.sensorData.hasAccelReading) MaterialTheme.colorScheme.onSurfaceVariant 
                                          else if (isStable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    
                    Surface(
                        color = sBadgeColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = sBadgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = sBadgeTextColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            val adviceText = if (!uiState.sensorData.isLightAvailable) "Sensor cahaya tidak tersedia pada perangkat ini." else if (!uiState.sensorData.hasLightReading) "Menunggu pembacaan sensor cahaya..." else uiState.sensorData.lightStatus.advice
            Text(
                text = adviceText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        AnimatedVisibility(visible = uiState.showDistractionAlert) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gerakan berlebih terdeteksi! Fokuslah kembali.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.dismissDistractionAlert() }) {
                        Text("OK", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FocusCategory.values().forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(contentAlignment = Alignment.Center) {
                    val progress = if (uiState.targetMinutes > 0) {
                        uiState.elapsedSeconds.toFloat() / (uiState.targetMinutes * 60)
                    } else 0f
                    
                    CircularProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.size(240.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFF262C36),
                        strokeWidth = 12.dp
                    )
                    
                    val remainingSeconds = maxOf(0, (uiState.targetMinutes * 60) - uiState.elapsedSeconds)
                    val mm = remainingSeconds / 60
                    val ss = remainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mm, ss),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = uiState.targetMinutes == mins,
                            onClick = { viewModel.setTargetMinutes(mins) },
                            label = { Text("${mins}m") },
                            enabled = !uiState.isRunning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!uiState.isRunning) {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mulai")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Jeda", color = MaterialTheme.colorScheme.onTertiary)
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        viewModel.pauseTimer()
                        sessionTitle = "Sesi ${uiState.selectedCategory.label}"
                        showSaveDialog = true 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.elapsedSeconds >= 1
                ) {
                    Text("Simpan")
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Simpan Sesi") },
            text = {
                OutlinedTextField(
                    value = sessionTitle,
                    onValueChange = { sessionTitle = it },
                    label = { Text("Judul Sesi") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveSession(sessionTitle)
                    showSaveDialog = false
                    sessionTitle = ""
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
