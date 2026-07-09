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
import kotlinx.coroutines.launch
import mx.edu.utng.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.smarthealthmonitor.data.db.SmartHealthDB
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
        ).build()
    }

    fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm
        if (::db.isInitialized) {
            scope.launch {
                db.lecturaFCDao().insertar(LecturaFC(valorBpm = bpm, hora = horaFormato.format(Date())))
            }
        }
    }

    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    fun obtenerHistorial(): Flow<List<LecturaFC>> = db.lecturaFCDao().obtenerTodas()
}