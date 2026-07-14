package mx.edu.utng.smarthealthmonitor.wear.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WearDataStore {
    private val _fcFlow = MutableStateFlow(72)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    private val _historialFlow = MutableStateFlow(
        listOf(
            WearLecturaFC(1, 72, "08:00"),
            WearLecturaFC(2, 85, "09:30"),
            WearLecturaFC(3, 110, "11:00"),
            WearLecturaFC(4, 68, "13:15"),
            WearLecturaFC(5, 95, "15:45"),
        )
    )
    val historialFlow: StateFlow<List<WearLecturaFC>> = _historialFlow.asStateFlow()

    fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm
    }
}
