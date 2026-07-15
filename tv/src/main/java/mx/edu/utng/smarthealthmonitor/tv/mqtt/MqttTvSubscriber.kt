package mx.edu.utng.smarthealthmonitor.tv.mqtt

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import mx.edu.utng.smarthealthmonitor.data.mqtt.MqttConfig
import mx.edu.utng.smarthealthmonitor.data.mqtt.TvMessage
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

private const val TAG = "MQTT_TV"

/**
 * La TV se suscribe al topic .../tv y alimenta el mismo Repository que ya
 * usa TvViewModel (fcFlow + Room), así que la UI se actualiza reactivamente
 * sin tocar TvViewModel/TvUiState.
 */
object MqttTvSubscriber {

    private var client: MqttAsyncClient? = null

    fun connect(context: Context) {
        if (!MqttConfig.isConfigured) {
            Log.w(TAG, "⚠️ MqttConfig sin configurar — agrega tus credenciales en local.properties")
            return
        }
        if (client?.isConnected == true) return

        client = MqttAsyncClient(MqttConfig.BROKER_URL, MqttConfig.CLIENT_TV, MemoryPersistence())

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, msg: MqttMessage) {
                if (topic == MqttConfig.TOPIC_TV) {
                    val tvMsg = Json.decodeFromString<TvMessage>(String(msg.payload))
                    SmartHealthRepository.actualizarFC(tvMsg.bpm)
                    Log.d(TAG, "📺 Recibido: ${tvMsg.bpm} bpm")
                }
            }
            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "Conexión perdida", cause)
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                Log.d(TAG, "✅ Conectado a HiveMQ Cloud")
                client?.subscribe(MqttConfig.TOPIC_TV, MqttConfig.QOS, null, object : IMqttActionListener {
                    override fun onSuccess(subToken: IMqttToken?) {
                        Log.d(TAG, "✅ TV suscrita a ${MqttConfig.TOPIC_TV}")
                    }
                    override fun onFailure(subToken: IMqttToken?, ex: Throwable?) {
                        Log.e(TAG, "❌ Falló la suscripción", ex)
                    }
                })
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                Log.e(TAG, "❌ Error de conexión", ex)
            }
        })
    }

    fun disconnect() {
        client?.disconnect()
    }
}
