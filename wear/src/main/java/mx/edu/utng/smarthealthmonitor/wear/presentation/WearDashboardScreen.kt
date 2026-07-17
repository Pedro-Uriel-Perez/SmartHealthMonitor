package mx.edu.utng.smarthealthmonitor.wear.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import kotlinx.coroutines.launch
import mx.edu.utng.smarthealthmonitor.wear.data.WearNeonRepository
import mx.edu.utng.smarthealthmonitor.wear.mqtt.MqttWearPublisher
import mx.edu.utng.smarthealthmonitor.wear.presentation.components.WearFCCard
import kotlin.random.Random

@Composable
fun WearDashboardScreen(
    onAlertClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    viewModel: WearDashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val listState = rememberScalingLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.scrollAway(listState))
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                WearFCCard(fc = fc, modifier = Modifier.fillMaxWidth())
            }
            item {
                Chip(
                    label = { Text("⚠ Alerta") },
                    onClick = onAlertClick,
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Chip(
                    label = { Text("📋 Historial") },
                    onClick = onHistorialClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Simula un cambio de FC (como movería el slider de Health Services):
                // actualiza la UI local, publica por MQTT (Sesión MQTT) y por Neon (esta sesión).
                Chip(
                    label = { Text("🔄 Simular FC") },
                    onClick = {
                        val bpm = Random.nextInt(55, 130)
                        val estado = when {
                            bpm < 60 -> "FC Baja"
                            bpm > 100 -> "FC Alta"
                            else -> "Normal"
                        }
                        WearDataStore.actualizarFC(bpm)
                        MqttWearPublisher.publishFC(bpm, estado)
                        scope.launch {
                            runCatching { WearNeonRepository.publicarLectura(bpm, estado) }
                                .onFailure { android.util.Log.w("WEAR", "Sin red / Neon: ${it.message}") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
