package mx.edu.utng.smarthealthmonitor.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.smarthealthmonitor.data.remote.toLecturaFC
import mx.edu.utng.smarthealthmonitor.tv.data.TvNeonRepository

data class TvUiState(
    // Fila 1 — "Estado Actual (3 dispositivos)": promedio por dispositivo.
    val estadisticas: List<LecturaFC> = emptyList(),
    // Fila 2 — "Historial Completo": últimas 50 lecturas de los 3 dispositivos.
    val lecturas: List<LecturaFC> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TvViewModel : ViewModel() {

    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val lecturas = TvNeonRepository.obtenerHistorialCompleto(50)
                val stats = TvNeonRepository.obtenerEstadisticas()
                _state.update {
                    it.copy(
                        lecturas = lecturas.map { dto -> dto.toLecturaFC() },
                        estadisticas = stats.map { dto -> dto.toLecturaFC() },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun refresh() = cargarDatos()
}
