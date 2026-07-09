package mx.edu.utng.smarthealthmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import mx.edu.utng.smarthealthmonitor.navigation.SmartHealthNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmartHealthRepository.init(this)
        setContent {
            SmartHealthNavGraph()
        }
    }
}