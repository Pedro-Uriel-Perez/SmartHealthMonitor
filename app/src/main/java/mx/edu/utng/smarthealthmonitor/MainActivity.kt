package mx.edu.utng.smarthealthmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.utng.smarthealthmonitor.navigation.SmartHealthNavGraph

// SmartHealthRepository.init(), MqttAppService.connect() y el sync periódico con Neon
// se inicializan en SmartHealthApplication.onCreate(), antes de que esta Activity exista.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthNavGraph()
        }
    }
}