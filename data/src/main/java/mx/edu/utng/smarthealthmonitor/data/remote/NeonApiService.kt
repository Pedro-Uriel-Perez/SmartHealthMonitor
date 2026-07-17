package mx.edu.utng.smarthealthmonitor.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/** Request genérico para la Neon HTTP API */
@Serializable
data class NeonRequest(
    val query: String,
    val params: List<JsonElement> = emptyList()
)

/** Construye la lista de params conservando el tipo real (Int/String/Boolean) como JSON. */
fun paramsOf(vararg values: Any?): List<JsonElement> = values.map {
    when (it) {
        null -> JsonNull
        is Int -> JsonPrimitive(it)
        is Long -> JsonPrimitive(it)
        is Boolean -> JsonPrimitive(it)
        else -> JsonPrimitive(it.toString())
    }
}

/** Response de la Neon HTTP API */
@Serializable
data class NeonResponse<T>(
    val rows: List<T> = emptyList(),
    val rowCount: Int = 0,
    val command: String = ""
)

/** DTO de lectura FC (mapea fila de PostgreSQL) */
@Serializable
data class LecturaFcDto(
    val id: Int = 0,
    val bpm: Int,
    val estado: String,
    val dispositivo: String,
    val hora: String,
    val fecha: String = "",
    val created_at: String = ""
)

/** Convierte una fila de Neon al mismo tipo que usa Room, para reusar la UI existente. */
fun LecturaFcDto.toLecturaFC(): LecturaFC = LecturaFC(
    id = id,
    valorBpm = bpm,
    hora = hora,
    dispositivo = dispositivo,
    sincronizado = true
)

/** Interfaz Retrofit para la Neon HTTP API */
interface NeonApiService {
    @POST("sql")
    suspend fun executeQuery(
        @Header("Authorization") auth: String,
        @Header("Neon-Connection-String") connStr: String,
        @Body request: NeonRequest
    ): NeonResponse<LecturaFcDto>
}
