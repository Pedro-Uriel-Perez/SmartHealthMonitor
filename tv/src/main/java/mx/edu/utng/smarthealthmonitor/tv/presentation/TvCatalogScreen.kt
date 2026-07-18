package mx.edu.utng.smarthealthmonitor.tv.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.smarthealthmonitor.tv.TvViewModel
import mx.edu.utng.smarthealthmonitor.tv.TvViewModelFactory

@Composable
fun TvCatalogScreen(
    onCardClick: (Int) -> Unit,
    viewModel: TvViewModel = viewModel(factory = TvViewModelFactory(LocalContext.current))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(48.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SmartHealth TV", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Surface(
                onClick = { viewModel.refresh() },
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color(0xFF1B4F8A),
                    focusedContainerColor = Color(0xFF2E7DD1)
                ),
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp))
            ) {
                Text(
                    if (state.isLoading) "Actualizando…" else "↺ Actualizar",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
        Spacer48()

        if (state.error != null) {
            Text("⚠ ${state.error}", color = Color(0xFFE53935))
            Spacer48()
        }

        Text("Estado Actual (3 dispositivos)", style = MaterialTheme.typography.titleMedium, color = Color.White)
        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.estadisticas, key = { it.dispositivo }) { estadistica ->
                // Cards informativas (promedio por dispositivo) — no navegan a detalle
                // porque no corresponden a una fila individual con id real en Neon.
                FcCardItem(lectura = estadistica, label = estadistica.dispositivo, onClick = {})
            }
        }

        Spacer48()

        Text("Historial Completo", style = MaterialTheme.typography.titleMedium, color = Color.White)
        val historialListState = rememberLazyListState()
        // La lista está ordenada más-reciente-primero; al llegar datos nuevos (refresh
        // o carga inicial) volvemos al inicio para que la última lectura sea visible
        // sin depender de que el usuario recuerde desplazarse manualmente.
        LaunchedEffect(state.lecturas) {
            if (state.lecturas.isNotEmpty()) historialListState.scrollToItem(0)
        }
        LazyRow(
            state = historialListState,
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.lecturas, key = { it.id }) { lectura ->
                FcCardItem(lectura = lectura, label = lectura.dispositivo, onClick = { onCardClick(lectura.id) })
            }
        }
    }
}

@Composable
private fun Spacer48() {
    Box(Modifier.height(32.dp))
}

@Composable
fun FcCardItem(lectura: LecturaFC, onClick: () -> Unit, label: String? = null) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(160.dp, 120.dp),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (lectura.esNormal) Color(0xFF1B4F8A) else Color(0xFFB3261E),
            focusedContainerColor = if (lectura.esNormal) Color(0xFF2E7DD1) else Color(0xFFE53935)
        ),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp))
    ) {
        Column(
            Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (label != null) {
                Text(label.uppercase(), color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("${lectura.bpm} bpm", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(lectura.hora, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
