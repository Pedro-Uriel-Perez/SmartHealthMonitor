package mx.edu.utng.smarthealthmonitor.tv

import android.app.Application
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import mx.edu.utng.smarthealthmonitor.tv.mqtt.MqttTvSubscriber

/**
 * Application del módulo tv.
 * Necesaria para inicializar el Repository (Room) antes de
 * que el TvViewModel intente leer datos.
 */
class TvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
        SmartHealthRepository.sembrarHistorialDeDemoSiVacio()
        MqttTvSubscriber.connect(this)
    }
}
