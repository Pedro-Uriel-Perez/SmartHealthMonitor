package mx.edu.utng.smarthealthmonitor.wear.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class WearDashboardViewModel : ViewModel() {
    val fc: StateFlow<Int> = WearDataStore.fcFlow
    val historial: StateFlow<List<WearLecturaFC>> = WearDataStore.historialFlow
}
