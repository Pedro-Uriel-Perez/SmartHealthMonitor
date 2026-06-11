package mx.edu.utng.smarthealthmonitor.wear

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/**
 * Envía datos de frecuencia cardíaca al teléfono emparejado
 * a través de la Wearable Data Layer API (MessageClient).
 *
 * PATH_FC debe coincidir exactamente con el que escucha
 * WearListenerService en el módulo :app.
 */
object WearDataSender {

    private const val PATH_FC    = "/smarthealthmonitor/fc"
    private const val PATH_PASOS = "/smarthealthmonitor/pasos"
    private const val TAG        = "WearDataSender"

    /**
     * Envía el valor de FC (en bpm) a todos los nodos conectados.
     * Debe llamarse desde una coroutine.
     */
    /** Devuelve true si se envió a al menos un nodo. */
    suspend fun sendHeartRate(context: Context, bpm: Int): Boolean =
        sendMessage(context, PATH_FC, bpm.toString())

    suspend fun sendSteps(context: Context, steps: Int): Boolean =
        sendMessage(context, PATH_PASOS, steps.toString())

    private suspend fun sendMessage(context: Context, path: String, payload: String): Boolean {
        return try {
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()
            if (nodes.isEmpty()) {
                Log.w(TAG, "No hay nodos conectados para path=$path")
                return false
            }
            val messageClient = Wearable.getMessageClient(context)
            val data = payload.toByteArray(Charsets.UTF_8)
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, path, data).await()
                Log.d(TAG, "Mensaje enviado a ${node.displayName}: $payload (path=$path)")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando mensaje (path=$path, payload=$payload)", e)
            false
        }
    }
}
