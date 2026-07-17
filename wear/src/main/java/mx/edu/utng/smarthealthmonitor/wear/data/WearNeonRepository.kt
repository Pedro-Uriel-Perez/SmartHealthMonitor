package mx.edu.utng.smarthealthmonitor.wear.data

import android.util.Log
import mx.edu.utng.smarthealthmonitor.data.remote.LecturaFcDto
import mx.edu.utng.smarthealthmonitor.data.remote.NeonClient
import mx.edu.utng.smarthealthmonitor.data.remote.NeonRequest
import mx.edu.utng.smarthealthmonitor.data.remote.paramsOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "WEAR_DB"

/** El reloj solo PUBLICA lecturas directo a Neon — sin Room local (para ahorrar memoria). */
object WearNeonRepository {

    private val horaFormato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    suspend fun publicarLectura(bpm: Int, estado: String) {
        if (!NeonClient.isConfigured) {
            Log.w(TAG, "⚠️ Neon sin configurar — agrega tus credenciales en local.properties")
            return
        }
        val hora = horaFormato.format(Date())
        NeonClient.api.executeQuery(
            auth = NeonClient.AUTH_HEADER,
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = "INSERT INTO lecturas_fc (bpm, estado, dispositivo, hora) VALUES ($1, $2, $3, $4)",
                params = paramsOf(bpm, estado, "wear", hora)
            )
        )
        Log.d(TAG, "⌚ FC enviada a Neon: $bpm bpm")
    }

    /** Últimas 5 lecturas del reloj, leídas directo de Neon. */
    suspend fun obtenerUltimasLecturas(): List<LecturaFcDto> {
        if (!NeonClient.isConfigured) return emptyList()
        return NeonClient.api.executeQuery(
            auth = NeonClient.AUTH_HEADER,
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = "SELECT * FROM lecturas_fc WHERE dispositivo='wear' ORDER BY created_at DESC LIMIT 5"
            )
        ).rows
    }
}
