package mx.edu.utng.smarthealthmonitor.data.models

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService : WearableListenerService() {
    companion object {
        const val PATH_FC    = "/smarthealthmonitor/fc"
        const val PATH_PASOS = "/smarthealthmonitor/pasos"
        private const val TAG = "WearListener"
    }
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = String(messageEvent.data)
        when (messageEvent.path) {
            PATH_FC    -> SmartHealthRepository.actualizarFC(data.toIntOrNull() ?: return)
            PATH_PASOS -> SmartHealthRepository.actualizarPasos(data.toIntOrNull() ?: return)
            else       -> Log.w(TAG, "Path desconocido: ${messageEvent.path}")
        }
    }
}