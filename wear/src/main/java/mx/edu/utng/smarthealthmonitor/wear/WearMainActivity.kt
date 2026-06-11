package mx.edu.utng.smarthealthmonitor.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.utng.smarthealthmonitor.wear.presentation.SmartHealthWearNavGraph
import mx.edu.utng.smarthealthmonitor.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthWearTheme {
                SmartHealthWearNavGraph()
            }
        }
    }
}
