package mx.edu.utng.smarthealthmonitor.data.models

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.smarthealthmonitor.data.db.MIGRATION_1_2
import mx.edu.utng.smarthealthmonitor.data.db.SmartHealthDB
import mx.edu.utng.smarthealthmonitor.data.remote.NeonClient
import mx.edu.utng.smarthealthmonitor.data.remote.NeonRequest
import mx.edu.utng.smarthealthmonitor.data.remote.paramsOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SmartHealthRepository {

    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    private lateinit var db: SmartHealthDB
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val horaFormato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun init(context: Context) {
        if (::db.isInitialized) return
        db = Room.databaseBuilder(
            context.applicationContext,
            SmartHealthDB::class.java,
            "smarthealth.db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    /** Cuántas lecturas locales todavía no llegan a Neon — para un ícono/badge en la UI. */
    fun contarPendientes(): Flow<Int> = db.lecturaFCDao().contarPendientes()

    /**
     * Guarda en Room primero (garantiza persistencia local, nunca falla),
     * luego intenta sincronizar con Neon en background (offline-first).
     */
    fun actualizarFC(bpm: Int, dispositivo: String = "app") {
        _fcFlow.value = bpm
        if (::db.isInitialized) {
            scope.launch {
                val lectura = LecturaFC(valorBpm = bpm, hora = horaFormato.format(Date()), dispositivo = dispositivo)
                val id = db.lecturaFCDao().insertar(lectura)
                try {
                    sincronizarHaciaNeon(lectura)
                    db.lecturaFCDao().marcarSincronizado(id)
                } catch (e: Exception) {
                    // Sin internet o Neon no configurado: queda pendiente para el próximo sync.
                    android.util.Log.w("SYNC", "Pendiente de sync: ${e.message}")
                }
            }
        }
    }

    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    fun obtenerHistorial(): Flow<List<LecturaFC>> = db.lecturaFCDao().obtenerTodas()

    // Cada app (tel, TV) tiene su propia base de datos privada, así que la TV
    // no ve las lecturas simuladas en el teléfono. Sembramos datos de ejemplo
    // solo si el historial local está vacío, para poder ver la fila poblada.
    fun sembrarHistorialDeDemoSiVacio() {
        if (!::db.isInitialized) return
        scope.launch {
            if (db.lecturaFCDao().obtenerTodas().first().isEmpty()) {
                listOf(88, 92, 76, 105, 81, 99, 70).forEach { bpm ->
                    db.lecturaFCDao().insertar(LecturaFC(valorBpm = bpm, hora = horaFormato.format(Date())))
                }
            }
        }
    }

    // ── PUSH: Room → Neon ──────────────────────────────────
    private suspend fun sincronizarHaciaNeon(lectura: LecturaFC) {
        if (!NeonClient.isConfigured) error("Neon sin configurar")
        NeonClient.api.executeQuery(
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = """INSERT INTO lecturas_fc (bpm, estado, dispositivo, hora)
                    VALUES ($1, $2, $3, $4) RETURNING *""".trimIndent(),
                params = paramsOf(lectura.valorBpm, lectura.estado, lectura.dispositivo, lectura.hora)
            )
        )
    }

    // ── PULL: Neon → Room ──────────────────────────────────
    /** Descarga los registros más recientes de Neon y actualiza Room si hay datos nuevos. */
    suspend fun sincronizarDesdeNeon(limite: Int = 50) {
        if (!::db.isInitialized || !NeonClient.isConfigured) return
        val response = NeonClient.api.executeQuery(
            connStr = NeonClient.CONN_STRING,
            request = NeonRequest(
                query = """SELECT id, bpm, estado, dispositivo, hora FROM lecturas_fc
                    ORDER BY created_at DESC LIMIT $1""".trimIndent(),
                params = paramsOf(limite)
            )
        )
        response.rows.forEach { dto ->
            db.lecturaFCDao().upsert(
                LecturaFC(
                    id = dto.id,
                    valorBpm = dto.bpm,
                    hora = dto.hora,
                    dispositivo = dto.dispositivo,
                    sincronizado = true
                )
            )
        }
        android.util.Log.d("SYNC", "✅ ${response.rowCount} registros descargados de Neon")
    }

    /** Reintenta subir a Neon las lecturas locales que quedaron pendientes (sin internet, etc). */
    suspend fun enviarPendientes() {
        if (!::db.isInitialized || !NeonClient.isConfigured) return
        db.lecturaFCDao().obtenerNoSincronizados().forEach { lectura ->
            try {
                sincronizarHaciaNeon(lectura)
                db.lecturaFCDao().marcarSincronizado(lectura.id.toLong())
                android.util.Log.d("SYNC", "✅ Sincronizado pendiente id=${lectura.id}")
            } catch (e: Exception) {
                android.util.Log.w("SYNC", "Aún sin internet: ${e.message}")
            }
        }
    }
}
