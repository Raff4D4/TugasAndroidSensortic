package com.zenspace.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenspace.app.data.DatabaseHelper
import com.zenspace.app.model.FocusCategory
import com.zenspace.app.model.FocusSession
import com.zenspace.app.model.SensorData
import com.zenspace.app.model.SessionStats
import com.zenspace.app.sensor.SensorHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ZenUiState(
    val sensorData: SensorData = SensorData(),
    val isRunning: Boolean = false,
    val elapsedSeconds: Int = 0,
    val targetMinutes: Int = 25,
    val selectedCategory: FocusCategory = FocusCategory.STUDY,
    val distractionCount: Int = 0,
    val showDistractionAlert: Boolean = false,
    val sessions: List<FocusSession> = emptyList(),
    val stats: SessionStats = SessionStats(),
    val selectedFilter: String = "Semua",
    val snackbarMessage: String? = null
)

class ZenViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val sensorHelper = SensorHelper(application)

    private val _uiState = MutableStateFlow(ZenUiState())
    val uiState: StateFlow<ZenUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var luxSum = 0f
    private var luxSamples = 0
    private var lastDistractionTime = 0L

    init {
        viewModelScope.launch {
            sensorHelper.sensorData.collect { sensor ->
                _uiState.update { it.copy(sensorData = sensor) }
                
                if (_uiState.value.isRunning && sensor.hasAccelReading && sensor.accelDelta >= 2.2f) {
                    val now = System.currentTimeMillis()
                    if (now - lastDistractionTime >= 3000L) {
                        lastDistractionTime = now
                        _uiState.update { 
                            it.copy(
                                distractionCount = it.distractionCount + 1,
                                showDistractionAlert = true
                            ) 
                        }
                    }
                }
            }
        }
        loadHistory()
    }

    fun startSensors() {
        sensorHelper.startListening()
    }

    fun stopSensors() {
        sensorHelper.stopListening()
    }

    fun setTargetMinutes(minutes: Int) {
        _uiState.update { it.copy(targetMinutes = minutes) }
    }

    fun setCategory(category: FocusCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun startTimer() {
        if (uiState.value.isRunning) return
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentSensor = _uiState.value.sensorData
                if (currentSensor.hasLightReading) {
                    luxSum += currentSensor.lux
                    luxSamples += 1
                }
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        luxSum = 0f
        luxSamples = 0
        _uiState.update { 
            it.copy(
                isRunning = false,
                elapsedSeconds = 0,
                distractionCount = 0,
                showDistractionAlert = false
            ) 
        }
    }

    fun dismissDistractionAlert() {
        _uiState.update { it.copy(showDistractionAlert = false) }
    }
    
    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun saveSession(customTitle: String) {
        pauseTimer()
        val currentState = uiState.value
        if (currentState.elapsedSeconds < 1) return

        val avgLux = if (luxSamples > 0) (luxSum / luxSamples) else currentState.sensorData.lux

        val title = if (customTitle.isBlank()) "Sesi ${currentState.selectedCategory.label}" else customTitle
        val stabilityScore = (100 - currentState.distractionCount * 12).coerceIn(20, 100)

        val tempSensor = SensorData(lux = avgLux)
        val status = tempSensor.lightStatus.label

        val session = FocusSession(
            title = title,
            category = currentState.selectedCategory.label,
            durationSeconds = currentState.elapsedSeconds,
            averageLux = avgLux,
            lightStatus = status,
            distractionCount = currentState.distractionCount,
            stabilityScore = stabilityScore
        )

        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.insertSession(session)
            withContext(Dispatchers.Main) {
                resetTimer()
                loadHistory()
                _uiState.update { it.copy(snackbarMessage = "Sesi berhasil disimpan") }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = dbHelper.getAllSessions(uiState.value.selectedFilter)
            val stats = dbHelper.getStats()
            _uiState.update { it.copy(sessions = sessions, stats = stats) }
        }
    }

    fun selectFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        loadHistory()
    }

    fun updateSessionTitle(id: Long, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.updateSessionTitle(id, newTitle)
            loadHistory()
            _uiState.update { it.copy(snackbarMessage = "Judul sesi berhasil diperbarui") }
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.deleteSession(id)
            loadHistory()
            _uiState.update { it.copy(snackbarMessage = "Sesi berhasil dihapus") }
        }
    }

    fun clearAllSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.clearAllSessions()
            loadHistory()
            _uiState.update { it.copy(snackbarMessage = "Semua sesi berhasil dihapus") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorHelper.stopListening()
    }
}
