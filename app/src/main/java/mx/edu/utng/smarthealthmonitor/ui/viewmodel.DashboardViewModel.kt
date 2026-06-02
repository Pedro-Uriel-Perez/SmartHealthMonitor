package mx.edu.utng.smarthealthmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.edu.utng.smarthealthmonitor.data.models.MockData
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository

class DashboardViewModel : ViewModel() {

    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) MockData.fcActual else it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MockData.fcActual
        )

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .map { if (it == 0) MockData.pasosActual else it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MockData.pasosActual
        )

    val historial = MockData.historialFC
}