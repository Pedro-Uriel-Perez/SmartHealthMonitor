package mx.edu.utng.smarthealthmonitor.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository

data class TvUiState(
    val fc: Int = 0,
    val lecturas: List<LecturaFC> = emptyList()
)

class TvViewModel : ViewModel() {

    val state: StateFlow<TvUiState> = combine(
        SmartHealthRepository.fcFlow,
        SmartHealthRepository.obtenerHistorial()
    ) { fc, historial -> TvUiState(fc = fc, lecturas = historial) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TvUiState())
}
