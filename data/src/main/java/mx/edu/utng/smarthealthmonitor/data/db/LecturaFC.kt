package mx.edu.utng.smarthealthmonitor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valorBpm: Int,
    val hora: String
) {
    // Considera "normal" una FC en reposo entre 60 y 100 bpm.
    val esNormal: Boolean
        get() = valorBpm in 60..100
}
