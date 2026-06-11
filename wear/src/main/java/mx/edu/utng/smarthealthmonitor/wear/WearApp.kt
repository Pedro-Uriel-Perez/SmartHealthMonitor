package mx.edu.utng.smarthealthmonitor.wear

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Rango de frecuencia cardíaca para el slider
private const val FC_MIN = 40f
private const val FC_MAX = 180f
private const val FC_STEPS = 140  // pasos de 1 en 1 (40..180)

/**
 * Composable principal del reloj Wear OS.
 *
 * Muestra:
 *  • Valor de FC actual (grande, en el centro)
 *  • Indicador de color (verde = normal 60-100, rojo = elevado >100)
 *  • InlineSlider para ajustar la FC
 *  • Botón "ENVIAR" que transmite el dato al teléfono via MessageClient
 */
@Composable
fun WearApp(context: Context) {
    // Valor actual del slider (0..1 mapeado a FC_MIN..FC_MAX)
    var sliderPosition by remember { mutableFloatStateOf(0.24f) }   // ~78 bpm por defecto
    var enviando by remember { mutableStateOf(false) }
    var ultimoEnviado by remember { mutableIntStateOf(-1) }
    var errorEnvio by remember { mutableStateOf(false) }

    // FC calculada desde la posición del slider
    val bpmActual = (FC_MIN + sliderPosition * (FC_MAX - FC_MIN)).toInt()
    val esNormal = bpmActual in 60..100
    val colorFC = if (esNormal) Color(0xFF4CAF50) else Color(0xFFF44336)

    Scaffold(
        timeText = {
            TimeText()
        },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título
            Text(
                text = "Frecuencia Cardíaca",
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Valor BPM grande
            Text(
                text = "$bpmActual",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = colorFC,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Unidad
            Text(
                text = "bpm",
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Slider de FC
            InlineSlider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                steps = FC_STEPS,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                decreaseIcon = {
                    Icon(
                        imageVector = InlineSliderDefaults.Decrease,
                        contentDescription = "Disminuir FC"
                    )
                },
                increaseIcon = {
                    Icon(
                        imageVector = InlineSliderDefaults.Increase,
                        contentDescription = "Aumentar FC"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Enviar
            Chip(
                onClick = {
                    if (!enviando) {
                        enviando = true
                        errorEnvio = false
                        val bpmAEnviar = bpmActual
                        CoroutineScope(Dispatchers.IO).launch {
                            val ok = WearDataSender.sendHeartRate(context, bpmAEnviar)
                            if (ok) ultimoEnviado = bpmAEnviar else errorEnvio = true
                            enviando = false
                        }
                    }
                },
                label = {
                    Text(
                        text = if (enviando) "Enviando…" else "ENVIAR AL TELÉFONO",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = ChipDefaults.primaryChipColors(
                    backgroundColor = if (enviando)
                        MaterialTheme.colors.surface
                    else
                        colorFC
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Feedback del último envío
            if (errorEnvio) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sin conexión con el teléfono",
                    style = MaterialTheme.typography.caption2,
                    color = Color(0xFFF44336),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (ultimoEnviado > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Enviado: $ultimoEnviado bpm",
                    style = MaterialTheme.typography.caption2,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
