package mx.edu.utng.smarthealthmonitor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaFCDao {

    @Insert
    suspend fun insertar(lectura: LecturaFC): Long

    @Query("SELECT * FROM lecturas_fc ORDER BY id DESC LIMIT 50")
    fun obtenerTodas(): Flow<List<LecturaFC>>

    // ── Sync con Neon (PostgreSQL) ──────────────────────────

    /** Upsert: inserta o reemplaza si el id ya existe (usado al descargar de Neon). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lectura: LecturaFC)

    /** Registros que aún no llegaron a Neon. */
    @Query("SELECT * FROM lecturas_fc WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizados(): List<LecturaFC>

    /** Marca un registro como sincronizado con Neon. */
    @Query("UPDATE lecturas_fc SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Long)

    /** Cuántos registros faltan por sincronizar (para mostrar un ícono/badge en UI). */
    @Query("SELECT COUNT(*) FROM lecturas_fc WHERE sincronizado = 0")
    fun contarPendientes(): Flow<Int>
}

