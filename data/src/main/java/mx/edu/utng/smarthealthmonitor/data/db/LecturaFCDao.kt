package mx.edu.utng.smarthealthmonitor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaFCDao {

    @Insert
    suspend fun insertar(lectura: LecturaFC)

    @Query("SELECT * FROM lecturas_fc ORDER BY id DESC LIMIT 50")
    fun obtenerTodas(): Flow<List<LecturaFC>>
}

