package mx.edu.utng.smarthealthmonitor.tv.data

import mx.edu.utng.smarthealthmonitor.data.remote.LecturaFcDto
import mx.edu.utng.smarthealthmonitor.data.remote.NeonClient
import mx.edu.utng.smarthealthmonitor.data.remote.NeonRequest
import mx.edu.utng.smarthealthmonitor.data.remote.paramsOf

/** La TV solo LEE: historial combinado de los 3 dispositivos, directo de Neon. */
object TvNeonRepository {

    /** Últimas [limite] lecturas de los 3 dispositivos combinados. */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> {
        if (!NeonClient.isConfigured) return emptyList()
        return NeonClient.api.executeQuery(
            auth = NeonClient.AUTH_HEADER,
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = """SELECT id, bpm, estado, dispositivo, hora, created_at
                    FROM lecturas_fc
                    ORDER BY created_at DESC
                    LIMIT $1""".trimIndent(),
                params = paramsOf(limite)
            )
        ).rows
    }

    /** Promedio de FC por dispositivo (wear/app/tv) — para la fila "Estado Actual". */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> {
        if (!NeonClient.isConfigured) return emptyList()
        return NeonClient.api.executeQuery(
            auth = NeonClient.AUTH_HEADER,
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = """SELECT dispositivo,
                    ROUND(AVG(bpm)) AS bpm,
                    'Promedio' AS estado,
                    MAX(hora) AS hora
                    FROM lecturas_fc
                    GROUP BY dispositivo""".trimIndent()
            )
        ).rows
    }
}
