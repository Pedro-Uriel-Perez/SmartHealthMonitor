package mx.edu.utng.smarthealthmonitor.tv.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        Text("SmartHealth TV", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        Spacer48()

        Text("Estado actual", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Box(Modifier.padding(top = 16.dp)) {
            // Card informativa con el FC en vivo — no navega a detalle porque
            // no corresponde a una fila persistida en Room.
            FcCardItem(
                lectura = LecturaFC(id = -1, valorBpm = state.fc, hora = "Ahora"),
                onClick = {}
            )
        }

        Spacer48()

        Text("Historial FC", style = MaterialTheme.typography.titleMedium, color = Color.White)
        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.lecturas, key = { it.id }) { lectura ->
                FcCardItem(lectura = lectura, onClick = { onCardClick(lectura.id) })
            }
        }
    }
}

@Composable
private fun Spacer48() {
    Box(Modifier.height(32.dp))
}

@Composable
fun FcCardItem(lectura: LecturaFC, onClick: () -> Unit) {
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
            Text("${lectura.bpm} bpm", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(lectura.hora, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
