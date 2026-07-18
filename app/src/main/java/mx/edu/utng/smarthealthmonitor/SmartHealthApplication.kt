package mx.edu.utng.smarthealthmonitor

import android.app.Application
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import mx.edu.utng.smarthealthmonitor.mqtt.MqttAppService
import mx.edu.utng.smarthealthmonitor.sync.NeonSyncWorker

class SmartHealthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
        MqttAppService.connect(this)
        NeonSyncWorker.schedule(this)
    }
}
