package mx.edu.utng.smarthealthmonitor.mqtt

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mx.edu.utng.smarthealthmonitor.data.models.SmartHealthRepository
import mx.edu.utng.smarthealthmonitor.data.mqtt.FcMessage
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MQTT_APP"

/**
 * El teléfono recibe la FC del reloj (topic .../fc), actualiza el
 * Repository local (Room + StateFlow) y reenvía un mensaje enriquecido
 * al topic .../tv para que Android TV lo reciba, sin necesitar
 * emparejamiento BLE entre los tres dispositivos.
 */
object MqttAppService {

    private var client: MqttAsyncClient? = null
    private val horaFormato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun connect(context: Context) {
        if (!MqttConfig.isConfigured) {
            Log.w(TAG, "⚠️ MqttConfig sin configurar — agrega tus credenciales en local.properties")
            return
        }
        if (client?.isConnected == true) return

        client = MqttAsyncClient(MqttConfig.BROKER_URL, MqttConfig.CLIENT_APP, MemoryPersistence())
        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, msg: MqttMessage) {
                if (topic == MqttConfig.TOPIC_FC) handleFcMessage(msg)
            }
            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "Conexión perdida", cause)
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                Log.d(TAG, "✅ Conectado a HiveMQ Cloud")
                client?.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS, null, object : IMqttActionListener {
                    override fun onSuccess(subToken: IMqttToken?) {
                        Log.d(TAG, "✅ Suscrito a ${MqttConfig.TOPIC_FC}")
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

    private fun handleFcMessage(msg: MqttMessage) {
        val fcMsg = Json.decodeFromString<FcMessage>(String(msg.payload))

        // 1. Actualizar Repository local (StateFlow reactivo + Room)
        SmartHealthRepository.actualizarFC(fcMsg.bpm)

        // 2. Re-publicar al topic TV con formato enriquecido
        val hora = horaFormato.format(Date())
        val tvMsg = TvMessage(bpm = fcMsg.bpm, estado = fcMsg.estado, hora = hora)
        val tvPayload = Json.encodeToString(tvMsg).toByteArray()
        val tvMqtt = MqttMessage(tvPayload).apply {
            qos = MqttConfig.QOS
            isRetained = true
        }
        client?.publish(MqttConfig.TOPIC_TV, tvMqtt)
        Log.d(TAG, "🔁 Re-publicado al TV: ${fcMsg.bpm} bpm")
    }

    fun disconnect() {
        client?.disconnect()
    }
}
