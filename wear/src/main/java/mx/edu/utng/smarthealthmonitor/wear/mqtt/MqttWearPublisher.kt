package mx.edu.utng.smarthealthmonitor.wear.mqtt

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mx.edu.utng.smarthealthmonitor.data.mqtt.FcMessage
import mx.edu.utng.smarthealthmonitor.data.mqtt.MqttConfig
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

private const val TAG = "MQTT_WEAR"

/** Publica la FC del reloj a HiveMQ Cloud. Objeto único, igual que SmartHealthRepository. */
object MqttWearPublisher {

    private var client: MqttAsyncClient? = null

    fun connect(context: Context) {
        if (!MqttConfig.isConfigured) {
            Log.w(TAG, "⚠️ MqttConfig sin configurar — agrega tus credenciales en local.properties")
            return
        }
        if (client?.isConnected == true) return

        client = MqttAsyncClient(MqttConfig.BROKER_URL, MqttConfig.CLIENT_WEAR, MemoryPersistence())
        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            connectionTimeout = 30
            keepAliveInterval = 60
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "✅ Conectado a HiveMQ Cloud")
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                Log.e(TAG, "❌ Error de conexión", ex)
            }
        })
    }

    /** Publicar FC al topic MQTT */
    fun publishFC(bpm: Int, estado: String) {
        if (client?.isConnected != true) return

        val message = FcMessage(bpm = bpm, estado = estado)
        val payload = Json.encodeToString(message).toByteArray()
        val mqttMessage = MqttMessage(payload).apply {
            qos = MqttConfig.QOS
            isRetained = true // el TV verá el último valor al conectarse
        }
        client?.publish(MqttConfig.TOPIC_FC, mqttMessage, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                Log.d(TAG, "📤 Publicado (confirmado por el broker): $bpm bpm → ${MqttConfig.TOPIC_FC}")
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                Log.e(TAG, "❌ Falló el publish", ex)
            }
        })
    }

    fun disconnect() {
        client?.disconnect()
    }
}
