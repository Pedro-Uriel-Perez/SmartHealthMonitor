package mx.edu.utng.smarthealthmonitor.wear.presentation

data class WearLecturaFC(
    val id: Int,
    val valorBpm: Int,
    val hora: String,
    val esNormal: Boolean = valorBpm in 60..100
)
